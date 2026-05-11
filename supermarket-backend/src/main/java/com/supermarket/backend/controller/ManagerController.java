package com.supermarket.backend.controller;

import com.supermarket.backend.entity.Employee;
import com.supermarket.backend.entity.SalesRecord;
import com.supermarket.backend.repository.EmployeeRepository;
import com.supermarket.backend.repository.SalesRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private SalesRecordRepository salesRepo;

    // 员工管理
    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeRepo.findAll();
    }

    @PostMapping("/employee")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeRepo.save(employee);
    }

    @PutMapping("/employee")
    public Employee updateEmployee(@RequestBody Employee employee) {
        return employeeRepo.save(employee);
    }

    @DeleteMapping("/employee/{id}")
    public void deleteEmployee(@PathVariable Integer id) {
        employeeRepo.deleteById(id);
    }

    // 销售记录
    @GetMapping("/sales")
    public List<SalesRecord> getAllSales() {
        return salesRepo.findAll();
    }
}