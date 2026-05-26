package com.supermarket.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.supermarket.backend.entity.Product;
import com.supermarket.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductCacheService {
    private static final String PRODUCT_CACHE_KEY = "cashier:products";
    private static final String PRODUCT_EXPIRE_KEY = "cashier:products:expire";
    private static final String PRODUCT_LOCK_KEY = "lock:products:update";
    private static final long CACHE_TTL_MIN = 10;
    private static final long LOCK_TTL_SEC = 30;

    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() throws JsonProcessingException {
        String cacheJson = redis.opsForValue().get(PRODUCT_CACHE_KEY);
        // 缓存为空，直接走加锁加载
        if (cacheJson == null) {
            return loadProductWithLock();
        }

        List<Product> productList;
        try {
            productList = objectMapper.readValue(cacheJson, new TypeReference<List<Product>>() {});
        } catch (Exception e) {
            evict();
            return loadProductWithLock();
        }

        // 读取独立过期时间
        String expireStr = redis.opsForValue().get(PRODUCT_EXPIRE_KEY);
        if (expireStr == null) {
            evict();
            return loadProductWithLock();
        }

        LocalDateTime cacheExpireTime;
        try {
            cacheExpireTime = LocalDateTime.parse(expireStr);
        } catch (Exception e) {
            evict();
            return loadProductWithLock();
        }

        // 未过期，直接返回
        if (cacheExpireTime.isAfter(LocalDateTime.now())) {
            return productList;
        }

        // 已过期，尝试加锁
        boolean lockSuccess = redis.opsForValue()
                .setIfAbsent(PRODUCT_LOCK_KEY, "1", LOCK_TTL_SEC, TimeUnit.SECONDS);

        if (lockSuccess) {
            try {
                // ===================== 二次校验：核心防重复更新 =====================
                String doubleCheckJson = redis.opsForValue().get(PRODUCT_CACHE_KEY);
                if (doubleCheckJson != null) {
                    List<Product> tempList = objectMapper.readValue(doubleCheckJson, new TypeReference<List<Product>>() {});
                    String doubleExpire = redis.opsForValue().get(PRODUCT_EXPIRE_KEY);
                    if (doubleExpire != null) {
                        LocalDateTime doubleExpireTime = LocalDateTime.parse(doubleExpire);
                        if (doubleExpireTime.isAfter(LocalDateTime.now())) {
                            return tempList;
                        }
                    }
                }
                // 二次校验仍过期，才更新
                return loadProductWithLock();
            } finally {
                redis.delete(PRODUCT_LOCK_KEY);
            }
        }

        // 抢锁失败，返回旧缓存兜底
        return productList;
    }

    private List<Product> loadProductWithLock() {
        List<Product> productList = productRepository.findAll();
        LocalDateTime newExpireTime = LocalDateTime.now().plusMinutes(CACHE_TTL_MIN);

        try {
            String json = objectMapper.writeValueAsString(productList);
            redis.opsForValue().set(PRODUCT_CACHE_KEY, json);
            redis.opsForValue().set(PRODUCT_EXPIRE_KEY, newExpireTime.toString());
        } catch (Exception e) {
            // 序列化异常静默处理
        }
        return productList;
    }

    public void evict() {
        redis.delete(PRODUCT_CACHE_KEY);
        redis.delete(PRODUCT_EXPIRE_KEY);
        redis.delete(PRODUCT_LOCK_KEY);
    }
}