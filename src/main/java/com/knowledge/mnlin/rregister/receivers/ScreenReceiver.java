package com.knowledge.mnlin.rregister.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

/**
 * Created on 2018/5/23  10:07
 * function : 监听屏幕打开或关闭
 *
 * @author mnlin
 */
public class ScreenReceiver extends BaseReceiver<ScreenReceiver.ScreenStatus> {
    public enum ScreenStatus {
        /**
         * 屏幕被打开
         */
        ScreenON,


        /**
         * 屏幕被关闭
         */
        ScreenOff,

        /**
         * 未知状态/异常状态
         */
        Unknown
    }

    public ScreenReceiver(@NonNull HttpCallback<ScreenReceiver.ScreenStatus> callback) {
        super(callback,Intent.ACTION_SCREEN_OFF, Intent.ACTION_SCREEN_ON);
    }

    @Override
    protected ScreenStatus apply(Intent intent) {
        PowerManager powerManager;
        if (context.get() == null || (powerManager = (PowerManager) context.get().getSystemService(Context.POWER_SERVICE)) == null) {
            return ScreenStatus.Unknown;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                return powerManager.isInteractive() ? ScreenStatus.ScreenON : ScreenStatus.ScreenOff;
            } else {
                return powerManager.isScreenOn() ? ScreenStatus.ScreenON : ScreenStatus.ScreenOff;
            }
        }
    }
}
