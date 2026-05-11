package com.supermarket.backend.repository;

import com.supermarket.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);
}