package com.example.asus.mp3player.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.mp3player.Controller.ListSongAdapter;
import com.example.asus.mp3player.Model.SongModel;
import com.example.asus.mp3player.R;

import java.util.ArrayList;

public class ListSongActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, TextWatcher, View.OnClickListener {

    private Toolbar toolbarListSong;
    private static ListView lstSong;
    public static int idPlaylistSelect=-1;
    public static int favorite=-1;
    private EditText editSearch;
    static ListSongAdapter adapter;
    public static boolean isSearch=false;
    public static ArrayList<SongModel> arrSongFromPlaylist = new ArrayList<>();
    public static ArrayList<SongModel> arrSongFromFavorite = new ArrayList<>();
    ArrayList<SongModel> songModelSearch = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        toolbarListSong=(Toolbar)this.findViewById(R.id.toolbar_lst_song);
        lstSong=(ListView)this.findViewById(R.id.lst_songs);
        //set toolbar
        toolbarListSong.setNavigationIcon(R.drawable.back);
        TextView mTitle = (TextView) this.findViewById(R.id.title_toolbar_song);
        Typeface toolbar_font = Typeface.createFromAsset(getAssets(),  "fonts/CookieRegular.ttf");
        mTitle.setTypeface(toolbar_font);
        toolbarListSong.setTitle("");
        setSupportActionBar(toolbarListSong);


        try{
            favorite = getIntent().getExtras().getInt("favorite");
        }
        catch (Exception e){}
      //  ListSongAdapter adapter;
        try {
            idPlaylistSelect = getIntent().getExtras().getInt("id_playlist");
        }
        catch (Exception e){

        }
        updateListSong(this);
        lstSong.setOnItemClickListener(this);

        toolbarListSong.setNavigationOnClickListener(this);
        editSearch=(EditText)this.findViewById(R.id.edit_search);
        //editSearch.setSelected(false);
        //editSearch.setFocusable(false);
//        editSearch.setFocusable(true);
//        editSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editSearch.setFocusable(true);
//            }
//        });
       editSearch.addTextChangedListener(this);

    }

    public static void updateListSong(Context context) {
        if(favorite==1)
        {
            arrSongFromFavorite=PlayerActivity.dataSource.getSongFromPlaylist(100);
            adapter = new ListSongAdapter(context,arrSongFromFavorite);
            lstSong.setAdapter(adapter);
            return;
        }
        if(idPlaylistSelect!=-1)
        {
            arrSongFromPlaylist=PlayerActivity.dataSource.getSongFromPlaylist(idPlaylistSelect);
            adapter = new ListSongAdapter(context,arrSongFromPlaylist);
            lstSong.setAdapter(adapter);
            return;
        }
        else {
            adapter = new ListSongAdapter(context, PlayerActivity.arrSong);
            lstSong.setAdapter(adapter);
            return;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isSearch)
            PlayerActivity.arrSong=songModelSearch;
        Intent intent= new Intent(this,PlayerActivity.class);
        intent.putExtra("index",position);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onClick(View v) {
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PlayerActivity.arrSong=PlayerActivity.dataSource.getAllSong();
        idPlaylistSelect=-1;
        favorite=-1;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateListSongSearch(editSearch.getText().toString());
    }

    private void updateListSongSearch(String s) {
        PlayerActivity.arrSong=PlayerActivity.dataSource.getAllSong();
        ArrayList<SongModel> allSong= new ArrayList<>();
        if(favorite==1)
            allSong=arrSongFromFavorite;
        else if(idPlaylistSelect!=-1)
            allSong = arrSongFromPlaylist;
        else
            allSong=PlayerActivity.arrSong;
        songModelSearch.clear();
        if(s.trim().length()==0)
        {
            adapter=new ListSongAdapter(this,allSong);
            lstSong.setAdapter(adapter);
            songModelSearch=allSong;
        }
        if(s.trim().length()>0)
        {
            songModelSearch.clear();
            for(int i=0;i<allSong.size();i++)
            {
                if(allSong.get(i).getName().toLowerCase().contains(s.trim().toLowerCase()))
                {
                    songModelSearch.add(allSong.get(i));
                }
            }
            adapter=new ListSongAdapter(this,songModelSearch);
            lstSong.setAdapter(adapter);
        }
        isSearch=true;
        //PlayerActivity.arrSong=songModelSearch;
    }

}
