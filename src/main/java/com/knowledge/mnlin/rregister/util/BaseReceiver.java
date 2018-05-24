package com.knowledge.mnlin.rregister.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.knowledge.mnlin.rregister.BuildConfig;

import java.lang.ref.WeakReference;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 2018/5/22  9:59
 * function : 广播监听器基类
 * <p>
 * <T> 为泛型,表示回调时传递的数据
 *
 * @author mnlin
 */
public abstract class BaseReceiver<T> extends BroadcastReceiver {

    /**
     * action
     *
     * @deprecated
     */
    @Deprecated
    protected String ACTION_;

    /**
     * action集合
     */
    protected String[] ACTIONS;

    /**
     * 回调函数
     */
    private HttpCallback<T> httpCallback;


    /**
     * 保持上下文的软引用,需要的时候直接拿来使用
     */
    protected WeakReference<Context> context;

    protected BaseReceiver(HttpCallback<T> httpCallback, @NonNull String... action) {
        this.httpCallback = httpCallback;
        ACTIONS = action;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = new WeakReference<>(context);

        Observable.just(intent)
                .map(i -> {
                    String action = i.getAction();
                    if (!judgeActionEqual() || containAction(action)) {
                        return apply(i);
                    } else {
                        throw new RuntimeException("非需要的 Action ");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> httpCallback.run(result),
                        throwable -> {
                            if (BuildConfig.DEBUG) {
                                Log.e(String.format(Locale.CHINA, "Error: %s :", getClass().getName()), String.valueOf(throwable));
                            }
                        });
    }

    /**
     * @return true 如果获取到的intent中action为需要的action
     */
    private boolean containAction(String action) {
        if (action != null) {
            for (String s : ACTIONS) {
                if (s != null && s.equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理获取到的intent对象
     * <p>
     * 可直接抛出异常来中断处理流程
     */
    abstract protected T apply(Intent intent);

    /**
     * @return 返回action 字符串形式
     * @deprecated 可能某一个监听会同时处理多个action, 因此该方法废弃
     */
    @Deprecated
    public String getAction() {
        return ACTION_;
    }

    /**
     * @return 返回action 字符串形式
     */
    public String[] getActions() {
        return ACTIONS;
    }

    /**
     * @return 获取上下文的若引用
     */
    public WeakReference<Context> getContext() {
        return context;
    }

    /**
     * @return 返回intent_filter
     */
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        for (String action : ACTIONS) {
            filter.addAction(action);
        }
        return filter;
    }

    /**
     * @return 是否需要判断返回的action字段
     */
    protected boolean judgeActionEqual() {
        return true;
    }
}
