package test9.mymusic.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import test9.mymusic.R;

/**
 * Created by 123 on 2016/3/14.
 */
public class MusicRaw {

    private List<String> mMusicList = new ArrayList<>();

    public MusicRaw(){
        mMusicList.add("color_x_3d");
        mMusicList.add("heroes_within_main");
        mMusicList.add("high_above");
    }

    public int getRawId(int number){
        switch (number){
            case 0:
                return R.raw.color_x_3d;
            case 1:
                return R.raw.heroes_within_main;
            case 2:
                return R.raw.high_above;
            default:
                return -1;
        }

    }

    public List<String> getmMusicList(){
        return mMusicList;
    }

}
