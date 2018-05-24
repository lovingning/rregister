package com.knowledge.mnlin.rregister.receivers;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.NetworkUtils;
import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

/**
 * Created on 2018/5/21  19:18
 * function : 监听网络变化
 *
 * @author mnlin
 */
public class NetReceiver extends BaseReceiver<NetworkUtils.NetworkType> {

    /**
     * 有新短信时系统发出的广播，有序，可拦截。
     */
    public NetReceiver(@NonNull HttpCallback<NetworkUtils.NetworkType> httpCallback) {
        super(httpCallback,"android.net.conn.CONNECTIVITY_CHANGE");
    }

    /**
     * 处理短信结果
     */
    protected NetworkUtils.NetworkType apply(Intent intent) {
        return NetworkUtils.getNetworkType();
    }
}
