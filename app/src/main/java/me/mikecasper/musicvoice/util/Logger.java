package me.mikecasper.musicvoice.util;

import android.support.annotation.NonNull;
import android.util.Log;

import me.mikecasper.musicvoice.MusicVoiceApplication;

public final class Logger {
    private Logger() { }

    public static void e(String tag, String message, @NonNull Throwable throwable) {
        if (MusicVoiceApplication.LOG_LEVEL != MusicVoiceApplication.LogLevel.BASIC) {
            Log.e(tag, message, throwable);
        }
    }

    public static void e(String tag, String message) {
        if (MusicVoiceApplication.LOG_LEVEL != MusicVoiceApplication.LogLevel.BASIC) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void d(String tag, String message) {
        if (MusicVoiceApplication.LOG_LEVEL != MusicVoiceApplication.LogLevel.BASIC) {
            Log.d(tag, message);
        }
    }
}