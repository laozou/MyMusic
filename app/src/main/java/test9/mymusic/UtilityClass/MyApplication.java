package test9.mymusic.UtilityClass;

import android.app.Application;
import android.content.Context;

/**
 * Created by 123 on 2016/3/15.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
