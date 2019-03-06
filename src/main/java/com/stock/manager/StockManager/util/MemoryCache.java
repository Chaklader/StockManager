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
     * custom class that stores the cache value and the last access timestamp
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
     * @param timerInterval the frequency for the time interval to impose the cleanup policy.
     *                      This is necessary te reduce the memory pressure where the memory
     *                      is critical.
     *
     *                      <p>
     * @param maxItems      Cache will keep most recently used items if we will try to add more
     *                      items then max specified.
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
                         * the "time to live" period after the last access.
                         * */
                        cleanup();
                    }
                }
            });

            /*
             * The Daemon is a low priority thread which will run in the background to perform the cleaning
             * process and the GC doesn't need to wait while it's still running to shutdown the system if
             * the app is terminated.
             * */
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

            if (key == null) {
                return;
            }

            /**
             * we have reached the max. size of items decided for the cache
             * and hence, we are not allowed to add more items for now. We
             * will need for the cache cleaning to append further items.
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

            MapIterator iterator = lruMap.mapIterator();

            K k = null;
            V v = null;

            CacheObject o = null;

            Product product = (Product) key;

            while (iterator.hasNext()) {

                k = (K) iterator.next();
                v = (V) iterator.getValue();

                Product p = (Product) k;

                if (p.getProductId().equalsIgnoreCase(product.getProductId())) {
                    o = (CacheObject) v;
                    break;
                }
            }

            if (o == null) {
                return null;
            } else {
                o.lastAccessed = System.currentTimeMillis();
                return o.value;
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

    /**
     * find the size of the memory cache
     *
     * @return size of the cache
     */
    public int size() {

        synchronized (lruMap) {
            return lruMap.size();
        }
    }


    /**
     * we will look after the cache objects with a certain time interval
     * that has stayed in the memory inactively more than the time to live
     * period and remove them iteratively.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteList = null;

        synchronized (lruMap) {

            MapIterator iterator = lruMap.mapIterator();

            deleteList = new ArrayList<K>((lruMap.size() / 2) + 1);

            K key = null;
            CacheObject o = null;

            while (iterator.hasNext()) {

                key = (K) iterator.next();
                o = (CacheObject) iterator.getValue();

                if (o != null && (now > (o.lastAccessed + timeToLive))) {
                    deleteList.add(key);
                }
            }
        }

        for (K key : deleteList) {

            synchronized (lruMap) {
                lruMap.remove(key);
            }

            /*
             * A yielding thread tells the OS (or the virtual machine etc) it's willing
             * to let other threads be scheduled in its stead. This indicates it's not
             * doing something too critical (It's only a hint, though)
             * */
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

            Map<Product, Integer> convertedMap = new HashMap<>();

            MapIterator iterator = lruMap.mapIterator();

            K k = null;
            V v = null;

            CacheObject o = null;

            while (iterator.hasNext()) {

                k = (K) iterator.next();
                v = (V) iterator.getValue();

                Product product = (Product) k;

                o = (CacheObject) v;
                int itemsSold = Integer.valueOf((o.value).toString());

                convertedMap.put(product, itemsSold);
            }

            return convertedMap;
        }
    }


//    public Map<K, V> convertToMap2() {
//
//        Map<K, V> map = new HashMap<>();
//
//        synchronized (lruMap) {
//
//            Iterator it = lruMap.entrySet().iterator();
//
//            while (it.hasNext()) {
//
//                Map.Entry entry = (Map.Entry) it.next();
//
//                K k = (K) entry.getKey();
//                V v = (V) entry.getValue();
//
//                map.put(k, v);
//            }
//
//            return map;
//        }
//    }
}