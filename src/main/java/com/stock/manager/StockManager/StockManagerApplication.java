package com.stock.manager.StockManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class StockManagerApplication {


    /**
     * load application specific stocks data for the testing purpose
     */
    public static void loadStockData() {

        String today = Instant.now().toString().replaceAll("T.*", "");

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:8080/api/v1/products/updateStock");

        List<String> entries = new ArrayList<>();


        /*
         * initial entries of stock data for today
         * */
        entries.add("{ \"productId\": \"Product ID1\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:05:00.000Z\", \"quantity\": \"25000\"}");
        entries.add("{ \"productId\": \"Product ID2\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:05:00.000Z\", \"quantity\": \"350\"}");
        entries.add("{ \"productId\": \"Product ID3\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:05:00.000Z\", \"quantity\": \"450\"}");

        entries.add("{ \"productId\": \"Product ID4\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:05:00.000\", \"quantity\": \"550\"}");
        entries.add("{ \"productId\": \"Product ID5\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:05:00.000Z\", \"quantity\": \"65000\"}");
        entries.add("{ \"productId\": \"Product ID6\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:06:00.000Z\", \"quantity\": \"750\"}");

        entries.add("{ \"productId\": \"Product ID7\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:06:00.000Z\", \"quantity\": \"85000\"}");
        entries.add("{ \"productId\": \"Product ID8\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:06:00.000Z\", \"quantity\": \"950\"}");
        entries.add("{ \"productId\": \"Product ID9\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:06:00.000Z\", \"quantity\": \"1050\"}");


        /*
         * updated info of stocks after sales for today
         * */
        entries.add("{ \"productId\": \"Product ID1\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:10:00.000Z\", \"quantity\": \"24000\"}");
        entries.add("{ \"productId\": \"Product ID2\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:11:00.000Z\", \"quantity\": \"250\"}");
        entries.add("{ \"productId\": \"Product ID3\", \"id\": \"Stock ID\", \"timestamp\":" + "\"" + today + "T06:12:00.000Z\", \"quantity\": \"350\"}");

        entries.add("{ \"productId\": \"Product ID4\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:13:00.000\", \"quantity\": \"250\"}");
        entries.add("{ \"productId\": \"Product ID5\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:14:00.000Z\", \"quantity\": \"45000\"}");
        entries.add("{ \"productId\": \"Product ID6\", \"id\": \"Stock ID1\", \"timestamp\":" + "\"" + today + "T06:15:00.000Z\", \"quantity\": \"100\"}");

        entries.add("{ \"productId\": \"Product ID7\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:16:00.000Z\", \"quantity\": \"55000\"}");
        entries.add("{ \"productId\": \"Product ID8\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:17:00.000Z\", \"quantity\": \"10\"}");
        entries.add("{ \"productId\": \"Product ID9\", \"id\": \"Stock ID2\", \"timestamp\":" + "\"" + today + "T06:18:00.000Z\", \"quantity\": \"75\"}");


        /*
         * ######################################################################################
         *
         *
         * ######################################################################################
         * */

        /*
         * initial entries of stock data for last month
         * */
        entries.add("{ \"productId\": \"Product ID10\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:05:00.000Z\", \"quantity\": \"23250\"}");
        entries.add("{ \"productId\": \"Product ID11\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:05:00.000Z\", \"quantity\": \"350\"}");
        entries.add("{ \"productId\": \"Product ID12\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:05:00.000Z\", \"quantity\": \"450\"}");

        entries.add("{ \"productId\": \"Product ID13\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:05:00.000Z\", \"quantity\": \"550\"}");
        entries.add("{ \"productId\": \"Product ID14\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:05:00.000Z\", \"quantity\": \"650\"}");
        entries.add("{ \"productId\": \"Product ID15\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:05:00.000Z\", \"quantity\": \"750\"}");

        entries.add("{ \"productId\": \"Product ID16\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:05:00.000Z\", \"quantity\": \"850\"}");
        entries.add("{ \"productId\": \"Product ID17\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:05:00.000Z\", \"quantity\": \"950\"}");
        entries.add("{ \"productId\": \"Product ID18\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:05:00.000Z\", \"quantity\": \"1050\"}");

        entries.add("{ \"productId\": \"Product ID19\", \"id\": \"Stock ID3\", \"timestamp\": \"2019-02-22T06:05:00.000Z\", \"quantity\": \"111150\"}");
        entries.add("{ \"productId\": \"Product ID20\", \"id\": \"Stock ID3\", \"timestamp\": \"2019-02-22T06:05:00.000Z\", \"quantity\": \"120\"}");
        entries.add("{ \"productId\": \"Product ID21\", \"id\": \"Stock ID3\", \"timestamp\": \"2019-02-22T06:05:00.000Z\", \"quantity\": \"1350\"}");

        entries.add("{ \"productId\": \"Product ID22\", \"id\": \"Stock ID4\", \"timestamp\": \"2019-02-24T06:05:00.000Z\", \"quantity\": \"1450\"}");
        entries.add("{ \"productId\": \"Product ID23\", \"id\": \"Stock ID4\", \"timestamp\": \"2019-02-24T06:05:00.000Z\", \"quantity\": \"1550\"}");
        entries.add("{ \"productId\": \"Product ID24\", \"id\": \"Stock ID4\", \"timestamp\": \"2019-02-24T06:05:00.000Z\", \"quantity\": \"211650\"}");

        entries.add("{ \"productId\": \"Product ID25\", \"id\": \"Stock ID5\", \"timestamp\": \"2019-02-28T06:05:00.000Z\", \"quantity\": \"1750\"}");
        entries.add("{ \"productId\": \"Product ID26\", \"id\": \"Stock ID5\", \"timestamp\": \"2019-02-28T06:05:00.000Z\", \"quantity\": \"1850\"}");
        entries.add("{ \"productId\": \"Product ID27\", \"id\": \"Stock ID5\", \"timestamp\": \"2019-02-28T06:05:00.000Z\", \"quantity\": \"1950\"}");




        /*
         * updated info of stocks after sales for last month
         * */
        entries.add("{ \"productId\": \"Product ID10\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:06:00.000Z\", \"quantity\": \"20250\"}");
        entries.add("{ \"productId\": \"Product ID11\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:07:00.000Z\", \"quantity\": \"200\"}");
        entries.add("{ \"productId\": \"Product ID12\", \"id\": \"Stock ID\", \"timestamp\": \"2019-02-04T06:08:00.000Z\", \"quantity\": \"300\"}");


        entries.add("{ \"productId\": \"Product ID13\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:06:00.000Z\", \"quantity\": \"450\"}");
        entries.add("{ \"productId\": \"Product ID14\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:07:00.000Z\", \"quantity\": \"50\"}");
        entries.add("{ \"productId\": \"Product ID15\", \"id\": \"Stock ID1\", \"timestamp\": \"2019-02-14T06:08:00.000Z\", \"quantity\": \"350\"}");

        entries.add("{ \"productId\": \"Product ID16\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:06:00.000Z\", \"quantity\": \"250\"}");
        entries.add("{ \"productId\": \"Product ID17\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:07:00.000Z\", \"quantity\": \"250\"}");
        entries.add("{ \"productId\": \"Product ID18\", \"id\": \"Stock ID2\", \"timestamp\": \"2019-02-19T06:08:00.000Z\", \"quantity\": \"450\"}");


        entries.forEach(entry -> {

                    try {

                        post.setEntity(new StringEntity(entry, Charset.forName("UTF-8")));

                        post.setHeader("Accept", "application/json");
                        post.setHeader("Content-type", "application/json");

                        HttpResponse response = client.execute(post);

                        System.out.println(EntityUtils.toString(response.getEntity()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
    }


    public static void main(String[] args) {

        SpringApplication.run(StockManagerApplication.class, args);
        loadStockData();
    }
}
