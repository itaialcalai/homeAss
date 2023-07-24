package com.genoox.homeAss.util;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private Map<String, String> cache;

    public Cache() {
        this.cache = new HashMap<>();
    }

    public String get(String key) {
        return cache.get(key);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }
}
