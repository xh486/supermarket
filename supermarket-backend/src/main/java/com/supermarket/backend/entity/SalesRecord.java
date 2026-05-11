package com.supermarket.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class SalesRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Integer cashierId;
    private LocalDateTime saleTime;
}