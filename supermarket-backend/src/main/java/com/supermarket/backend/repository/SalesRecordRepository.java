package com.supermarket.backend.repository;

import com.supermarket.backend.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, Integer> {
}