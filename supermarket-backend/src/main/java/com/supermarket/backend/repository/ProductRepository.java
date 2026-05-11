package com.supermarket.backend.repository;

import com.supermarket.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStockLessThanEqual(Integer minStock);
}