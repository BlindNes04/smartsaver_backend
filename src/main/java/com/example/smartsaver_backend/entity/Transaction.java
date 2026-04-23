package com.example.smartsaver_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private double amount;
    private String note;
    private String emoji;

    @JsonProperty("isIncome")
    private boolean isIncome;
    private LocalDateTime date;

    public Transaction() {}
}
