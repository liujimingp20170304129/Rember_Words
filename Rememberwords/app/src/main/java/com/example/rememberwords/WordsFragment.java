package com.example.rememberwords;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.android.material.snackbar.Snackbar;

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
    private List<Word> allWords;
    private boolean undoAction;
    private DividerItemDecoration dividerItemDecoration;

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
            //切换视图功能
            case R.id.switch_views:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(viewType){
                    recyclerView.setAdapter(layoutAdapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    //false表示当前没有使用卡片View
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                }else {
                    recyclerView.setAdapter(layoutAdapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
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
                filterWords.removeObservers(getViewLifecycleOwner());
                filterWords = wordViewModel.findWordsWithPatten(patten);
                filterWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = layoutAdapter1.getItemCount();
                            allWords = words;
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

        //添加边线
        dividerItemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);

        if(viewType){
            recyclerView.setAdapter(layoutAdapter2);
        }else {
            recyclerView.setAdapter(layoutAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

//        recyclerView.setAdapter(layoutAdapter1);
        //观察数据是否发生变化变化时刷新
        filterWords = wordViewModel.getAllWordsLive();
        filterWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = layoutAdapter1.getItemCount();
                    allWords = words;
                if (temp != words.size()){
                    if (temp < words.size() && !undoAction) {
                        recyclerView.smoothScrollBy(0, -200);
                    }
                    undoAction = false;
                    //
                    layoutAdapter1.submitList(words);
                    layoutAdapter2.submitList(words);
//                    layoutAdapter1.notifyDataSetChanged();
//                    layoutAdapter2.notifyDataSetChanged();
                }
            }
        });

        //滑动删除列表中的单词
        //ItemTouchHelper.START |  ItemTouchHelper.END 允许向左或向右滑动
        //ItemTouchHelper.UP | ItemTouchHelper.DOWN 允许向上或向下滑动
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START ) {
            //设置上下滚动换位置，由于右bug没解决
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                Word wordFrom = allWords.get(viewHolder.getAdapterPosition());
//                Word wordTo = allWords.get(target.getAdapterPosition());
//                int idTemp = wordFrom.getId();
//                wordFrom.setId(wordTo.getId());
//                wordTo.setId(idTemp);
//                wordViewModel.updateWords(wordFrom,wordTo);
//                layoutAdapter1.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
//                layoutAdapter2.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final Word wordDelete = allWords.get(viewHolder.getAdapterPosition());
                    wordViewModel.deleteWords(wordDelete);
                Snackbar.make(requireActivity().findViewById(R.id.wordsFramentView),"删除了一个单词",Snackbar.LENGTH_SHORT)
                .setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoAction = true;
                        wordViewModel.insertWords(wordDelete);
                    }
                }).show();
            }
            Drawable icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconLeft,iconRight,iconTop,iconBottom;
                int backTop,backBottom,backLeft,backRight;
                backTop= itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) /2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX>0){
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                } else if (dX<0){
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconRight = itemView.getLeft() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);

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
