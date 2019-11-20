package com.example.rememberwords;

import android.app.Application;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordViewModel extends AndroidViewModel {

    private WordDao wordDao;
    private WordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        //把WordDatabase作为参数传进来
       wordRepository = new WordRepository(application);

    }

    LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }
    LiveData<List<Word>>findWordsWithPatten(String patten){
        return wordRepository.findWordsWithPatten(patten);
    }

    //操作数据的接口
    void insertWords(Word... words) {
       wordRepository.insertWords(words);
    }

    void updateWords(Word... words) {
        wordRepository.updateWords(words);
    }

    void deleteWords(Word... words) {
        wordRepository.deleteWords(words);
    }

    void deleteallWords() {
        wordRepository.deleteallWords();
    }


}
