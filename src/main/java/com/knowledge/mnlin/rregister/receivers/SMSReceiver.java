package com.knowledge.mnlin.rregister.receivers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.util.Pair;

import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

/**
 * Created on 2018/4/11
 * function : 短信监听器,可以实现指定格式短信的拦截
 *
 * @author ACChain
 */

public class SMSReceiver extends BaseReceiver<Pair<String, String>> {
    /**
     * 有新短信时系统发出的广播，有序，可拦截。
     */
    public SMSReceiver(@NonNull HttpCallback<Pair<String, String>> httpCallback) {
        super(httpCallback,"android.provider.Telephony.SMS_RECEIVED");
    }

    /**
     * 处理短信结果
     */
    @Override
    protected Pair<String, String> apply(Intent intentPairFunction) {
        String contact;

        StringBuilder builder = new StringBuilder();
        Bundle bundle = intentPairFunction.getExtras();

        if (bundle != null) {
            //从Intent中获取bundle对象，此对象包含了所有的信息，短信是以“pdus”字段存储的。得到的是一个object数组，每个object都包含一条短信，（可能会获取到多条信息）。
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) {
                throw new RuntimeException("短信无内容");
            }

            //新建SmsMessage数组对象存储短信，每个SmsMessage对应一条短信类。
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            //获取得到的最末端短信的联系人地址，赋值给contact
            contact = messages[pdus.length - 1].getDisplayOriginatingAddress();

            //读取短信内容，getDisplayMessageBody()是获取正文消息。
            for (SmsMessage message : messages) {
                builder.append(message.getDisplayMessageBody());
            }

            return Pair.create(contact, builder.toString());
        }

        throw new RuntimeException("无法获取短信内容");
    }
}

