package com.example.asus.mp3player.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.mp3player.Model.PlaylistModel;
import com.example.asus.mp3player.Model.SongModel;
import com.example.asus.mp3player.R;
import com.example.asus.mp3player.view.ListPlaylistActivity;
import com.example.asus.mp3player.view.ListSongActivity;
import com.example.asus.mp3player.view.LyricsActivity;
import com.example.asus.mp3player.view.PlayerActivity;

import java.io.IOException;
import java.util.ArrayList;

public class ListSongAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private ArrayList<SongModel> listSong= new ArrayList<>();
    private TextView lblOption;
    public static String pathSongLyric;
    public static int idSongLyric;
    public ListSongAdapter(Context context, ArrayList<SongModel> listSong) {
        this.context = context;
        this.listSong = listSong;
    }

    @Override
    public int getCount() {
        return listSong.size();
    }

    @Override
    public Object getItem(int position) {
        return listSong.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listSong.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = LayoutInflater.from(context).inflate(R.layout.list_song_item,parent,false);
        TextView lblName= (TextView) rowView.findViewById(R.id.lbl_song_name);
        lblName.setText(listSong.get(position).getName());
        Typeface text_font = Typeface.createFromAsset(context.getAssets(),  "fonts/DancingScrip.ttf");
        lblName.setTypeface(text_font);
        lblOption= (TextView)rowView.findViewById(R.id.lbl_option_song);
        lblOption.setTag(listSong.get(position));
        lblOption.setOnClickListener(this);
        return rowView;
    }

    @Override
    public void onClick(View v) {
        PopupMenu popupMenu = new PopupMenu(this.context,v);
        if(ListSongActivity.favorite==1)
            popupMenu.getMenuInflater().inflate(R.menu.song_option3_menu,popupMenu.getMenu());
        else if(ListSongActivity.idPlaylistSelect==-1)
            popupMenu.getMenuInflater().inflate(R.menu.song_option_menu,popupMenu.getMenu());
        else
            popupMenu.getMenuInflater().inflate(R.menu.song_option2_menu,popupMenu.getMenu());
        //https://www.tutorialspoint.com/android-popup-menu-example
        //Toast.makeText(this.context,"Bạn click vào vị trí: "+v.getTag(),Toast.LENGTH_LONG).show();
        final SongModel songSelected=(SongModel) v.getTag();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.delete_from_favorite)
                {
                    PlayerActivity.dataSource.deleteDetail(100,songSelected.getId());
                    ListSongActivity.updateListSong(context);
                }
                if(item.getItemId()==R.id.add_to_favorite)
                {
                    PlayerActivity.dataSource.addNewDetail(songSelected.getId(),100);
                }
                int n =PlayerActivity.dataSource.getAllPlaylist().size();
                String[] playlistName = new String[n];
                final int []pos= new int[n];
                for(int i=0;i<n;i++)
                {
                    playlistName[i]=PlayerActivity.dataSource.getAllPlaylist().get(i).getName().toString();
                    pos[i]=PlayerActivity.dataSource.getAllPlaylist().get(i).getId();
                }
                if(item.getItemId()==R.id.add_to_playlist){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose a playlist");
                    builder.setItems(playlistName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayerActivity.dataSource.addNewDetail(songSelected.getId(),pos[which]);
                        }
                    });

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if(item.getItemId()==R.id.delete_from_playlist)
                {
                    PlayerActivity.dataSource.deleteDetail(ListSongActivity.idPlaylistSelect,songSelected.getId());
                    ListSongActivity.updateListSong(context);
                }
                if(item.getItemId()==R.id.set_lyrics)
                {
                    Intent intent = new Intent(context, LyricsActivity.class);
                    pathSongLyric=songSelected.getPath();
                    idSongLyric=songSelected.getId();
                    context.startActivity(intent);
                }

                return true;
            }
        });
        popupMenu.show();

    }
}