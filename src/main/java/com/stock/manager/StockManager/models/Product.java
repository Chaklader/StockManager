package com.stock.manager.StockManager.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by Chaklader on 2019-03-03.
 */
@Entity
public class Product {

    @Id
    @Column(name = "productId")
    private String productId;

    @Column(name = "stockId")
    private String id;

    @Column(name = "stock_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Timestamp timestamp;

    @Column(name = "quantity")
    private int quantity;


    public Product() {
    }

    public Product(String productId, Timestamp requestTimestamp, String id, Timestamp timestamp, int quantity) {
        this.productId = productId;
        this.id = id;
        this.timestamp = timestamp;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getQuantity() == product.getQuantity() &&
                getProductId().equals(product.getProductId()) &&
                getId().equals(product.getId()) &&
                getTimestamp().equals(product.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getId(), getTimestamp(), getQuantity());
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", quantity=" + quantity +
                '}';
    }
}
