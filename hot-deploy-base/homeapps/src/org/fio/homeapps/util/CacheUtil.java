package org.fio.homeapps.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ofbiz.base.util.UtilValidate;

/**
 * @author Sharif
 *
 */
public class CacheUtil {

	private static CacheUtil instance;
    private static Object monitor = new Object();
    private Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String, Object>());
    //private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    static {
    	instance = new CacheUtil();
    }
    
    private CacheUtil() {
    }

    public void put(String cacheKey, Object value) {
        cache.put(cacheKey, value);
    }

    public Object get(String cacheKey) {
        return cache.get(cacheKey);
    }
    
    public boolean contains(String cacheKey) {
        return cache.containsKey(cacheKey);
    }
    
    public boolean notContains(String cacheKey) {
        return !contains(cacheKey);
    }

    public void clear(String cacheKey) {
        cache.put(cacheKey, null);
    }

    public void clear() {
        cache.clear();
    }

    public static CacheUtil getInstance() {
        /*if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new CacheUtil();
                }
            }
        }*/
        return instance;
    }
    
}
