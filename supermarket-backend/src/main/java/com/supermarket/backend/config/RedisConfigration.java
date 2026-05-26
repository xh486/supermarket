package com.supermarket.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfigration {

    /**
     * 全局 ObjectMapper Bean，解决自动注入失败
     * 同时适配 LocalDateTime 时间序列化
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 支持 Java8+ 时间类型（LocalDateTime）
        mapper.registerModule(new JavaTimeModule());
        // 禁用时间戳格式，输出标准时间字符串
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}