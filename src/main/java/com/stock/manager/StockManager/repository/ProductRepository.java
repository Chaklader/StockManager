package com.stock.manager.StockManager.repository;

import com.stock.manager.StockManager.models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Chaklader on 2019-03-03.
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, String> {

    @Query(value = "SELECT * FROM StockHandler.product WHERE DATE(stock_timestamp) BETWEEN  DATE(:startDate) AND DATE(:endDate) ORDER BY quantity DESC LIMIT 3 ", nativeQuery = true)
    Optional<List<Product>> findTopThreeAvailableProducts(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
