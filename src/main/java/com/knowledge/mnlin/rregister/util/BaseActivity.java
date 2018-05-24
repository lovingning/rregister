package com.knowledge.mnlin.rregister.util;

import android.app.Activity;

/**
 * Created by Administrator on 17-1-22.
 * <p>
 * activity基类,实现动态注册和移除广播监听器的逻辑
 * <p>
 * 如果无法继承baseActivity,则需要让基类activity实现 {@link ListenerInActivity} 接口,并将该类代码拷贝到基类activity中
 */
public abstract class BaseActivity extends Activity implements ListenerInActivity {
    /**
     * 注册监听器,当销毁或者是暂停的时候,用于执行回调
     */
    private HttpCallback<Object> callbackOnDestroy, callbackOnStop;

    /**
     * 当activity销毁时候,关闭资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callbackOnDestroy != null) {
            callbackOnDestroy.run(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (callbackOnStop != null) {
            callbackOnStop.run(null);
        }
    }

    /**
     * 当activity被关掉时,添加监听
     */
    @Override
    public void listenerOnDestroy(HttpCallback<Object> callback) {
        callbackOnDestroy = callback;
    }

    /**
     * 当activity不可见时,添加监听
     */
    @Override
    public void listenerOnStop(HttpCallback<Object> callback) {
        callbackOnStop = callback;
    }
}
