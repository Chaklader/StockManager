package com.stock.manager.StockManager.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Created by Chaklader on 2019-03-04.
 */
public class SortingHelper {


    public static Map<String, Integer> SortMapBasedOnValues(Map<String, Integer> map, int n) {

        Map<String, Integer> sortedDecreasingly = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(n)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return sortedDecreasingly;
    }


}
