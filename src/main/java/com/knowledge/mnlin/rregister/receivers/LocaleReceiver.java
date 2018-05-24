package com.knowledge.mnlin.rregister.receivers;

import android.content.Intent;

import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

import java.util.Locale;


/**
 * Created on 2018/5/22  19:20
 * function : 监听区域语言变化
 *
 * @author mnlin
 */
public class LocaleReceiver extends BaseReceiver<Locale> {

    public LocaleReceiver(@android.support.annotation.NonNull HttpCallback<Locale> callback) {
        super(callback,Intent.ACTION_LOCALE_CHANGED);
    }

    @Override
    protected Locale apply(Intent intent) {
        return context.get().getResources().getConfiguration().locale;
    }
}
