package com.clearcrane.worldclock.utils;

import android.content.Context;
import android.text.TextUtils;

import com.clearcrane.worldclock.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jjy on 2018/3/27.
 * <p>
 * 随机工具类。
 */

public class RandomUtil {
    private static List<String> randomStrings = new ArrayList<>();
    private static boolean isInit = false;

    public static void init(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.xjb);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = br.readLine()) != null) {
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            randomStrings.add(line);
        }

        br.close();

        isInit = true;
    }

    public static char getRandomHan() {
        if (!isInit) {
            return '未';
        }
        String line = randomStrings.get(random(0, randomStrings.size()));
        return line.charAt(random(0, line.length()));
    }

    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static String randomHans(int min, int max) {
        StringBuilder s = new StringBuilder();
        while (s.length() < min) {
            s.append(getRandomHan());
        }
        while (random(0, 100) < 50 && s.length() < max) {
            s.append(getRandomHan());
        }

        return s.toString();
    }
}
