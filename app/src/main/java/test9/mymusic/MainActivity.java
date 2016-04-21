package test9.mymusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test9.mymusic.ListViewAdapter.InformationList;
import test9.mymusic.ListViewAdapter.ListViewAdapter;
import test9.mymusic.UtilityClass.MusicRaw;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mListView;
    private MusicRaw mMusicRaw = new MusicRaw();
    private Messenger mMusicMessenger;
    private ImageButton star_pause_button;
    private ImageButton next_button;
    private ImageButton prev_button;
    private TextView mTextView;
    private Boolean star_or_pause=true;
    private Message message_to_service=new Message();
    private RemoteViews remoteViews;
    private Notification mNotification;
    private NotificationManager manager;
    private DoSendMessageToService doSendMessageToService;
    public static final int STAR_MUSIC=0;
    public static final int PAUSE_MUSIC=-1;
    public static final int NEXT_MUSIC=1;
    public static final int PREV_MUSIC=2;
    public static final int CHANGE_MUSIC=3;
    public static final int PLAY_FROM_FIRST=4;
    public static final int PLAY_SONG=5;
    public static final String STAR_PAUSE="star_pause";
    public static final String NEXT="next";
    public static final String PREV="prev";


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicMessenger=new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //与service的messenger绑定
        message_to_service.replyTo=messenger;

        //自定义notification
        remoteViews=new RemoteViews(getPackageName(), R.layout.notification_layout);
        manager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContent(remoteViews);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());

        mNotification = builder.build();
        manager.notify(0, mNotification);

        //获取各个控件
        mTextView=(TextView)findViewById(R.id.the_music_Name);

        star_pause_button=(ImageButton)findViewById(R.id.star_pause_music_button);
        next_button=(ImageButton)findViewById(R.id.next_music_button);
        prev_button=(ImageButton)findViewById(R.id.prev_music_button);

        //设置点击事件
        star_pause_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        prev_button.setOnClickListener(this);
        remoteViews.setOnClickPendingIntent(R.id.notification_star_pause_music_button, getPendingIntent(this, R.id.notification_star_pause_music_button));
        remoteViews.setOnClickPendingIntent(R.id.notification_next_music_button, getPendingIntent(this, R.id.notification_next_music_button));
        remoteViews.setOnClickPendingIntent(R.id.notification_prev_music_button,getPendingIntent(this,R.id.notification_prev_music_button));

        //设置播放列表及其点击事件
        mListView = (ListView) findViewById(R.id.music_list_view);
        List<InformationList> musicname = new ArrayList<>();
        musicname.add(new InformationList(mMusicRaw.getmMusicList().get(0)));
        musicname.add(new InformationList(mMusicRaw.getmMusicList().get(1)));
        musicname.add(new InformationList(mMusicRaw.getmMusicList().get(2)));

        ListViewAdapter listViewAdapter = new ListViewAdapter(MainActivity.this, musicname);
        mListView.setAdapter(listViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        doSendMessageToService.playListViewSong(message_to_service,position);
                        break;
                    case 1:
                        doSendMessageToService.playListViewSong(message_to_service,position);
                        break;
                    case 2:
                        doSendMessageToService.playListViewSong(message_to_service,position);
                        break;
                }
            }
        });

        //绑定服务
        bindService(new Intent(this, MusicService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        doSendMessageToService=new DoSendMessageToService();

        //动态注册广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(STAR_PAUSE);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(PREV);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        if(mMusicMessenger!=null) {
            switch (v.getId()) {
                case R.id.star_pause_music_button:
                    doSendMessageToService.starOrpause(message_to_service);
                    break;
                case R.id.next_music_button:
                    doSendMessageToService.next(message_to_service);
                    break;
                case R.id.prev_music_button:
                    doSendMessageToService.prev(message_to_service);
                    break;
                default:
                    break;
            }
        }
    }

    //接受来自service的message，用以更新显示信息
    private Messenger messenger=new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CHANGE_MUSIC:
                    mTextView.setText(mMusicRaw.getmMusicList().get(msg.arg1));
                    remoteViews.setTextViewText(R.id.notification_the_music_Name, mMusicRaw.getmMusicList().get(msg.arg1));
                    mNotification.contentView=remoteViews;
                    manager.notify(0,mNotification);
                    break;
                case PLAY_FROM_FIRST:
                    mTextView.setText(mMusicRaw.getmMusicList().get(0));
                    remoteViews.setTextViewText(R.id.notification_the_music_Name, mMusicRaw.getmMusicList().get(0));
                    mNotification.contentView=remoteViews;
                    manager.notify(0, mNotification);
                    break;
            }
        }
    });


    //向service发送信息播放歌曲
    class  DoSendMessageToService{
        public void starOrpause(Message msg){
            if(star_or_pause){
                star_pause_button.setBackgroundResource(R.drawable.button_grey_pause);
                remoteViews.setImageViewResource(R.id.notification_star_pause_music_button,R.drawable.button_grey_pause);
                mNotification.contentView=remoteViews;
                manager.notify(0, mNotification);
                star_or_pause=false;
                msg.what=STAR_MUSIC;
                try {
                    mMusicMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else{
                star_pause_button.setBackgroundResource(R.drawable.button_grey_play);
                remoteViews.setImageViewResource(R.id.notification_star_pause_music_button, R.drawable.button_grey_play);
                mNotification.contentView=remoteViews;
                manager.notify(0, mNotification);
                star_or_pause=true;
                msg.what=PAUSE_MUSIC;
                try {
                    mMusicMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void next(Message msg){
            msg.what=NEXT_MUSIC;
            if(star_or_pause){
                star_pause_button.setBackgroundResource(R.drawable.button_grey_pause);
                remoteViews.setImageViewResource(R.id.notification_star_pause_music_button, R.drawable.button_grey_pause);
                mNotification.contentView=remoteViews;
                manager.notify(0, mNotification);
                star_or_pause = false;
            }
            try {
                mMusicMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void prev(Message msg){
            msg.what=PREV_MUSIC;
            if(star_or_pause){
                star_pause_button.setBackgroundResource(R.drawable.button_grey_pause);
                remoteViews.setImageViewResource(R.id.notification_star_pause_music_button, R.drawable.button_grey_pause);
                mNotification.contentView=remoteViews;
                manager.notify(0, mNotification);
                star_or_pause=false;
            }
            try {
                mMusicMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void playListViewSong(Message msg,int number){
            msg.what=PLAY_SONG;
            msg.arg1=number;
            if(star_or_pause){
                star_pause_button.setBackgroundResource(R.drawable.button_grey_pause);
                remoteViews.setImageViewResource(R.id.notification_star_pause_music_button, R.drawable.button_grey_pause);
                mNotification.contentView=remoteViews;
                manager.notify(0, mNotification);
                star_or_pause=false;
            }
            try {
                mMusicMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent=new Intent();
        switch (buttonId){
            case R.id.notification_star_pause_music_button:
                intent.setAction(STAR_PAUSE);
                break;
            case R.id.notification_next_music_button:
                intent.setAction(NEXT);
                break;
            case R.id.notification_prev_music_button:
                intent.setAction(PREV);
                break;
            default:
                break;
        }

        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    //接收来自notification的广播，使notification也能控制音乐播放
    private BroadcastReceiver receiver=new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction=intent.getAction();

            switch (mAction) {
                case STAR_PAUSE:
                    doSendMessageToService.starOrpause(message_to_service);
                    break;
                case NEXT:
                    doSendMessageToService.next(message_to_service);
                    break;
                case PREV:
                    doSendMessageToService.prev(message_to_service);
                    break;
            }
        }
    };

}
