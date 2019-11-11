package com.example.rememberwords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button mBtnInsert, mBtnClear;
    WordViewModel wordViewModel;
    RecyclerView recyclerView;
    Switch aSwitch;
    LayoutAdapter layoutAdapter1,layoutAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        layoutAdapter1 = new LayoutAdapter(false);
        layoutAdapter2 = new LayoutAdapter(true);
        //将recyclerview设置成线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(layoutAdapter1);
        aSwitch =findViewById(R.id.switch1);

        //卡片开关监听器
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recyclerView.setAdapter(layoutAdapter2);
                }else {
                    recyclerView.setAdapter(layoutAdapter1);
                }
            }
        });

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        //LiveData监听数据变化
        wordViewModel.getAllWordsLive().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                layoutAdapter1.setAllWords(words);
                layoutAdapter2.setAllWords(words);
                //刷新视图
                layoutAdapter1.notifyDataSetChanged();
                layoutAdapter2.notifyDataSetChanged();
            }
        });

        mBtnInsert = findViewById(R.id.btn_insert);

        mBtnClear = findViewById(R.id.btn_clear);


        //添加数据
        mBtnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] english = {
                        "Hello",
                        "World",
                        "Android",
                        "Google",
                        "Studio",
                        "Project",
                        "Database",
                        "Recycler",
                        "View",
                        "String",
                        "Value",
                        "Interger",
                };
                String[] chinese = {
                        "你好",
                        "世界",
                        "安卓系统",
                        "谷歌公司",
                        "工作室",
                        "项目",
                        "数据库",
                        "回收站",
                        "视图",
                        "字符串",
                        "价值",
                        "整数类型",
                };
                for (int i = 0;i<english.length;i++){
                    wordViewModel.insertWords(new Word(english[i],chinese[i]));
                }
                Word word1 = new Word("Hello", "你好！");
                Word word2 = new Word("World", "世界！");
                wordViewModel.insertWords(word1,word2);
            }
        });

        //清空数据
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordViewModel.deleteallWords();
            }
        });


    }

}
