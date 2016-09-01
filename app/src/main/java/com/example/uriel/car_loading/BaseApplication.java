package com.example.uriel.car_loading;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.util.Log;

import com.amap.api.navi.logger.Logger;

/**
 * Created by uriel on 2016/6/2.
 */
public class BaseApplication extends Application {
    // 播放背景音乐的MediaPlayer
    private MediaPlayer player;
    public void onCreate() {
        // 播放背景音乐
        player = MediaPlayer.create(this, R.raw.a);
        player.setLooping(true);
        player.start();
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("activity",activity.toString()+"Stopped");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.e("activity",activity.toString()+"Started");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.e("activity",activity.toString()+"SaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // 当暂停时，暂停播放背景音乐
                if(player != null && !player.isPlaying())
                {
                    player.start();
                }
                Log.e("activity",activity.toString()+"Resumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // 当恢复时，如果没有播放背景音乐，开始播放背景音乐
                if(player != null && player.isPlaying())
                {
                    player.pause();
                }
                Log.e("activity",activity.toString()+"Paused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("activity",activity.toString()+"Destroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("activity",activity.toString()+"Created");
            }
        });
    };


}
