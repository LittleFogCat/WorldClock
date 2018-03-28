package com.clearcrane.worldclock;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Locale;

/**
 * Created by jjy on 2018/3/27.
 * <p>
 * 分成24个时区。
 * 指针在离屏幕左方12时区的位置。初始时区是0，
 * 每移动一次，时区变化1；通过偏移量除以每个时区的宽度即可得到当前时区。
 */

public class TimezoneView extends CircularView {
    private static final String TAG = "TimeRegionView";
    private static final int TIMEZONE_COUNT = 24;

    private int mCurrentTimezone = 0;// 当前时区

    public TimezoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimezoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 通过当前图片的位置，计算当前所在的时区。
     * 如何计算？
     */
    public int calCurrentTimezone() {
        float leftX = getLeftView().getX();
        int timezone = -Math.round(leftX / (getImageWidth() / TIMEZONE_COUNT));
        if (timezone > 12) {
            timezone -= 24;
        } else if (timezone < -12) {
            timezone += 24;
        }
        Log.d(TAG, "calCurrentTimezone: Timezone = " + timezone);
        return timezone;
    }

    public static String getTimezoneString(int zone, String languageCode) {
        if (zone < -11 || zone > 12) {
            return null;
        }
        String[] chnRegion = {"西十一区", "西十区", "西九区", "西八区", "西七区", "西六区", "西五区", "西四区", "西三区",
                "西二区", "西一区", "零时区", "东一区", "东二区", "东三区", "东四区", "东五区", "东六区", "东七区",
                "东八区", "东九区", "东十区", "东十一区", "十二区"};
        if ("ZH-CN".equalsIgnoreCase(languageCode) || "ZHCN".equalsIgnoreCase(languageCode)
                || "CHN".equalsIgnoreCase(languageCode) || "CN".equalsIgnoreCase(languageCode)) {
            return chnRegion[zone + 11];
        } else {
            return "GMT" + (zone > 0 ? "+" + zone : zone);
        }

    }

    public static String getTimezoneString(int zone, Locale locale) {
        if (locale.getLanguage().equals(Locale.CHINA.getLanguage())) {
            return getTimezoneString(zone, "ZH-CH");
        }

        return getTimezoneString(zone, "en");
    }

    public void setTimezone(int zone) {
        float zoneWidth = getImageWidth() / TIMEZONE_COUNT;
        int offset = zone - mCurrentTimezone;
        move(-offset * zoneWidth);
    }

}
