package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Employee;
import com.supermarket.backend.repository.EmployeeRepository;
import com.supermarket.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Employee emp = employeeRepo.findByUsername(username);
        Map<String, Object> result = new HashMap<>();

        if (emp != null && emp.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(emp.getUsername(), emp.getRole());
            result.put("token", token);
            result.put("role", emp.getRole());
            result.put("name", emp.getName());
            return result;
        } else {
            result.put("error", "用户名或密码错误");
            return result;
        }
    }
}