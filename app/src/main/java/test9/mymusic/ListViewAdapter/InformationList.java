package test9.mymusic.ListViewAdapter;

import java.io.Serializable;

/**
 * Created by 123 on 2016/3/14.
 */
public class InformationList implements Serializable {
    private String mInfo;

    public InformationList(String info){
        mInfo = info;
    }

    public String getmInfo(){ return mInfo;}

    public void setmInfo(String info){ mInfo = info;}
}
