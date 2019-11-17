package com.example.rememberwords;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//singleton使这个类只允许生成一个实例
@Database(entities = {Word.class},version = 5,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;
    //synchronized保证多个线程下客户端同时申请INSTANCE不会发生冲突以排队的方式保证只生成一个INSTANCE
    static synchronized WordDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
//                    .fallbackToDestructiveMigration()//破坏式的迁移（每次运行清空现有数据）
                    .addMigrations(MIGRATION_4_5)
                    .build();
        }
        return INSTANCE;
    }
    public abstract WordDao getWordDao();

    //在数据库中添加字段
//    static final Migration MIGRATION_2_3 = new Migration(2,3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE word ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");
//        }
//    };

    //在现有的数据库中删除字段
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //先创建一个新的表
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL,english_word TEXT," +
                    "chinese_meaing TEXT)");
            //让后把word中的数据复制到新表中
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaing)" +
                    "SELECT id,english_word,chinese_meaing FROM word");
            //删除旧的表
            database.execSQL("DROP TABLE word");
            //将新表的名字改回旧表的名字
            database.execSQL("ALTER TABLE word_temp RENAME to word");
        }
    };

    //在数据库中添加字段
    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE word ADD COLUMN hidden_chinese INTEGER NOT NULL DEFAULT 0");
        }
    };
}
