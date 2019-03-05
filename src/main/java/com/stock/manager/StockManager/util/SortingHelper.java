package com.stock.manager.StockManager.util;

import com.stock.manager.StockManager.models.Product;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Created by Chaklader on 2019-03-04.
 */
public class SortingHelper {


    public static Map<Product, Integer> SortMapBasedOnValues(Map<Product, Integer> map, int n, String time) {

        /*
         * we only keep the values provided in the time range
         * */
        Map<Product, Integer> sortedDecreasingly = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(n)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return sortedDecreasingly;
    }


}
