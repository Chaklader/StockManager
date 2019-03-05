package com.stock.manager.StockManager.api;


import com.stock.manager.StockManager.models.Product;
import com.stock.manager.StockManager.service.ProductService;
import com.stock.manager.StockManager.util.MemoryCache;
import com.stock.manager.StockManager.util.SortingHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by Chaklader on 2/25/19.
 */

/**/
@RestController
@RequestMapping("/api/v1/products")
public class ProductAPI {


    /**
     * we will store the product IDs and the number of items sold in the cache object for a duration
     * of 1800s since the item was last accessed. We will be able to store for the maximum of 10,000
     * products info and impose a clean up policies in each 500s the reduce the memory pressure.
     */
    MemoryCache<Product, Integer> cache = new MemoryCache<>(1800, 500, 10000);

    private ProductService service;

    /**
     * the setter based wiring is used which utilized the reflection based
     * dependency injection inside the IoC container, but, still enable the
     * programmer to map the dependency manually while testing.
     *
     * @param service
     */
    @Autowired
    public void setService(ProductService service) {
        this.service = service;
    }


    /**
     * This end-point POST the product data inside the storage and return the create status (201)
     * Initially, it looks for stock for the respective product ID and if exists and have a older
     * entry in the storage, then the POST request proceed and override the existing information.
     * If the already stored entry has newer timestamp than the request, then it return 204 HTTP
     * status.
     * <p>
     * If there is existing entry, it stores the product information in the database.
     * <p>
     * We can use a similar cURL request for the POST call,
     * <p>
     * $ curl -i -X POST -H "Content-Type:application/json" -d "{ \"productId\": \"Product ID\", \"id\": \"Stock ID\", \"timestamp\": \"2014-01-16T22:54:01.754Z\", \"quantity\": \"250\"}" http://localhost:8080/api/v1/products/updateStock
     *
     * @param product the product data we intened to store in the database
     * @return HTTP status 201 if new information is stored and 204 if there is no change
     */
    @PostMapping(value = "/updateStock", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        /*
         * check if a newer stock was processed earlier
         * */
        String productId = product.getProductId();

        Optional<Product> optional = service.findById(productId);

        Product prod;

        /*
         *
         * the product was not in the storage, hence, store and retrun 201 status
         * */
        if (!optional.isPresent()) {

            service.save(product);
            cache.put(product, 55);

            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        }

        /*
         * the product is already in the storage, so, retrieve the product
         * */
        prod = optional.get();

        /*
         * we have a newer stock, so, store inside
         * */
        if (product.getTimestamp().compareTo(prod.getTimestamp()) >= 0) {

            /*
             * If we have fewer stocks than earlier record, the difference is already sold.
             * Be mindful, that the company could introduce new stocks in the market (ie
             * stock split etc) but this is not our concern. We will only count as sold when
             * the current quantity is lesser than the earlier record.
             * */
            if (product.getQuantity() < prod.getQuantity()) {

                int currentSales = prod.getQuantity() - product.getQuantity();

                if(cache.get(prod) !=null){

                    int previousSales = cache.get(prod);

                    System.out.println("Miami");

                    /*
                     * update the stock sales record
                     * */
                    cache.put(product, previousSales + currentSales);
                }
            }

            service.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        }

        /*
         * outdated stock, because a newer stock was processed first
         * */
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * retrieve the product info from the storage and diplay to the user
     * We can achieve purpose using a similar cURL GET request,
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/products/stock?productId=Product%20ID | jq
     *
     * @param productId the product ID that we look for in the storage
     * @return a JSON response with the product info (incl. the request timestamp)
     */
    @GetMapping("/stock")
    public ResponseEntity<Object> findById(@RequestParam("productId") String productId) {

        Optional<Product> optionalProduct = service.findById(productId);

        Product product;

        /*
         * the product doesn't exist in the storage
         * */
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        /*
         * we have the product in the storage
         * */
        product = optionalProduct.get();


        JSONObject productData = new JSONObject();
        JSONObject stockData = new JSONObject();

        productData.put("productId", product.getProductId());
        productData.put("requestTimestamp", Instant.now().toString().replaceFirst("....$", "Z"));

        stockData.put("id", product.getId());

        /*
         * format to the standard UTC before display to the user
         * */
        String format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(product.getTimestamp());
        stockData.put("timestamp", format);

        stockData.put("quantity", product.getQuantity());

        productData.put("stock", stockData);

        return ResponseEntity.status(HttpStatus.CREATED).body(productData.toString());
    }


    /**
     * the end-point will provide the top 3 available and top 3 sold products
     * with the UTC timestamp of when the info is request and the time range
     * for the query.
     * <p>
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/products/statistics?time=today | jq
     * $ curl -X GET http://localhost:8080/api/v1/products/statistics?time=lastMonth | jq
     *
     * @param time will only accept value as "today" or "lastMonth" and query on the
     *             respective range
     *             <p>
     * @return JSON string with the statistics info for the products
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getStockStatistics(@RequestParam("time") String time) {


        /*
         * the primary obejct to append the JSON info
         * */
        JSONObject statistics = new JSONObject();

        /*
         * top 3 available products info
         * */
        JSONArray topAvailableProducts = new JSONArray();

        /*
         * top 3 selling products info
         * */
        JSONArray topSellingProducts = new JSONArray();

        /*
         * append the info to a JSON object
         * */
        statistics.put("requestTimestamp", Instant.now());

        if (time.equals("today") || time.equals("lastMonth")) {
            statistics.put("range", time);
        } else {
            statistics.put("range", "");
        }

        Optional<List<Product>> optional = findTopThreeAvailableProducts(time);

        List<Product> products = null;
        JSONObject object = null;

        if (!optional.isPresent()) {

            statistics.append("topAvailableProducts", "[]");
        }

        /*
         * we have products availability information
         * */
        else {

            products = optional.get();

            for (int i = 0; i < products.size(); i++) {


                Product product = products.get(i);

                object = new JSONObject();

                object.put("id", product.getId());
                object.put("timestamp", product.getTimestamp());
                object.put("productId", product.getProductId());
                object.put("quantity", product.getQuantity());

                topAvailableProducts.put(i, object);
            }

            statistics.append("topAvailableProducts", topAvailableProducts);
        }

        Map<Product, Integer> map = findTopSellingProducts(3, time);

        if (map == null || map.isEmpty()) {
            statistics.append("topSellingProducts", "[]");
        }

        /*
         * we have products selling information
         * */
        else {
            int index = 0;

            for (Map.Entry<Product, Integer> entry : map.entrySet()) {

                String key = entry.getKey().getProductId();
                int value = entry.getValue();

                if (key != null && !key.isEmpty() && value > 0) {

                    object = new JSONObject();

                    object.put("productId", key);
                    object.put("itemsSold", value);

                    topSellingProducts.put(index, object);

                    index++;
                }
            }

            statistics.append("topSellingProducts", topSellingProducts);
        }

        /*
         * ask the GC to take care of the object
         * */
        object = null;

        return ResponseEntity.status(HttpStatus.CREATED).body(statistics.toString());
    }


