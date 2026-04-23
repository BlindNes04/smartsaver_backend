package com.example.smartsaver_backend.controller;

import com.example.smartsaver_backend.entity.Transaction;
import com.example.smartsaver_backend.repository.TransactionRepository;
import com.example.smartsaver_backend.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionRepository repository;
    private final AIService aiService;

    // ใช้ Constructor Injection
    public TransactionController(TransactionRepository repository, AIService aiService) {
        this.repository = repository;
        this.aiService = aiService;
    }

    // --- ส่วนของข้อมูลรายการ (CRUD เดิม) ---

    @GetMapping("/{deviceId}")
    public List<Transaction> getTransactions(@PathVariable String deviceId) {
        return repository.findByDeviceIdOrderByDateDesc(deviceId);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        return repository.save(transaction);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction details) {
        return repository.findById(id).map(t -> {
            t.setAmount(details.getAmount());
            t.setNote(details.getNote());
            t.setEmoji(details.getEmoji());
            t.setDate(details.getDate());
            t.setIncome(details.isIncome());
            return repository.save(t);
        }).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @DeleteMapping("/all/{deviceId}")
    public void deleteAllByDevice(@PathVariable String deviceId) {
        repository.deleteByDeviceId(deviceId);
    }

    // --- ส่วนของ AI Intelligence ---

    @GetMapping("/ai-analysis/{deviceId}")
    public Map<String, String> getAIAnalysis(@PathVariable String deviceId) {
        // เรียกใช้ Service เพื่อส่งข้อมูลใน DB ไปถาม Gemini
        String analysis = aiService.analyzeSpending(deviceId);

        // ส่งกลับเป็น Map เพื่อให้ Flutter รับเป็น JSON
        return Map.of("analysis", analysis);
    }
}