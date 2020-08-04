package com.example.asus.mp3player.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.mp3player.Model.Function;
import com.example.asus.mp3player.Model.DataSource;
import com.example.asus.mp3player.Model.LyricModel;
import com.example.asus.mp3player.Model.PlaylistModel;
import com.example.asus.mp3player.Model.SongModel;
import com.example.asus.mp3player.R;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    public static int SONG_REQUEST =0;
    public static int PLAYLIST_REQUEST =1;
    public static int FAVORITE_REQUEST = 2;
    private int STATE_REPEAT=0;
    private int STATE_LOVE=0; //nolove
    private boolean random=false;
    private Toolbar toolbar;
    public static ArrayList<SongModel> arrSong= new ArrayList<>();
    public static ArrayList<PlaylistModel> arrPlaylist= new ArrayList<>();
    public static DataSource dataSource;
    public static MediaPlayer mp;
    private CountDownTimer countDownTimer;
    private Handler handler = new Handler();


    public static ImageView btnPlay;
    private ImageView btnNextSong;
    private ImageView btnBackSong;
    private ImageView btnRepeat;
    private ImageView btnRandom;
    private TextView lblCurrentTime;
    private TextView lblTotalTime;
    private SeekBar prgPlay;
    private TextView lblSongPlay;
    private ImageView btnLove;
    private Switch timer;
    private TextView remainTime;
    private TextView lblLyic;
    private int currentSongIndex = 0;
    private ArrayList<LyricModel> arrLineLyrics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //requestForPermission();
        //set toolbar
        toolbar =  (Toolbar)this.findViewById(R.id.my_toolbar);


        TextView mTitle = (TextView) this.findViewById(R.id.title_toolbar);
        Typeface toolbar_font = Typeface.createFromAsset(getAssets(),  "fonts/CookieRegular.ttf");
        Typeface text_font = Typeface.createFromAsset(getAssets(),  "fonts/DancingScrip.ttf");
        mTitle.setTypeface(toolbar_font);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //lấy tất cả bài hát từ db
        dataSource = new DataSource(this);
        arrSong= dataSource.getAllSong();

        //tạo Mediaplayer
        mp= new MediaPlayer();

        //find view id
        btnPlay =(ImageView)this.findViewById(R.id.btn_play);
        btnNextSong=(ImageView)this.findViewById(R.id.btn_next_song);
        btnBackSong=(ImageView)this.findViewById(R.id.btn_back_song);
        btnRepeat=(ImageView)this.findViewById(R.id.btn_repeat);
        btnRandom=(ImageView)this.findViewById(R.id.btn_random);
        lblCurrentTime=(TextView)this.findViewById(R.id.lbl_current_time);
        lblTotalTime=(TextView)this.findViewById(R.id.lbl_total_time);
        prgPlay =(SeekBar)this.findViewById(R.id.prg_play);
        lblSongPlay=(TextView)this.findViewById(R.id.lbl_song_play);
        timer = (Switch) this.findViewById(R.id.switch_timer);
        lblLyic =(TextView)this.findViewById(R.id.lbl_lyric);
        lblLyic.setTypeface(text_font);
        timer.setChecked(false);
        remainTime = (TextView)this.findViewById(R.id.remain_time);
        remainTime.setText("");
        timer.setOnCheckedChangeListener(this);
        lblSongPlay.setTypeface(text_font);
        //lblSongPlay.setSelected(true);
        btnLove=(ImageView)this.findViewById(R.id.img_love);

        btnPlay.setOnClickListener(this);
        btnBackSong.setOnClickListener(this);
        btnNextSong.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnRandom.setOnClickListener(this);
        btnLove.setOnClickListener(this);
        //btnNextSong.setOnLongClickListener(this);
        mp.setOnCompletionListener(this);
        prgPlay.setOnSeekBarChangeListener(this);
        if(arrSong.size()>0)
            playSong(currentSongIndex);
        mp.pause();
        btnPlay.setImageResource(R.drawable.play);

    }


    public void playSong(int index)
    {

        try {
            lblLyic.setText("");
            mp.reset();
            mp.setDataSource(arrSong.get(index).getPath());
            mp.prepare();
            mp.start();
            if(dataSource.isLove(arrSong.get(index).getId()))
            {
                btnLove.setImageResource(R.drawable.love);
                STATE_LOVE=1;
            }
            else {
                btnLove.setImageResource(R.drawable.no_love);
                STATE_LOVE=0;
            }
            //hiển thị tên bài hát lên toolbar
            //toolbar.setTitle(arrSong.get(index).getName());
            lblSongPlay.setText(arrSong.get(index).getName());
            //thay button play thành button pause
            btnPlay.setImageResource(R.drawable.pause);

            //set giá trị prg
            prgPlay.setProgress(0);
            prgPlay.setMax(100);
            arrLineLyrics = dataSource.getAllLyricByIdSong(arrSong.get(currentSongIndex).getId());
            //cập nhật trạng thái progress bar
            updateProgressBar();
            buildNotification();
        } catch (IOException e) {

        }
    }

    private void buildNotification() {
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Playing")
                .setContentText(arrSong.get(currentSongIndex).getName())
                .setDeleteIntent(pendingIntent)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 1, builder.build() );
    }


    public void updateProgressBar(){
        handler.postDelayed(updateTime,100);
    }
    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            long totalTime=mp.getDuration();
            long currentTime=mp.getCurrentPosition();

            //set lbl thời gian
            lblTotalTime.setText(Function.convertMilisecondstoTimer(totalTime));
            lblCurrentTime.setText(Function.convertMilisecondstoTimer(currentTime));

            //update progress bar
            prgPlay.setProgress(Function.percentageOfProgress(currentTime,totalTime));

            //đệ quy sau mỗi 100 milisecond
            long curTime=mp.getCurrentPosition();
            if(arrLineLyrics.size()!=0)
            {
                long max=0;
                String title="";
                for(LyricModel model:arrLineLyrics){
                    if(model.getTime()<=curTime)
                    {
                        if(model.getTime()>max)
                        {
                            max=model.getTime();
                            title=model.getTitle();
                        }

                    }
                }
                lblLyic.setText(title);
            }
            handler.postDelayed(this,100);
        }
    };

