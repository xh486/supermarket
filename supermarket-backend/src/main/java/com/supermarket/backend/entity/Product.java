package com.supermarket.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer categoryId;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private String barcode;
    private Integer supplierId;
    private LocalDateTime cacheExpireTime;


}