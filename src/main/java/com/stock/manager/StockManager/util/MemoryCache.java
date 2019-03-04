package com.stock.manager.StockManager.util;

import java.util.*;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

import static java.util.stream.Collectors.toMap;


/**
 * Created by Chaklader on 2019-03-03.
 */
public class MemoryCache<K, T> {


    private long timeToLive;
    private LRUMap lruMap;

    protected class CacheObject {

        public long lastAccessed = System.currentTimeMillis();
        public T value;

        protected CacheObject(T value) {
            this.value = value;
        }
    }

    /**
     * @param timeToLive    Items will expire based on a time to live period.
     * @param timerInterval For the expiration of items use the timestamp of the last access
     *                      and in a separate thread remove the items when the time to live
     *                      limit is reached. This is nice for reducing memory pressure for
     *                      applications that have long idle time in between accessing the
     *                      cached objects. We have disabled the cleanup for this case scenario
     * @param maxItems      Cache will keep most recently used items if we will try to add more
     *                      items then max specified. The Apache common collections has a LRUMap,
     *                      which, removes the least used entries from a fixed sized map
     */
    public MemoryCache(long timeToLive, final long timerInterval, int maxItems) {

        this.timeToLive = timeToLive * 1000;

        lruMap = new LRUMap(maxItems);

        if (this.timeToLive > 0 && timerInterval > 0) {

            Thread t = new Thread(new Runnable() {

                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timerInterval * 1000);
                        } catch (InterruptedException ex) {
                        }

                        /*
                         *
                         * */
                        // cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    public void put(K key, T value) {
        synchronized (lruMap) {
            lruMap.put(key, new CacheObject(value));
        }
    }

    @SuppressWarnings("unchecked")
    public T get(K key) {

        synchronized (lruMap) {

            CacheObject c = (CacheObject) lruMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (lruMap) {
            lruMap.remove(key);
        }
    }

    public int size() {
        synchronized (lruMap) {
            return lruMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (lruMap) {
            MapIterator itr = lruMap.mapIterator();

            deleteKey = new ArrayList<K>((lruMap.size() / 2) + 1);
            K key = null;
            CacheObject c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject) itr.getValue();

                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (lruMap) {
                lruMap.remove(key);
            }

            Thread.yield();
        }
    }


    public Map<String, Integer> convertToMap() {

        synchronized (lruMap) {

            Map<String, Integer> m = new HashMap<>();

            MapIterator iterator = lruMap.mapIterator();

            CacheObject d = null;
            CacheObject c = null;

            while (iterator.hasNext()) {

                d = (CacheObject) iterator.getKey();
                c = (CacheObject) iterator.getValue();

                if (c != null && d != null) {

                    String key = String.valueOf(d);
                    String value = String.valueOf(c);

                    m.put(key, Integer.parseInt(value));
                }
            }

            return m;
        }
    }

}