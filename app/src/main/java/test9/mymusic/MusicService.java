package test9.mymusic;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.io.IOException;

import test9.mymusic.UtilityClass.MusicRaw;
import test9.mymusic.UtilityClass.MyApplication;

/**
 * Created by 123 on 2016/3/14.
 */
public class MusicService extends Service {
    private MediaPlayer mPlayer;
    private int mIndex = 0;
    private MusicRaw mMusicRaw=new MusicRaw();
    private Message message_from_service=new Message();
    public static boolean is_first_connect=false;

    public Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MainActivity.STAR_MUSIC:
                    starMusic();
                    if(is_first_connect){
                        message_from_service.what=MainActivity.PLAY_FROM_FIRST;
                        try {
                            msg.replyTo.send(message_from_service);
                            is_first_connect=false;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MainActivity.PAUSE_MUSIC:
                    pauseMusic();
                    break;
                case MainActivity.NEXT_MUSIC:
                    message_from_service.what=MainActivity.CHANGE_MUSIC;
                    message_from_service.arg1=playNext(MyApplication.getContext());
                    try {
                        msg.replyTo.send(message_from_service);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MainActivity.PREV_MUSIC:
                    message_from_service.what=MainActivity.CHANGE_MUSIC;
                    message_from_service.arg1=playPrev(MyApplication.getContext());
                    try {
                        msg.replyTo.send(message_from_service);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MainActivity.PLAY_SONG:
                    mIndex=msg.arg1;
                    playSong(MyApplication.getContext(), mMusicRaw.getRawId(mIndex));
                    message_from_service.what=MainActivity.CHANGE_MUSIC;
                    message_from_service.arg1=mIndex;
                    try {
                        msg.replyTo.send(message_from_service);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
            super.handleMessage(msg);
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer=new MediaPlayer();
        mPlayer=MediaPlayer.create(this,mMusicRaw.getRawId(mIndex));
        is_first_connect=true;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        return super.onUnbind(intent);
    }

    /**
     * 播放下一首
     */
    public int playNext(Context context) {
        if (++mIndex > 2) {
            mIndex = 0;
        }
        playSong(context, mMusicRaw.getRawId(mIndex));

        return mIndex;
    }

    /**
     * 播放上一首
     */
    public int playPrev(Context context) {
        if (--mIndex < 0) {
            mIndex = 2;
        }
        playSong(context, mMusicRaw.getRawId(mIndex));

        return mIndex;
    }

    /**
     * 播放指定的歌曲
     */
    public void playSong(Context context, int resid) {
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mPlayer.prepare();
            mPlayer.start();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void starMusic() {
        mPlayer.start();
    }

    public void pauseMusic() {
        mPlayer.pause();
    }

}
