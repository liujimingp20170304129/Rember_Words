package com.example.rememberwords;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private Button mBtnSubmit;
    private EditText editTextEnglish,editTextChinese;
    private WordViewModel wordViewModel;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = requireActivity();
        wordViewModel = ViewModelProviders.of(activity).get(WordViewModel.class);
        mBtnSubmit = activity.findViewById(R.id.btn_submit);
        editTextEnglish =activity.findViewById(R.id.ed_english);
        editTextChinese = activity.findViewById(R.id.ed_chinese);

        //进入界面时按钮变为灰色
        mBtnSubmit.setEnabled(false);
        editTextEnglish.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editTextEnglish,0);

        //edittext监听器
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            //当文本内容发生改变是调用
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String english = editTextEnglish.getText().toString().trim();
                    String chinese = editTextChinese.getText().toString().trim();
                    mBtnSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextEnglish.addTextChangedListener(textWatcher);
        editTextChinese.addTextChangedListener(textWatcher);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                Word word  = new Word(english,chinese);
                wordViewModel.insertWords(word);
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
    }
}
