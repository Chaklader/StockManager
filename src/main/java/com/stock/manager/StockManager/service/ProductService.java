package com.stock.manager.StockManager.service;

import com.stock.manager.StockManager.models.Product;
import com.stock.manager.StockManager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Chaklader on 2019-03-03.
 */
@Service
public class ProductService {


    private ProductRepository repository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.repository = productRepository;
    }

//    public Optional<Product> findByProductId(String id) {
//        return repository.findByProductId(id);
//    }

    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    public Optional<List<Product>> findTopThreeAvailableProducts(String start, String end) {

        return repository.findTopThreeAvailableProducts(start, end);
    }

    public List<Product> findAll() {
        return (List<Product>) repository.findAll();
    }

//    public List<Product> findAllWithCreationDateTimeBefore(Date date) {
//        return (List<Product>) repository.findAllWithCreationDateTimeBefore(date);
//    }

//    public List<Product> findAllWithCreationRange(Date start, Date end) {
//        return (List<Product>) repository.findAllWithCreationRange(start, end);
//    }

//    public List<Product> findByProductId(String productId) {
//
//        return (List<Product>) repository.findByProductId(productId);
//    }

    public Product save(Product Product) {
        return repository.save(Product);
    }

//    public void deleteById(Long id) {
//        repository.deleteById(id);
//    }

    public void deleteAll() {
        repository.deleteAll();
    }

//    public Product findByProductId(String productId) {
//
//        return repository.findByProductId(productId);
//    }
}
