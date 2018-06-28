package com.knowledge.mnlin.rregister.util;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.util.Pair;

import com.blankj.utilcode.util.NetworkUtils;
import com.knowledge.mnlin.rregister.receivers.BatteryReceiver;
import com.knowledge.mnlin.rregister.receivers.HomeReceiver;
import com.knowledge.mnlin.rregister.receivers.LocaleReceiver;
import com.knowledge.mnlin.rregister.receivers.NetReceiver;
import com.knowledge.mnlin.rregister.receivers.SMSReceiver;
import com.knowledge.mnlin.rregister.receivers.ScreenReceiver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

import static com.knowledge.mnlin.rregister.util.RemoveTime.onDestroy;
import static com.knowledge.mnlin.rregister.util.RemoveTime.onStop;

/**
 * Created on 2018/4/11
 * function : 广播监听注册及移除
 * <p>
 * 方法中会返回监听器对象,用于提前移除
 *
 * @author ACChain
 */

public class BCRMachine {

    /**
     * 短信监听器
     * <p>
     * 需要短信读取和接收权限
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     监听器被移除的周期
     * @param callback 拦截成功的回调,first参数表示联系人,second参数表示验证码
     */
    @RequiresPermission(allOf = {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS})
    public static BroadcastReceiver registerSMSReceiver(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<Pair<String, String>> callback) {
        SMSReceiver smsReceiver = new SMSReceiver(callback);
        return commonRemove(activity, regs, time, smsReceiver.getIntentFilter(), smsReceiver);
    }

    /**
     * 动态监听电池状态变化,包括电量,温度,电压,充电状态等
     * <p>
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     移除时间
     * @param callback 回调,枚举类型
     */
    public static BroadcastReceiver registerBatteryChange(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<BatteryReceiver.BatteryStatus> callback
    ) {
        BatteryReceiver batteryReceiver = new BatteryReceiver(callback);
        return commonRemove(activity, regs, time, batteryReceiver.getIntentFilter(), batteryReceiver);
    }


    /**
     * 动态监听语言变化
     * <p>
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     移除时间
     * @param callback 回调,枚举类型
     * @return 返回当前的Locale值
     */
    public static BroadcastReceiver registerLocale(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<Locale> callback) {
        LocaleReceiver localeReceiver = new LocaleReceiver(callback);
        return commonRemove(activity, regs, time, localeReceiver.getIntentFilter(), localeReceiver);
    }

    /**
     * 动态监听网络变化
     * <p>
     * 需要网络权限
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     移除时间
     * @param callback 回调,枚举类型
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE})
    public static BroadcastReceiver registerNetStatus(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<NetworkUtils.NetworkType> callback) {
        NetReceiver netReceiver = new NetReceiver(callback);
        return commonRemove(activity, regs, time, netReceiver.getIntentFilter(), netReceiver);
    }

    /**
     * 屏幕变化(开启/关闭)
     * <p>
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     移除时间
     * @param callback 回调,枚举类型
     */
    public static BroadcastReceiver registerScreen(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<ScreenReceiver.ScreenStatus> callback) {
        ScreenReceiver screenReceiver = new ScreenReceiver(callback);
        return commonRemove(activity, regs, time, screenReceiver.getIntentFilter(), screenReceiver);
    }

    /**
     * 监听特殊键位(home键位等)
     * <p>
     *
     * @param activity activity
     * @param regs     注册机
     * @param time     移除时间
     * @param callback 回调,枚举类型
     */
    public static BroadcastReceiver registerHome(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<HomeReceiver.SpecialKey> callback) {
        HomeReceiver homeReceiver = new HomeReceiver(callback);
        return commonRemove(activity, regs, time, homeReceiver.getIntentFilter(), homeReceiver);
    }

    /**
     * 注册监听器,通过公用方法,利用反射来生成对象
     * <p>
     * 反射只调用一次,基本不会影响性能
     * <p>
     * 规定添加的监听器构造方法需要传入 callback,且构造方法访问权限为public
     * <p>
     * 泛型 F 为回调函数中需要传入的类型
     * <p>
     * 泛型 T 为新添加的广播接收者的类型
     *
     * @return 注册得到的广播监听器
     * <p>
     * 该方法只向外部提供,用于动态的添加监听器;
     * 本模块中方法不会主动去调用
     */
    public static <F, T extends BaseReceiver> BroadcastReceiver registerBroadcast(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<F> cb, Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, UnsupportedOperationException {
        Constructor<T> constructor = clazz.getConstructor(HttpCallback.class);
        T receiver = constructor.newInstance(cb);

        //获取回调接口中定义的泛型类型
        Type[] genericInterfaces = cb.getClass().getGenericInterfaces();
        Type type_callback = null;
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface.toString().contains(HttpCallback.class.getName())) {
                type_callback = genericInterface;
                break;
            }
        }
        //在java1.8后,使用lambda会导致泛型不可见,因此,如果发现泛型被移除,则不再进行判断
        if (type_callback instanceof ParameterizedType) {
            Type type_receiver = ((ParameterizedType) receiver.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            type_callback = ((ParameterizedType) type_callback).getActualTypeArguments()[0];
            //如果目标需求的参数与返回的参数不同,则默认方法调用失败
            if (!type_callback.equals(type_receiver)) {
                throw new UnsupportedOperationException("receiver接收器参数类型与需要回调的类型不符");
            }
        }

        return commonRemove(activity, regs, time, receiver.getIntentFilter(), receiver);
    }

    /**
     * 注册监听器,通过公用方法,利用反射来生成对象(不强制检查泛型是否正确)
     * <p>
     * 反射只调用一次,基本不会影响性能
     * <p>
     * 规定添加的监听器构造方法需要传入 callback,且构造方法访问权限为public
     * <p>
     * 泛型 F 为回调函数中需要传入的类型
     * <p>
     * 泛型 T 为新添加的广播接收者的类型
     *
     * @return 注册得到的广播监听器
     * <p>
     * 该方法只向外部提供,用于动态的添加监听器;
     * 本模块中方法不会主动去调用
     */
    public static <F, T extends BaseReceiver> BroadcastReceiver registerBroadcastWithoutCheck(Activity activity, ListenerInActivity regs, RemoveTime time, HttpCallback<F> cb, Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, UnsupportedOperationException {
        Constructor<T> constructor = clazz.getConstructor(HttpCallback.class);
        T receiver = constructor.newInstance(cb);
        return commonRemove(activity, regs, time, receiver.getIntentFilter(), receiver);
    }

    /**
     * 移除监听
     */
    private static BroadcastReceiver commonRemove(Activity activity, ListenerInActivity regs, RemoveTime time, IntentFilter filter, BroadcastReceiver receiver) {
        activity.registerReceiver(receiver, filter);
        Log.v(BCRMachine.class.getName(), "已注册监听器..." + receiver.getClass().getName());

        if (time == onStop) {
            regs.listenerOnStop(tag -> {
                activity.unregisterReceiver(receiver);
                Log.v(BCRMachine.class.getName(), "已移除监听器..."+ receiver.getClass().getName());
            });
        } else if (time == onDestroy) {
            regs.listenerOnDestroy(tag -> {
                activity.unregisterReceiver(receiver);
                Log.v(BCRMachine.class.getName(), "已移除监听器..."+ receiver.getClass().getName());
            });
        }

        return receiver;
    }
}
