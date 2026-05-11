package com.supermarket.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String contactPerson;
    private String phone;
    private String address;
}