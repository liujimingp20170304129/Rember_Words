package com.example.rememberwords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    WordDatabase wordDatabase;
    WordDao wordDao;
    TextView textView;
    Button mBtnInsert, mBtnUpdate, mBtnClear, mBtnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取wordDatabase对象
        wordDatabase = Room.databaseBuilder(this, WordDatabase.class, "word_database")
                .allowMainThreadQueries()
                .build();
        wordDao = wordDatabase.getWordDao();
        textView = findViewById(R.id.textView);
        updateView();

        mBtnInsert = findViewById(R.id.btn_insert);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnClear = findViewById(R.id.btn_clear);
        mBtnDelete = findViewById(R.id.btn_delete);

        //添加数据
        mBtnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word1 = new Word("Hello","你好！");
                Word word2 = new Word("World","世界！");
                wordDao.insertWords(word1,word2);
                updateView();
            }
        });

        //清空数据
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordDao.deleteAllWords();
                updateView();
            }
        });

        //修改数据
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word("Hi","你好aaaaa！");
                word.setId(20);
                wordDao.updateWords(word);
                updateView();
            }
        });

        //删除数据
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word("Hi","你好aaaaa！");
                word.setId(21);
                wordDao.deleteWords(word);
                updateView();
            }
        });
    }

    //查询数据并显示
    void updateView() {
        List<Word> list = wordDao.getAllWords();
        String text = "";
        for (int i = 0; i < list.size(); i++) {
            Word word = list.get(i);
            text += word.getId() + ":" + word.getWord() + "=" + word.getChineseMeaning() + "\n";
        }
        textView.setText(text);
    }
}
