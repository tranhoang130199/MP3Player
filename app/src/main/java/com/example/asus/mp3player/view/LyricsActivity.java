package com.example.asus.mp3player.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.asus.mp3player.R;


public class LyricsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnOk;
    private Button btnCancel;
    private EditText editLyric;

    //public static Map<Integer, String> lyricMap = new HashMap<Integer, String>();
    //public static ArrayList<String> lyricArray = new ArrayList<>();
    public static String[] lines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);
        btnOk = (Button)this.findViewById(R.id.btn_ok);
        btnCancel = (Button)this.findViewById(R.id.btn_cancel);
        editLyric = (EditText)this.findViewById(R.id.edit_lyric);
        editLyric.setTextColor(Color.WHITE);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        TextView mTitle = (TextView) this.findViewById(R.id.title_toolbar_lyric);
        Typeface toolbar_font = Typeface.createFromAsset(getAssets(),  "fonts/CookieRegular.ttf");
        mTitle.setTypeface(toolbar_font);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_ok)
        {
            String multiLines = editLyric.getText().toString();
            String delimiter = "\n";
            lines = multiLines.split(delimiter);
            Intent intent = new Intent(this,SetLyricActivity.class);
            startActivity(intent);

        }
        if(id==R.id.btn_cancel)
            finish();
    }
}
