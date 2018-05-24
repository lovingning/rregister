package com.knowledge.mnlin.rregister.receivers;

import android.content.Intent;
import android.os.BatteryManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.knowledge.mnlin.rregister.util.BaseReceiver;
import com.knowledge.mnlin.rregister.util.HttpCallback;

/**
 * Created on 2018/5/22  10:37
 * function : 监听电池变化
 *
 * @author mnlin
 */
public class BatteryReceiver extends BaseReceiver<BatteryReceiver.BatteryStatus> {

    public BatteryReceiver(@NonNull HttpCallback<BatteryReceiver.BatteryStatus> httpCallback) {
        super(httpCallback,Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    protected BatteryReceiver.BatteryStatus apply(Intent intent) {
        return BatteryStatus.analyzeBattery(intent);
    }

    /**
     * 电池状态信息
     * <p>
     * 对于Int类型,若取值为{@link BatteryStatus#VALUE_DEFAULT_ERROR},则表示异常状态,未获取到值
     */
    public static class BatteryStatus {
        /**
         * 从Intent中解析出电池信息
         *
         * @return 电池状态
         */
        public static BatteryStatus analyzeBattery(Intent intent) {
            BatteryStatus status = new BatteryStatus();

            status.health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.iconRes = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true);
            status.scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, BatteryStatus.VALUE_DEFAULT_ERROR);
            status.technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            return status;
        }

        /**
         * 未取到值或其他异常状态
         */
        public static final int VALUE_DEFAULT_ERROR = -1;

        /**
         * 电池健康状态
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_HEALTH} 字段获取
         * <p>
         * 取值为：
         * {@link android.os.BatteryManager#BATTERY_HEALTH_COLD}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_DEAD}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_GOOD}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_OVERHEAT}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_OVER_VOLTAGE}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_UNKNOWN}
         * {@link android.os.BatteryManager#BATTERY_HEALTH_UNSPECIFIED_FAILURE}
         */
        private int health;

        /**
         * 电池电量显示的图标
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_ICON_SMALL} 字段获取
         */
        @DrawableRes
        private int iconRes;

        /**
         * 电池当前的电量
         * <p>
         * 介于0和最大值之间
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_LEVEL} 字段获取
         */
        private int level;

        /**
         * 电池当前使用的电源
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_PLUGGED} 字段获取
         * <p>
         * 取值为：
         * 0 表示电池供电
         * {@link android.os.BatteryManager#BATTERY_PLUGGED_AC} 充电器
         * {@link android.os.BatteryManager#BATTERY_PLUGGED_USB} usb充电
         * {@link android.os.BatteryManager#BATTERY_PLUGGED_WIRELESS} 无线电
         */
        private int plugged;

        /**
         * 现在是否在使用电池（某些情况下，在充电时电池不可见）
         * <p>
         * 若未获取成功,则默认为true
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_PRESENT} 字段获取
         */
        private boolean present;

        /**
         * 电池电量最大值
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_SCALE} 字段获取
         */
        private int scale;

        /**
         * 电池当前状态（充电）
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_STATUS} 字段获取
         * <p>
         * 取值为：
         * {@link android.os.BatteryManager#BATTERY_STATUS_CHARGING} 正在充电
         * {@link android.os.BatteryManager#BATTERY_STATUS_DISCHARGING} 断开连接
         * {@link android.os.BatteryManager#BATTERY_STATUS_FULL} 已充满
         * {@link android.os.BatteryManager#BATTERY_STATUS_NOT_CHARGING} 未充电
         * {@link android.os.BatteryManager#BATTERY_STATUS_UNKNOWN} 未知状态
         */
        private int status;

        /**
         * 电池使用的技术，如li电池等
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_TECHNOLOGY} 字段获取
         */
        private String technology;

        /**
         * 电池温度
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_TEMPERATURE} 字段获取
         */
        private int temperature;

        /**
         * 电池电压
         * <p>
         * 通过intent的 {@link android.os.BatteryManager#EXTRA_VOLTAGE} 字段获取
         */
        private int voltage;

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getPlugged() {
            return plugged;
        }

        public void setPlugged(int plugged) {
            this.plugged = plugged;
        }

        public boolean isPresent() {
            return present;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTechnology() {
            return technology;
        }

        public void setTechnology(String technology) {
            this.technology = technology;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public int getVoltage() {
            return voltage;
        }

        public void setVoltage(int voltage) {
            this.voltage = voltage;
        }
    }
}
