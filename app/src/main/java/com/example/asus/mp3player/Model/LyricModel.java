package com.example.asus.mp3player.Model;

public class LyricModel {
    private long time;
    private int song_id;
    private String title;

    public long getTime() {
        return time;
    }

    public int getSong_id() {
        return song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}