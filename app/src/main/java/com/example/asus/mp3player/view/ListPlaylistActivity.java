package com.example.asus.mp3player.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.mp3player.Controller.ListPlaylistAdapter;
import com.example.asus.mp3player.R;

public class ListPlaylistActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Toolbar toolbarListPlaylist;
    private static ListView lstPlaylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_playlist);

        toolbarListPlaylist= (Toolbar)this.findViewById(R.id.toolbar_lst_playlist);
        lstPlaylist = (ListView) this.findViewById(R.id.lst_playlist);
        lstPlaylist.setOnItemClickListener(this);
        TextView mTitle = (TextView) this.findViewById(R.id.title_toolbar_playlist);
        Typeface toolbar_font = Typeface.createFromAsset(getAssets(),  "fonts/CookieRegular.ttf");
        mTitle.setTypeface(toolbar_font);
        toolbarListPlaylist.setNavigationIcon(R.drawable.back);
        toolbarListPlaylist.setTitle("");
        setSupportActionBar(toolbarListPlaylist);

        //set adapter

        updateListPlaylist(this);
        toolbarListPlaylist.setNavigationOnClickListener(this);
    }

    public static void updateListPlaylist(Context context) {
        PlayerActivity.arrPlaylist= PlayerActivity.dataSource.getAllPlaylist();
        ListPlaylistAdapter adapter = new ListPlaylistAdapter(context,PlayerActivity.arrPlaylist);
        lstPlaylist.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_playlist)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add new playlist");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    PlayerActivity.dataSource.addNewPlaylist(m_Text);
                    updateListPlaylist(ListPlaylistActivity.this);
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
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent= new Intent(this,ListSongActivity.class);
        intent.putExtra("id_playlist",PlayerActivity.arrPlaylist.get(position).getId());
        startActivityForResult(intent,PlayerActivity.SONG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PlayerActivity.SONG_REQUEST&&resultCode==RESULT_OK)
        {
            int curSong =data.getExtras().getInt("index");
            Intent intent= new Intent(this,PlayerActivity.class);
            intent.putExtra("indexP",curSong);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
