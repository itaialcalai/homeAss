// Itai Alcalai 206071110
package com.genoox.homeAss.util;

// Import necessary classes
import java.util.HashMap;
import java.util.Map;

// Cache class for caching key-value pairs
public class Cache {

    // An instance of HashMap to store the cache data
    private Map<String, String> cache;

    // Constructor to initialize the HashMap instance
    public Cache() {
        this.cache = new HashMap<>();
    }

    // Method to retrieve a value from the cache by its key
    public String get(String key) {
        return cache.get(key);
    }

    // Method to add a key-value pair to the cache
    public void put(String key, String value) {
        cache.put(key, value);
    }
}

