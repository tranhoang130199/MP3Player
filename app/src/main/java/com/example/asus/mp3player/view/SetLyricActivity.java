package com.example.asus.mp3player.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.mp3player.Controller.ListLyricAdapter;
import com.example.asus.mp3player.Controller.ListSongAdapter;
import com.example.asus.mp3player.Model.LyricModel;
import com.example.asus.mp3player.Model.State;
import com.example.asus.mp3player.R;

import java.io.IOException;
import java.util.ArrayList;

public class SetLyricActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {

    private ListView listLineLyric;
    private Button btnSave;
    private Button btnCancel;
    private int premax;
    private int newmax;
    public static ArrayList<LyricModel> arrLyric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lyric);

        arrLyric= new ArrayList<>();
        listLineLyric = (ListView)this.findViewById(R.id.list_lyrics);
        btnSave = (Button)this.findViewById(R.id.btn_save);
        btnCancel=(Button)this.findViewById(R.id.btn_cancel_set);
        ListLyricAdapter adapter = new ListLyricAdapter(this,LyricsActivity.lines);
        listLineLyric.setAdapter(adapter);
        PlayerActivity.mp.reset();
        try {
            PlayerActivity.mp.setDataSource(ListSongAdapter.pathSongLyric);
            PlayerActivity.mp.prepare();
        } catch (IOException e) {

        }
        PlayerActivity.mp.start();
        PlayerActivity.btnPlay.setImageResource(R.drawable.pause);
        listLineLyric.setOnItemClickListener(this);
        listLineLyric.setOnItemLongClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        TextView mTitle = (TextView) this.findViewById(R.id.title_toolbar_set_lyric);
        Typeface toolbar_font = Typeface.createFromAsset(getAssets(),  "fonts/CookieRegular.ttf");
        mTitle.setTypeface(toolbar_font);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position<newmax)
            return;
        State st =(State) view.getTag();
        if(st==null)
        {
            view.setBackgroundColor(Color.GRAY);
            view.setTag(new State(1));
            LyricModel model = new LyricModel();
            model.setTime(PlayerActivity.mp.getCurrentPosition());
            model.setTitle(LyricsActivity.lines[position]);
            model.setSong_id(ListSongAdapter.idSongLyric);
            arrLyric.add(model);
            premax=newmax;
            newmax=position;
            return;
        }
        if(st.state==1)
        {

            PlayerActivity.mp.seekTo((int) arrLyric.get(arrLyric.size()-1).getTime()-3000);
            arrLyric.remove(arrLyric.size()-1);
            view.setBackgroundColor(Color.parseColor("#222222"));
            //view.setBackgroundColor(Color.GREEN);
            view.setTag(new State(0));
            newmax = premax;
        }
        else {
            view.setBackgroundColor(Color.GRAY);
            view.setTag(new State(1));
            LyricModel model = new LyricModel();
            model.setTime(PlayerActivity.mp.getCurrentPosition());
            model.setTitle(LyricsActivity.lines[position]);
            model.setSong_id(ListSongAdapter.idSongLyric);
            arrLyric.add(model);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.btn_save)
        {
            PlayerActivity.dataSource.deleteLyric(ListSongAdapter.idSongLyric);
            if(arrLyric.size()==0)
                return;
            for(LyricModel model : arrLyric){
                PlayerActivity.dataSource.insertLyric(model.getTime()-1000,model.getSong_id(),model.getTitle());
            }
            finish();
        }
        if(id==R.id.btn_cancel_set)
        {
            finish();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(arrLyric.size()==0)
            return false;
        if(position==arrLyric.size()-1)
        {
            PlayerActivity.mp.seekTo((int) arrLyric.get(arrLyric.size()-2).getTime());
        }
       return true;
    }
}
