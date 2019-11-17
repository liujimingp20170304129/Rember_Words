package com.example.rememberwords;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao        //访问数据库接口
public interface WordDao {
    @Insert
    void insertWords(Word... words);

    @Update
    void updateWords(Word... words);

    @Delete
    void deleteWords(Word... words);

    @Query("delete from Word")
    void deleteAllWords();

    @Query("select * from word order by id desc")
    //LiveData观察数据发生变化
    LiveData<List<Word>>getAllWordsLive();

    //模糊查询
    @Query("select * from word where english_word like :patten order by id desc")
    LiveData<List<Word>>findWordsWithPatten(String patten);

}
