package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Employee;
import com.supermarket.backend.entity.Product;
import com.supermarket.backend.entity.SalesRecord;
import com.supermarket.backend.repository.EmployeeRepository;
import com.supermarket.backend.repository.ProductRepository;
import com.supermarket.backend.repository.SalesRecordRepository;
import com.supermarket.backend.service.ProductCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cashier")
public class CashierController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private SalesRecordRepository salesRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private ProductCacheService productCacheService;

    // 获取所有商品（Redis 缓存）
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productCacheService.getAllProducts();
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
            Product product = productRepo.findByIdForUpdate(productId);

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
        productCacheService.evict();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("change", change);
        return result;
    }
    // 查询最近的销售记录（用于退货时查找原单）
    @GetMapping("/sales")
    public List<Map<String, Object>> getRecentSales() {
        List<SalesRecord> records = salesRepo.findAll(Sort.by(Sort.Direction.DESC, "saleTime"));
        List<Map<String, Object>> result = new ArrayList<>();

        // 只取最近 50 条，避免数据太多
        for (SalesRecord record : records.stream().limit(50).collect(Collectors.toList())) {
            if (record.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) { // 只显示正常销售，不显示退货记录
                Product product = productRepo.findById(record.getProductId()).orElse(null);
                Employee cashier = employeeRepo.findById(record.getCashierId()).orElse(null);

                Map<String, Object> item = new HashMap<>();
                item.put("id", record.getId());
                item.put("productId", record.getProductId());
                item.put("productName", product != null ? product.getName() : "未知商品");
                item.put("quantity", record.getQuantity());
                item.put("totalPrice", record.getTotalPrice());
                item.put("cashierName", cashier != null ? cashier.getName() : "未知");
                item.put("saleTime", record.getSaleTime().toString());
                result.add(item);
            }
        }
        return result;
    }

    // 基于销售记录退货
    @PostMapping("/refund/{saleId}")
    public Map<String, Object> refundBySale(@PathVariable Integer saleId) {
        Map<String, Object> result = new HashMap<>();

        SalesRecord original = salesRepo.findById(saleId).orElse(null);
        if (original == null) {
            result.put("success", false);
            result.put("message", "销售记录不存在");
            return result;
        }

        // 检查这笔记录是不是已经被退货过了
        if (original.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            result.put("success", false);
            result.put("message", "这笔记录本身是退货记录，不能再次退货");
            return result;
        }

        // 恢复库存
        Product product = productRepo.findById(original.getProductId()).orElse(null);
        if (product != null) {
            product.setStock(product.getStock() + original.getQuantity());
            productRepo.save(product);
        }

        // 生成退货冲销记录
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee cashier = employeeRepo.findByUsername(username);

        SalesRecord refundRecord = new SalesRecord();
        refundRecord.setProductId(original.getProductId());
        refundRecord.setQuantity(original.getQuantity());
        refundRecord.setTotalPrice(original.getTotalPrice().negate()); // 负数冲销
        refundRecord.setCashierId(cashier.getId());
        refundRecord.setSaleTime(LocalDateTime.now());
        salesRepo.save(refundRecord);
        productCacheService.evict();

        result.put("success", true);
        result.put("message", "退货成功，已退款 " + original.getTotalPrice() + " 元");
        result.put("productName", product != null ? product.getName() : "未知商品");
        return result;
    }
}