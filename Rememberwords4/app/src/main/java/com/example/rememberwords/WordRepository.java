package com.example.rememberwords;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private LiveData<List<Word>>allWordsLive;
    private WordDao wordDao;
    public WordRepository(Context context) {
//把WordDatabase作为参数传进来
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase.getWordDao();
        allWordsLive = wordDao.getAllWordsLive();
    }

    //操作数据的接口
    void insertWords(Word... words){
        new InsertAsyncTask(wordDao).execute(words);
    }
    void updateWords(Word... words){
        new UpdateAsyncTask(wordDao).execute(words);
    }
    void deleteWords(Word... words){
        new DeleteAsyncTask(wordDao).execute(words);
    }
    void deleteallWords(){
        new DeleteAllAsyncTask(wordDao).execute();
    }


     LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }
    LiveData<List<Word>>findWordsWithPatten(String patten){
        return wordDao.findWordsWithPatten("%" + patten + "%");
    }


    //对数据库的操作
    //添加数据
    static class InsertAsyncTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        InsertAsyncTask(WordDao wordDao){
            this.wordDao = wordDao;
        }

        //任务创建时呼叫
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    //修改数据
    static class UpdateAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        UpdateAsyncTask(WordDao wordDao){
            this.wordDao = wordDao;
        }

        //任务创建时呼叫
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }

    //删除数据
    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        DeleteAsyncTask(WordDao wordDao){
            this.wordDao = wordDao;
        }

        //任务创建时呼叫
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    //清空数据
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        DeleteAllAsyncTask(WordDao wordDao){
            this.wordDao = wordDao;
        }

        //任务创建时呼叫
        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
