package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Employee;
import com.supermarket.backend.entity.Product;
import com.supermarket.backend.entity.SalesRecord;
import com.supermarket.backend.repository.EmployeeRepository;
import com.supermarket.backend.repository.ProductRepository;
import com.supermarket.backend.repository.SalesRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cashier")
public class CashierController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private SalesRecordRepository salesRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    // 获取所有商品
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // 结账
    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody Map<String, Object> data) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        BigDecimal paid = new BigDecimal(data.get("paid").toString());

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee cashier = employeeRepo.findByUsername(username);

        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Integer productId = (Integer) item.get("productId");
            Integer quantity = (Integer) item.get("quantity");
            Product product = productRepo.findById(productId).orElse(null);

            if (product == null || product.getStock() < quantity) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "库存不足或商品不存在");
                return error;
            }

            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subTotal);

            product.setStock(product.getStock() - quantity);
            productRepo.save(product);

            SalesRecord record = new SalesRecord();
            record.setProductId(productId);
            record.setQuantity(quantity);
            record.setTotalPrice(subTotal);
            record.setCashierId(cashier.getId());
            record.setSaleTime(LocalDateTime.now());
            salesRepo.save(record);
        }

        if (paid.compareTo(total) < 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "支付金额不足，需支付：" + total);
            return error;
        }

        BigDecimal change = paid.subtract(total);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("change", change);
        return result;
    }
}