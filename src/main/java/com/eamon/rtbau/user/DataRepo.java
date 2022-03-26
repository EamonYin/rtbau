package com.eamon.rtbau.user;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataRepo {
    private static Map<String, String> data = new HashMap();

    public static void put(String key, String value) {
        data.put(key, value);
    }

    public static String get(String key) {
        return data.get(key);
    }

    public static void remove(String key) {
        data.remove(key);
    }

    public static void clear() {
        data.clear();
    }
}