    /**
     * find the top three available products based on their stock quantity
     * from the database. The method only accept parameters of "today" or
     * "lastMonth" as the range to query inside the database. If anything
     * else is provided as the range argument, it will return an empty list.
     *
     * @param range the range for the query and only accept "today" or "lastMonth"
     *              as an argument
     * @return An Optional list of top 3 availble products from the database
     */
    public Optional<List<Product>> findTopThreeAvailableProducts(String range) {

        LocalDate now = LocalDate.now(ZoneOffset.UTC);

        String start;
        String end;

        if (range.equalsIgnoreCase("today")) {

            start = now.toString();
            end = now.toString();
        } else if (range.equalsIgnoreCase("lastMonth")) {

            LocalDate lastMonth = now.minus(1, ChronoUnit.MONTHS);

            LocalDate firstDate = lastMonth.withDayOfMonth(1);
            LocalDate lastDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

            start = firstDate.toString();
            end = lastDate.toString();

        } else {
            return Optional.empty();
        }

        Optional<List<Product>> products = service.findTopThreeAvailableProducts(start, end);

        if (!products.isPresent()) {
            return Optional.empty();
        }

        return products;
    }


    /**
     * the function oversees the cache storage and find the top "n" top selling products
     *
     * @param n count of the top selling produts need to retrieve
     * @return Map with items where key is the product ID and the count of sales as value
     */

    /*
     *
     * NEED TO FIND THE VALUES BASED ON THE TIME STAMP
     * */
    public Map<Product, Integer> findTopSellingProducts(int n, String time) {

        if (!time.equalsIgnoreCase("today")
                && !time.equalsIgnoreCase("lastMonth")) {

            return new HashMap<>();
        }

        Map<Product, Integer> cacheValues = cache.convertToMap();

        Map<Product, Integer> map = SortingHelper.SortMapBasedOnValues(cacheValues, n, time);
        return map;
    }

}
