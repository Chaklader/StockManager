package com.stock.manager.StockManager.service;

import com.stock.manager.StockManager.models.Product;
import com.stock.manager.StockManager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor = Exception.class)
    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<List<Product>> findTopThreeAvailableProducts(String start, String end) {

        return repository.findTopThreeAvailableProducts(start, end);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Product> findAll() {
        return (List<Product>) repository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Product save(Product Product) {
        return repository.save(Product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        repository.deleteAll();
    }
}
