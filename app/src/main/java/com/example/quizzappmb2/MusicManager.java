package com.example.quizzappmb2;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicManager {
    private static MediaPlayer mp;
    private static boolean isInitialized = false;


    static void initialize(Context context) {
        if (!isInitialized) {
            try {
                mp = MediaPlayer.create(context.getApplicationContext(), R.raw.background);
                if (mp != null) {
                    mp.setLooping(true);
                    mp.setVolume(0.5f, 0.5f);
                }
                isInitialized = true;
            } catch (Exception e) {
                Log.e("MUSIC_MANAGER", "Lỗi khởi tạo nhạc nền: File không tồn tại hoặc bị lỗi.");
            }
        }
    }

    public static void play(Context context) {
        initialize(context);

        SharedPreferences prefs = context.getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        boolean isMusicOn = prefs.getBoolean("MUSIC", true);

        if (!isMusicOn) {
            pause();
            return;
        }

        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }

    public static void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    public static void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            isInitialized = false;
        }
    }

    public static void updateMusicState(Context context, boolean isTurnOn) {
        if (isTurnOn) play(context);
        else pause();
    }
}