//    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
//    };
//
//    public final int EXTERNAL_REQUEST = 138;
//
//    public boolean requestForPermission() {
//
//        boolean isPermissionOn = true;
//        final int version = Build.VERSION.SDK_INT;
//        if (version >= 23) {
//            if (!canAccessExternalSd()) {
//                isPermissionOn = false;
//                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
//            }
//        }
//
//        return isPermissionOn;
//    }
//
//    public boolean canAccessExternalSd() {
//        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
//    }
//
//    private boolean hasPermission(String perm) {
//        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dataSource.isLove(arrSong.get(currentSongIndex).getId()))
        {
            btnLove.setImageResource(R.drawable.love);
            STATE_LOVE=1;
        }
        else {
            btnLove.setImageResource(R.drawable.no_love);
            STATE_LOVE=0;
        }

        //requestForPermission();
    }

    @Override
    public void onClick(View v) {
        int id= v.getId();
        if(id==R.id.btn_play) {
            if (mp.isPlaying()) {
                if (mp != null) {
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.play);
                }
            } else {
                if (mp != null) {
                    mp.start();
                    btnPlay.setImageResource(R.drawable.pause);
                }
            }
        }
        if(id==R.id.btn_back_song)
        {
            if(mp!=null){
            if(currentSongIndex>0){
                currentSongIndex--;
                playSong(currentSongIndex);
            }
            else
            {
                currentSongIndex=arrSong.size()-1;
                playSong(currentSongIndex);
            }
            buildNotification();
            }
        }
        if(id==R.id.btn_next_song)
        {
            if(mp!=null) {
                if (currentSongIndex < arrSong.size() - 1) {
                    currentSongIndex++;
                    playSong(currentSongIndex);
                } else {
                    currentSongIndex = 0;
                    playSong(currentSongIndex);
                }
                buildNotification();
            }
        }
        if(id==R.id.btn_repeat)
        {

                if(STATE_REPEAT==0)
                {
                    STATE_REPEAT=1;
                    btnRepeat.setImageResource(R.drawable.repeat_all_choose);
                }
                else if(STATE_REPEAT==1){
                    STATE_REPEAT=2;
                    btnRepeat.setImageResource(R.drawable.repeat1_choose);
                }
                else if(STATE_REPEAT==2)
                {
                    STATE_REPEAT=0;
                    btnRepeat.setImageResource(R.drawable.repeat_all);
                }

        }
        if(id==R.id.btn_random){
            if(random==false)
            {
                random=true;
                btnRandom.setImageResource(R.drawable.random_choose);
            }else{
                random=false;
                btnRandom.setImageResource(R.drawable.random);
            }
        }
        if(id==R.id.img_love)
        {
            if(STATE_LOVE==0)
            {
                STATE_LOVE=1;
                btnLove.setImageResource(R.drawable.love);
                dataSource.addNewDetail(arrSong.get(currentSongIndex).getId(),100);
            }
            else
            {
                STATE_LOVE=0;
                btnLove.setImageResource(R.drawable.no_love);
                dataSource.deleteDetail(100,arrSong.get(currentSongIndex).getId());
            }
        }
    }

   // @Override
    //public boolean onLongClick(View v) {
