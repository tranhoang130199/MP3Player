package com.example.asus.mp3player.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DataSource {
    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    //final String MEDIA_PATH = "/sdcard/Download/";
    private String mp3Pattern = ".mp3";

    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private String[] allColumnsSong = {"id","name","path"};
    private String[] allColumnsPlaylist={"id","name"};
    private String[] allColumnsDetail={"song_id","playlist_id"};
    private Context context;
    public DataSource(Context context) {
        this.context = context;
        createDataSong();
        try {
            createFavorite();
        }catch (Exception e){

        }
    }

    public void open() throws SQLException
    {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }
    public void close() throws  SQLException
    {
        sqLiteOpenHelper.close();
    }

    public void createDataSong() {
        this.sqLiteOpenHelper = new PlayerSQLiteHelper(context);
        open();
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            //File home =context.getFilesDir();
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToDB(file);
                    }
                }
            }
        }
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToDB(file);
                    }

                }
            }
        }
    }
    private void addSongToDB(File song) {
        if (song.getName().endsWith(mp3Pattern)) {
            ArrayList<SongModel> songList= new ArrayList<>();
            songList=getAllSong();
            for(SongModel songModel: songList)
            {
                if(songModel.getPath().equals(song.getPath()))
                    return;
            }
            ContentValues values = new ContentValues();
            values.put("name",song.getName().substring(0,song.getName().length()-4));
            values.put("path",song.getPath());
            sqLiteDatabase.insert("songs",null,values);
        }
    }
    public ArrayList<SongModel> getAllSong()
    {
        ArrayList<SongModel> songsList= new ArrayList<>();
        //select * note
        Cursor cursor =sqLiteDatabase.query("songs",allColumnsSong,null,null,null,null,null);

        if(cursor==null)
            return null;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            SongModel songModel = new SongModel();
            songModel.setId(cursor.getInt(0));
            songModel.setName(cursor.getString(1));
            songModel.setPath((cursor.getString(2)));
            songsList.add(songModel);
            cursor.moveToNext();
        }
        return songsList;
    }

    public ArrayList<PlaylistModel> getAllPlaylist()
    {
        ArrayList<PlaylistModel> playlistsList= new ArrayList<>();
        //select * note
       Cursor cursor =sqLiteDatabase.query("playlists",allColumnsPlaylist,null,null,null,null,null);
      //  Cursor cursor =sqLiteDatabase.query("songs",allColumnsSong,null,null,null,null,null);


        if(cursor==null)
            return null;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            if(!cursor.getString(1).equals("Favorite")) {
                PlaylistModel playlistModel = new PlaylistModel();
                playlistModel.setId(cursor.getInt(0));
                playlistModel.setName(cursor.getString(1));
                playlistsList.add(playlistModel);
            }
            cursor.moveToNext();
        }
        return playlistsList;
    }

    public void createFavorite(){
        ContentValues values = new ContentValues();
        values.put("id",100);
        values.put("name","Favorite");
        sqLiteDatabase.insert("playlists",null,values);

    }
    public void addNewPlaylist(String name){
        ContentValues values = new ContentValues();
        values.put("name",name);
        sqLiteDatabase.insert("playlists",null,values);
       // Toast.makeText(this.context,"Add new playlist success",Toast.LENGTH_LONG).show();
    }

    public void deletePlaylist(PlaylistModel model)
    {
        sqLiteDatabase.delete("detail","playlist_id=?",new String[]{model.getId()+""});
        sqLiteDatabase.delete("playlists","id="+model.getId(),null);
        //Toast.makeText(this.context,"Delete playlist success",Toast.LENGTH_LONG).show();
    }

    public void updatePlaylist(PlaylistModel model,String name){
        ContentValues values = new ContentValues();
        values.put("name",name);
        sqLiteDatabase.update("playlists",values,"id="+model.getId(),null);
        //Toast.makeText(this.context,"Update playlist success",Toast.LENGTH_LONG).show();
    }

    public void addNewDetail(int song_id, int playlist_id)
    {
        Cursor cursor =sqLiteDatabase.query("detail",allColumnsDetail,null,null,null,null,null);
        //  Cursor cursor =sqLiteDatabase.query("songs",allColumnsSong,null,null,null,null,null);
        if(cursor==null)
        {
            ContentValues values = new ContentValues();
            values.put("song_id",song_id);
            values.put("playlist_id",playlist_id);
            sqLiteDatabase.insert("detail",null,values);
            Toast.makeText(this.context,"Add song to playlist success",Toast.LENGTH_LONG).show();
            return;
        }
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            if(cursor.getInt(0)==song_id && cursor.getInt(1)==playlist_id)
                return;
            cursor.moveToNext();
        }
        ContentValues values = new ContentValues();
        values.put("song_id",song_id);
        values.put("playlist_id",playlist_id);
        sqLiteDatabase.insert("detail",null,values);
        //Toast.makeText(this.context,"Add song to playlist success",Toast.LENGTH_LONG).show();
    }

    public ArrayList<SongModel> getSongFromPlaylist(int playlist_id){
        ArrayList<SongModel> songsList= new ArrayList<>();
        Cursor cursor= sqLiteDatabase.query("detail",new String[]{"song_id"},"playlist_id=?",new String[]{playlist_id+""},null,null,null);
        if(cursor==null)
            return null;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            Cursor cursor2= sqLiteDatabase.query("songs",allColumnsSong,"id=?",new String[]{cursor.getInt(0)+""},null,null,null);
            cursor2.moveToFirst();
            SongModel songModel = new SongModel();
            songModel.setId(cursor2.getInt(0));
            songModel.setName(cursor2.getString(1));
            songModel.setPath(cursor2.getString(2));
            songsList.add(songModel);
            cursor.moveToNext();
        }
        return songsList;
    }
    public void deleteDetail(int playlist_id,int song_id)
    {
        sqLiteDatabase.delete("detail","song_id=? and playlist_id=?",new String[]{song_id+"",playlist_id+""});
        //Toast.makeText(this.context,"Delete song from playlist success",Toast.LENGTH_LONG).show();
    }
    public boolean isLove(int song_id)
    {
        Cursor cursor =sqLiteDatabase.query("detail",new String[]{"song_id"},"playlist_id=?",new String[]{100+""},null,null,null);
        //  Cursor cursor =sqLiteDatabase.query("songs",allColumnsSong,null,null,null,null,null);


        if(cursor==null)
            return false;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            if(cursor.getInt(0)==song_id)
                return true;
            cursor.moveToNext();
        }
        return false;
    }

    public void insertLyric(long time, int idsong, String title)
    {
        try {
            ContentValues values = new ContentValues();
            values.put("time", time);
            values.put("song_id", idsong);
            values.put("title", title);
            sqLiteDatabase.insert("lyric", null, values);
        }
        catch (Exception e)
        {

        }
    }

    public ArrayList<LyricModel> getAllLyricByIdSong(int idsong)
    {
        ArrayList<LyricModel> lyricModels= new ArrayList<>();
        //select * note
        Cursor cursor =sqLiteDatabase.query("lyric",new String[]{"time","song_id","title"},"song_id=?",new String[]{idsong+""},null,null,null);
        //  Cursor cursor =sqLiteDatabase.query("songs",allColumnsSong,null,null,null,null,null);


        if(cursor==null)
            return null;

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {

            LyricModel model = new LyricModel();
            model.setTime(cursor.getInt(0));
            model.setSong_id(cursor.getInt(1));
            model.setTitle(cursor.getString(2));
            lyricModels.add(model);
            cursor.moveToNext();
        }
        return lyricModels;
    }

    public void deleteLyric(int idsong)
    {
        sqLiteDatabase.delete("lyric","song_id=?",new String[]{idsong+""});
    }
    public void deleteLyricByTime(long time, int idsong)
    {
        sqLiteDatabase.delete("lyric","time=? and song_id=?",new String[]{time+"",idsong+""});
    }
}