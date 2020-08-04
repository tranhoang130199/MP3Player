package com.example.asus.mp3player.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayerSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="mp3applications.db";
    public static final int DATABASE_VERSION=1;
    private static final String CREATE_DATABASE_SONGS ="CREATE TABLE songs(id INTEGER PRIMARY KEY autoincrement,name TEXT NOT NULL,path  TEXT NOT NULL)";
    private static final String CREATE_DATABASE_PLAYLISTS="CREATE TABLE playlists(id INTEGER PRIMARY KEY autoincrement,name TEXT NOT NULL);";
    private static final String CREATE_DATABASE_DETAIL="CREATE TABLE detail(song_id INTEGER,playlist_id INTEGER,FOREIGN KEY (song_id) REFERENCES songs(id),FOREIGN KEY (playlist_id) REFERENCES playlists(id));";
    private static final String CREATE_DATABASE_LYRIC=" CREATE TABLE lyric (time INTEGER NOT NULL, song_id INTEGER NOT NULL,title TEXT NOT NULL, FOREIGN KEY(song_id) REFERENCES songs(id))";

    public PlayerSQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE_SONGS);
        db.execSQL(CREATE_DATABASE_PLAYLISTS);
        db.execSQL(CREATE_DATABASE_DETAIL);
        db.execSQL(CREATE_DATABASE_LYRIC);

        //Log.d("TEST:","onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists detail");
        db.execSQL("Drop table if exists songs");
        db.execSQL("Drop table if exists playlists");
        db.execSQL("Drop table if exists lyric");
        onCreate(db);

        //Log.d("TEST:","onUpdate");
    }
}