//        int currentPosition = mp.getCurrentPosition();
//        if (currentPosition + ForwardTime <= mp.getDuration()) {
//            mp.seekTo(currentPosition + ForwardTime);
//        } else {
//            mp.seekTo(mp.getDuration());
//        }
//        btnNextSong.setOnLongClickListener(this);
     //      return true;
   // }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if(STATE_REPEAT==2)
        {
            playSong(currentSongIndex);
        }
        else if(random) {
            int flag;
            //Random rd = new Random();
            if(arrSong.size()>1) {
                do {
                    flag = (int) (Math.random() * arrSong.size() + 0);
                    //flag = rd.nextInt(arrSong.size() - 1);
                } while (flag == currentSongIndex);
                currentSongIndex = flag;
                playSong(currentSongIndex);
            }
            else
                playSong(0);
        } else{
            if(currentSongIndex==arrSong.size()-1)
            {
                if(STATE_REPEAT==1)
                {
                    currentSongIndex=0;
                    playSong(currentSongIndex);
                }
                else {
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.play);
                }
            }
            else{
                currentSongIndex++;
                playSong(currentSongIndex);
                buildNotification();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
       handler.removeCallbacks(updateTime);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTime);
        long totalTime=mp.getDuration();
        mp.seekTo((int) Function.progressToMilisecond(seekBar.getProgress(),totalTime));

        updateProgressBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.list_songs) {
            arrSong=dataSource.getAllSong();
            Intent intent = new Intent(this, ListSongActivity.class);
            startActivityForResult(intent, SONG_REQUEST);
        }
        if(item.getItemId()==R.id.list_playlists){
            Intent intent = new Intent(this, ListPlaylistActivity.class);
            startActivityForResult(intent,PLAYLIST_REQUEST);
        }
        if(item.getItemId()==R.id.list_favorite)
        {
            Intent intent = new Intent(this, ListSongActivity.class);
            intent.putExtra("favorite",1);
            startActivityForResult(intent, FAVORITE_REQUEST);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==SONG_REQUEST&&resultCode==RESULT_OK)
        {

            if(ListSongActivity.isSearch=true)
            {
                ListSongActivity.isSearch=false;
                currentSongIndex =data.getExtras().getInt("index");
                playSong(currentSongIndex);
                arrSong=dataSource.getAllSong();
            }
            else
            {
                arrSong=dataSource.getAllSong();
                currentSongIndex =data.getExtras().getInt("index");
                playSong(currentSongIndex);
            }

        }
        if(requestCode==PLAYLIST_REQUEST&&resultCode==RESULT_OK)
        {

            if(ListSongActivity.isSearch==true) {
                ListSongActivity.isSearch = false;
                currentSongIndex =data.getExtras().getInt("indexP");
                playSong(currentSongIndex);
                arrSong = ListSongActivity.arrSongFromPlaylist;
            }
            else
            {
                arrSong = ListSongActivity.arrSongFromPlaylist;
                currentSongIndex =data.getExtras().getInt("indexP");
                playSong(currentSongIndex);
            }

        }
        if(requestCode==FAVORITE_REQUEST && resultCode==RESULT_OK)
        {

            if(ListSongActivity.isSearch==true) {
                ListSongActivity.isSearch=false;
                currentSongIndex =data.getExtras().getInt("index");
                playSong(currentSongIndex);
                arrSong=ListSongActivity.arrSongFromFavorite;
            }
            else
            {
                arrSong=ListSongActivity.arrSongFromFavorite;
                currentSongIndex =data.getExtras().getInt("index");
                playSong(currentSongIndex);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
        mp.release();
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.cancel(1);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(timer.isChecked())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
            builder.setTitle("Enter the number of minutes");

            // Set up the input
            final EditText input = new EditText(PlayerActivity.this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long time =Long.parseLong(input.getText().toString());
                    countDownTimer = new CountDownTimer(time*60*1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            remainTime.setText(Function.convertMilisecondstoTimer(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            timer.setChecked(false);
                            if(mp.isPlaying()) {
                                mp.pause();
                                btnPlay.setImageResource(R.drawable.play);
                            }
                        }
                    }.start();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    countDownTimer = new CountDownTimer(100,100) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            timer.setChecked(false);
                        }
                    }.start();
                    dialog.cancel();
                }

            });

            builder.show();
        }
        else
        {
            countDownTimer.cancel();
            remainTime.setText("");
        }

    }

}
