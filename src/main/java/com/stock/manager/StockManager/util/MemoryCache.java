package com.stock.manager.StockManager.util;

import java.util.*;

import com.stock.manager.StockManager.models.Product;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;


/**
 * Created by Chaklader on 2019-03-03.
 */
public class MemoryCache<K, V> {

    private long timeToLive;
    private LRUMap lruMap;

    /**
     * custom class that stores the cache value
     * and the timestamp for the last access
     */
    protected class CacheObject {

        public long lastAccessed = System.currentTimeMillis();
        public V value;

        protected CacheObject(V value) {
            this.value = value;
        }
    }

    /**
     * @param timeToLive    this is the permitted period of time for an object to live since
     *                      they are last accessed.
     *
     *                      <p>
     * @param timerInterval For the expiration of items use the timestamp of the last access
     *                      and in a separate thread remove the items when the time to live
     *                      limit is reached. This is nice for reducing memory pressure for
     *                      applications that have long idle time in between accessing the
     *                      cached objects. We have disabled the cleanup for this case scenario
     *
     *                      <p>
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
                         * clean the objects from the cache that has reached
                         * the timeToLive period after the last access.
                         * */
                        cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }


    /**
     * insert a new key and value inside the cache memory
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {

        synchronized (lruMap) {

            /**
             * we have reached the max. size of items decided for the cache
             * and hence, we are not allowed to add more items for now. We
             * will need for the chache cleaning to add further items.
             */
            if (lruMap.isFull()) {
                return;
            }

            lruMap.put(key, new CacheObject(value));
        }
    }


    /**
     * retrieve the cache object from the memory using the key
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public V get(K key) {

        synchronized (lruMap) {

            CacheObject object = (CacheObject) lruMap.get(key);

            if (object == null)
                return null;

            else {
                object.lastAccessed = System.currentTimeMillis();
                return object.value;
            }
        }
    }

    /**
     * remove a cache object from the memory using the key
     *
     * @param key
     */
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


    /**
     * we will look after the cache objects with a certain interval (ie timerInterval)
     * that has stayed in the memory inactively more than the time to live period and
     * remove them iteratively.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (lruMap) {

            MapIterator iterator = lruMap.mapIterator();

            deleteKey = new ArrayList<K>((lruMap.size() / 2) + 1);

            K key = null;
            CacheObject object = null;

            while (iterator.hasNext()) {

                key = (K) iterator.next();
                object = (CacheObject) iterator.getValue();

                if (object != null && (now > (object.lastAccessed + timeToLive))) {
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

    /**
     * convert the cache full of items to regular HashMap with the same
     * key and value pair
     *
     * @return
     */
    public Map<Product, Integer> convertToMap() {

        synchronized (lruMap) {

            Map<Product, Integer> map = new HashMap<>();
            MapIterator iterator = lruMap.mapIterator();

            K k = null;
            V v = null;

            CacheObject o = null;

            while (iterator.hasNext()) {

                k = (K) iterator.next();
                v = (V) iterator.getValue();

                Product product = (Product) k;
                int value = Integer.parseInt(String.valueOf(v));

                map.put(product, value);
            }

            return map;
        }
    }

}