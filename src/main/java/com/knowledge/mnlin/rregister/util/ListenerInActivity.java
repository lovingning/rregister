package com.knowledge.mnlin.rregister.util;

/**
 * Created on 2018/5/21  17:22
 * function : 动态移除监听器
 *
 * @author mnlin
 */
public interface ListenerInActivity {
    /**
     * 当activity被关掉时,添加监听
     */
    void listenerOnDestroy(HttpCallback<Object> callback);

    /**
     * 当activity不可见时,添加监听
     */
    void listenerOnStop(HttpCallback<Object> callback);
}
