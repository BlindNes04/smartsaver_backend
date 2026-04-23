package com.example.smartsaver_backend.repository;

import com.example.smartsaver_backend.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDeviceIdOrderByDateDesc(String deviceId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Transaction t Where t.deviceId = :deviceId")
    void deleteByDeviceId(@Param("deviceId") String deviceId);
}