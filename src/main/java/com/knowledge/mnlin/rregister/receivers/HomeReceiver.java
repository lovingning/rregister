package com.knowledge.mnlin.rregister.receivers;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

/**
 * Created on 2018/5/23  19:56
 * function : 监听一些特殊的功能键位,比如:home键,最近任务键等等
 *
 * 由于手机不同,因此特殊键位最好一次只监听一种
 *
 * 比如华为某些手机在:SpecialKey.RecentApps后,在任务栏点击近期的任务进入应用,还会触发SpecialKey.Home
 *
 * @author mnlin
 */
public class HomeReceiver extends BaseReceiver<HomeReceiver.SpecialKey> {
    /**
     * 特殊的 键位触发事件
     */
    public enum SpecialKey{
        /**
         * home键
         */
        Home,

        /**
         * 弹出近期任务
         */
        RecentApps,

        /**
         * 锁屏(屏幕锁定)
         */
        Lock,

        /**
         * 辅助键,一说为长按Home键时触发
         */
        Assist,

        /**
         * 未知状态
         */
        Unknown
    }

    /**
     * 特殊键位的不同可能情况
     */
    private static final String KEY_SYSTEM_DIALOG_REASON = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    public HomeReceiver(@NonNull HttpCallback<HomeReceiver.SpecialKey> callback){
        super(callback,Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    @Override
    protected SpecialKey apply(Intent intent) {
        String reason = intent.getStringExtra(KEY_SYSTEM_DIALOG_REASON);
        switch (reason){
            case SYSTEM_DIALOG_REASON_RECENT_APPS:
                return SpecialKey.RecentApps;
            case SYSTEM_DIALOG_REASON_HOME_KEY:
                return SpecialKey.Home;
            case SYSTEM_DIALOG_REASON_LOCK:
                return SpecialKey.Lock;
            case SYSTEM_DIALOG_REASON_ASSIST:
                return SpecialKey.Assist;
                default:
                    return SpecialKey.Unknown;
        }
    }
}
