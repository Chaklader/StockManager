package com.stock.manager.StockManager.util;

import com.stock.manager.StockManager.models.Product;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Created by Chaklader on 2019-03-04.
 */
public class SortingHelper {


    /**
     * accept a Map with key as Product and value as the items sold. Then, filter
     * it for the given time range, sort it in descending order based on the value
     * and return only the top n items in an updated Map.
     *
     * @param map
     * @param n
     * @param time
     * @return
     */
    public static Map<Product, Integer> SortMapBasedOnValues(Map<Product, Integer> map, int n, String time) {

        Map<Product, Integer> updatedMap = new HashMap<>();

        LocalDate t = LocalDate.now(ZoneOffset.UTC);
        String today = t.toString();

        /*
         * retrieve the date a month from now
         * */
        LocalDate lastMonth = t.minus(1, ChronoUnit.MONTHS);

        /*
         * retrieve the date format of "yyyy-MM" from the "yyyy-MM-dd" format
         * */
        String monthAndYear = lastMonth.toString().substring(0, 7);

        /*
         * store only the data for today in the map
         * */
        if (time.equalsIgnoreCase("today")) {

            for (Map.Entry<Product, Integer> entry : map.entrySet()) {

                Product p = entry.getKey();
                int itemsSold = entry.getValue();

                /*
                 * format a datetime String similar of "yyyy-MM-dd HH:mm:ss.SS" to "yyyy-MM-dd"
                 * */
                String date = p.getTimestamp().toString().replaceAll(" .*", "");

                if (date.equalsIgnoreCase(today)) {
                    updatedMap.put(p, itemsSold);
                }
            }
        }

        /*
         * store only the data for last month in the map
         * */
        else if (time.equalsIgnoreCase("lastMonth")) {

            for (Map.Entry<Product, Integer> entry : map.entrySet()) {

                Product p = entry.getKey();
                int itemsSold = entry.getValue();

                /*
                 * format a datetime String similar of "yyyy-MM-dd HH:mm:ss.SS" to "yyyy-MM"
                 * */
                String date = p.getTimestamp().toString().substring(0, 7);

                if (date.equalsIgnoreCase(monthAndYear)) {
                    updatedMap.put(p, itemsSold);
                }
            }
        }

        /*
         *
         * time range is not correctly provided.
         * */
        else {
            return new HashMap<>();
        }


        /*
         * we only keep the top "n" values provided in the time range
         * */
        Map<Product, Integer> sortedDecreasingly = updatedMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(n)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return sortedDecreasingly;
    }

}
