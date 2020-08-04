package com.example.asus.mp3player.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.asus.mp3player.Model.PlaylistModel;
import com.example.asus.mp3player.R;
import com.example.asus.mp3player.view.ListPlaylistActivity;
import com.example.asus.mp3player.view.PlayerActivity;

import java.util.ArrayList;

public class ListPlaylistAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private ArrayList<PlaylistModel> listPlaylist= new ArrayList<>();
    private TextView lblOption;
    public ListPlaylistAdapter(Context context, ArrayList<PlaylistModel> listPlaylist) {
        this.context = context;
        this.listPlaylist = listPlaylist;
    }

    @Override
    public int getCount() {
        return listPlaylist.size();
    }

    @Override
    public Object getItem(int position) {
        return listPlaylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listPlaylist.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(this.context).inflate(R.layout.list_playlist_item,parent,false);
        TextView lblName= (TextView)rowView.findViewById(R.id.lbl_playlist_name);
        lblName.setText(listPlaylist.get(position).getName());
        Typeface text_font = Typeface.createFromAsset(context.getAssets(),  "fonts/DancingScrip.ttf");
        lblName.setTypeface(text_font);
        lblOption = (TextView)rowView.findViewById(R.id.lbl_option_playlist);
        lblOption.setTag(listPlaylist.get(position));
        lblOption.setOnClickListener(this);
        return rowView;
    }

    @Override
    public void onClick(View v) {
        final PlaylistModel playlistSelected =(PlaylistModel) v.getTag();
        PopupMenu popupMenu = new PopupMenu(this.context,v);
        popupMenu.getMenuInflater().inflate(R.menu.playlist_option_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.edit_playlist)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit playlist's name");

                    // Set up the input
                    final EditText input = new EditText(context);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String m_Text = input.getText().toString();
                            PlayerActivity.dataSource.updatePlaylist(playlistSelected,m_Text);
                            ListPlaylistActivity.updateListPlaylist(context);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                if(item.getItemId()==R.id.Delete_playlist)
                {
                    AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                            // set message, title, and icon
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    PlayerActivity.dataSource.deletePlaylist(playlistSelected);
                                    ListPlaylistActivity.updateListPlaylist(context);
                                    dialog.dismiss();
                                }

                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            })
                            .create();
                    myQuittingDialogBox.show();
                }
                return true;
            }
        });
        popupMenu.show();
    }
}