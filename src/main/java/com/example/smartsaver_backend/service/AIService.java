package com.example.smartsaver_backend.service;

import com.example.smartsaver_backend.entity.Transaction;
import com.example.smartsaver_backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final TransactionRepository repository;
    private final String apiKey = "AIzaSyA8pTZ4263pENuC7QMXUJ36ryAKUT05EfY";
    private final String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey.trim();

    public AIService(TransactionRepository repository) {
        this.repository = repository;
    }

    public String analyzeSpending(String deviceId) {
        // ดึงข้อมูลจริงจาก DB
        List<Transaction> list = repository.findByDeviceIdOrderByDateDesc(deviceId);

        // แปลงข้อมูลเป็นข้อความให้ AI อ่านง่าย
        String dataSummary = list.stream()
                .map(t -> (t.isIncome() ? "รายรับ: " : "รายจ่าย: ") + t.getAmount() + " บาท, โน้ต: " + t.getNote())
                .collect(Collectors.joining("\n"));

        // เขียน Prompt สั่ง AI
        String prompt = "ในฐานะที่ปรึกษาการเงินอัจฉริยะ นี่คือข้อมูลรายรับรายจ่ายล่าสุดของฉัน:\n" + dataSummary +
                "\nช่วยวิเคราะห์สั้นๆ ว่าฉันใช้เงินเป็นยังไง และแนะนำวิธีประหยัดเงิน 1 ข้อ (ตอบเป็นภาษาไทยนะครับ)";

        // ยิงไปหา Google Gemini
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        try {
            Map response = restTemplate.postForObject(geminiUrl, requestBody, Map.class);
            // เอาข้อความที่ AI ตอบกลับมา
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "ขอโทษที AI มึนไปนิดนึง: " + e.getMessage();
        }
    }
}