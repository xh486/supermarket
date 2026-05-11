package com.supermarket.backend.repository;

import com.supermarket.backend.entity.PurchaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseRecordRepository extends JpaRepository<PurchaseRecord, Integer> {
    List<PurchaseRecord> findByStatus(Integer status);
}