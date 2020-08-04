package com.example.asus.mp3player.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.mp3player.R;


public class ListLyricAdapter extends BaseAdapter {
    private Context context;
    private String[] listLyric;
    private TextView lblLyric;

    public ListLyricAdapter(Context context, String[] listLyric) {
        this.context = context;
        this.listLyric = listLyric;
    }

    @Override
    public int getCount() {
        return listLyric.length;
    }

    @Override
    public Object getItem(int position) {
        return listLyric[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(this.context).inflate(R.layout.list_lyric_item,parent,false);
        lblLyric= (TextView)rowView.findViewById(R.id.lbl_lyric);
        lblLyric.setText(listLyric[position]);
        return rowView;
    }
}