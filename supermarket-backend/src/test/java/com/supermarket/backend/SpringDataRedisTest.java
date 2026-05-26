package com.supermarket.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() {
        System.out.println(redisTemplate);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        SetOperations setOperations = redisTemplate.opsForSet();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

    }

    @Test
    public void test1() {
       redisTemplate.opsForValue().set("city","武汉");
       System.out.println(redisTemplate.opsForValue().get("city"));
       redisTemplate.opsForValue().set("code","1234", 10,TimeUnit.MINUTES);
       redisTemplate.opsForValue().setIfAbsent("abc","1234",10 ,TimeUnit.MINUTES);
    }
}