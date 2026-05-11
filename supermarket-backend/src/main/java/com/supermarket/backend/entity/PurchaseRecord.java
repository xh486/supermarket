package com.supermarket.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class PurchaseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer productId;
    private Integer supplierId;
    private Integer quantity;
    private BigDecimal price;
    private Integer purchaserId;
    private Integer status;      // 0:待入库，1:已入库
    private LocalDateTime purchaseTime;
}