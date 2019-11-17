package com.example.rememberwords;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {

    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private LayoutAdapter layoutAdapter1,layoutAdapter2;
    private FloatingActionButton floatingActionButton;
    private LiveData<List<Word>> filterWords;
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";

    public WordsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    //菜单下按钮功能
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear_data:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.deleteallWords();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //完成这些操作后弹出对话框
                builder.create();
                builder.show();
            break;
            case R.id.switch_views:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(viewType){
                    recyclerView.setAdapter(layoutAdapter1);
                    //false表示当前没有使用卡片View
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                }else {
                    recyclerView.setAdapter(layoutAdapter2);
                    //false表示当前没有使用卡片View
                    editor.putBoolean(IS_USING_CARD_VIEW,true);
                }
                editor.apply();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //顶部导航栏
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(750);
        //监听内容发生改变
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击完确定后调用
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            //内容改变时调用
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("mylog","onQueryTextChange"+ newText);
                String patten = newText.trim();
                //要删除原有的观察
                filterWords.removeObservers(requireActivity());
                filterWords = wordViewModel.findWordsWithPatten(patten);
                filterWords.observe(requireActivity(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = layoutAdapter1.getItemCount();

                        if (temp != words.size()){
                            layoutAdapter1.submitList(words);
                            layoutAdapter2.submitList(words);
//                            layoutAdapter1.notifyDataSetChanged();
//                            layoutAdapter2.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
        recyclerView = requireView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        layoutAdapter1 = new LayoutAdapter(false,wordViewModel);
        layoutAdapter2 = new LayoutAdapter(true,wordViewModel);
        //刷新列表前面的序号
        recyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firsPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i =  firsPosition;i<=lastPosition; i++){
                        LayoutAdapter.LayoutViewHolder holder = (LayoutAdapter.LayoutViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder != null) {
                            holder.TvNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(viewType){
            recyclerView.setAdapter(layoutAdapter1);
        }else {
            recyclerView.setAdapter(layoutAdapter2);
        }

//        recyclerView.setAdapter(layoutAdapter1);
        //观察数据是否发生变化变化时刷新
        filterWords = wordViewModel.getAllWordsLive();
        filterWords.observe(requireActivity(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = layoutAdapter1.getItemCount();

                if (temp != words.size()){
                    recyclerView.smoothScrollBy(0,-200);
                    layoutAdapter1.submitList(words);
                    layoutAdapter2.submitList(words);
//                    layoutAdapter1.notifyDataSetChanged();
//                    layoutAdapter2.notifyDataSetChanged();
                }
            }
        });

        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });

    }

    @Override
    public void onResume() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),0);
        super.onResume();
    }



}
