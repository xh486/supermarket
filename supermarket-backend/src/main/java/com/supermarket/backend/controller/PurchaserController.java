package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Employee;
import com.supermarket.backend.entity.Product;
import com.supermarket.backend.entity.PurchaseRecord;
import com.supermarket.backend.entity.Supplier;
import com.supermarket.backend.repository.EmployeeRepository;
import com.supermarket.backend.repository.ProductRepository;
import com.supermarket.backend.repository.PurchaseRecordRepository;
import com.supermarket.backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/purchaser")
public class PurchaserController {

    @Autowired
    private SupplierRepository supplierRepo;

    @Autowired
    private PurchaseRecordRepository purchaseRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    // 供货商管理
    @GetMapping("/suppliers")
    public List<Supplier> getSuppliers() {
        return supplierRepo.findAll();
    }

    @PostMapping("/supplier")
    public Supplier addSupplier(@RequestBody Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    @PutMapping("/supplier")
    public Supplier updateSupplier(@RequestBody Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    @DeleteMapping("/supplier/{id}")
    public void deleteSupplier(@PathVariable Integer id) {
        supplierRepo.deleteById(id);
    }

    // 获取所有商品
    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepo.findAll();
    }

    // 创建采购单
    @PostMapping("/purchase")
    public PurchaseRecord addPurchase(@RequestBody PurchaseRecord record) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee purchaser = employeeRepo.findByUsername(username);
        record.setPurchaserId(purchaser.getId());
        record.setStatus(0);
        record.setPurchaseTime(LocalDateTime.now());
        return purchaseRepo.save(record);
    }
}