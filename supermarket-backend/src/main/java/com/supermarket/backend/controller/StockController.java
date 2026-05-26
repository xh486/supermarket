package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Category;
import com.supermarket.backend.entity.Product;
import com.supermarket.backend.entity.PurchaseRecord;
import com.supermarket.backend.repository.CategoryRepository;
import com.supermarket.backend.repository.ProductRepository;
import com.supermarket.backend.repository.PurchaseRecordRepository;
import com.supermarket.backend.service.ProductCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private PurchaseRecordRepository purchaseRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ProductCacheService productCacheService;

    // 待入库记录
    @GetMapping("/pending")
    public List<PurchaseRecord> getPending() {
        return purchaseRepo.findByStatus(0);
    }

    // 确认入库
    @PutMapping("/confirm/{id}")
    public Map<String, Object> confirmStock(@PathVariable Integer id) {
        PurchaseRecord pr = purchaseRepo.findById(id).orElse(null);
        Map<String, Object> res = new HashMap<>();
        if (pr != null && pr.getStatus() == 0) {
            Product product = productRepo.findById(pr.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + pr.getQuantity());
                productRepo.save(product);
            }
            pr.setStatus(1);
            purchaseRepo.save(pr);
            productCacheService.evict();
        }
        res.put("success", true);
        return res;
    }

    // 全部库存
    @GetMapping("/inventory")
    public List<Product> getInventory() {
        return productRepo.findAll();
    }

    // 库存预警
    @GetMapping("/alerts")
    public List<Product> getAlerts() {
        return productRepo.findByStockLessThanEqual(10);
    }

    // 分类管理
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepo.findAll();
    }

    @PostMapping("/category")
    public Category addCategory(@RequestBody Category category) {
        return categoryRepo.save(category);
    }
}