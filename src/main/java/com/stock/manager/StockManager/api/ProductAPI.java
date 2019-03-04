package com.stock.manager.StockManager.api;


import com.stock.manager.StockManager.models.Product;
import com.stock.manager.StockManager.service.ProductService;
import com.stock.manager.StockManager.util.MemoryCache;
import com.stock.manager.StockManager.util.SortingHelper;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by Chaklader on 2/25/19.
 */

// product.setRequestTimestamp(Timestamp.from(Instant.now()));

@RestController
@RequestMapping("/api/v1/products")
public class ProductAPI {


    MemoryCache<String, Integer> cache = new MemoryCache<String, Integer>(200, 500, 100);

    private ProductService service;

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
            cache.put(product.getProductId(), 0);

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

            service.save(product);

            /*
             * If we have fewer stocks than earlier record, the difference is already sold.
             * Be mindful, that the company could introduce new stocks in the market (ie
             * stock split etc) but this is not our concern. We will only count as sold when
             * the current quantity is lesser than the earlier record.
             *
             * */
            if (product.getQuantity() < prod.getQuantity()) {

                int currentSales = prod.getQuantity() - product.getQuantity();
                int previousSales = cache.get(productId);

                /*
                 * update the stock sales record
                 * */
                cache.put(productId, previousSales + currentSales);
            }

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


    // $ curl -X GET http://localhost:8080/api/v1/products/findTopThree?duration=today | jq
    // $ curl -X GET http://localhost:8080/api/v1/products/findTopThree?duration=lastMonth | jq
    @GetMapping("/findTopThree")
    public ResponseEntity<List<Product>> findTopThreeAvailableProducts(@RequestParam("duration") String duration) {

        LocalDate now = LocalDate.now(ZoneOffset.UTC);

        String start;
        String end;

        if (duration.equals("today")) {

            start = now.toString();
            end = now.toString();
        } else if (duration.equals("lastMonth")) {

            LocalDate lastMonth = now.minus(1, ChronoUnit.MONTHS);

            LocalDate firstDate = lastMonth.withDayOfMonth(1);
            LocalDate lastDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

            start = firstDate.toString();
            end = lastDate.toString();

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Optional<List<Product>> products = service.findTopThreeAvailableProducts(start, end);

        if (!products.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(products.get());
    }


    public Map<String, Integer> findTopSellingProducts() {

        Map<String, Integer> map = SortingHelper.SortMapBasedOnValues(cache.convertToMap(), 3);
        return map;
    }

}
