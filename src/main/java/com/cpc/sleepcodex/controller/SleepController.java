package com.cpc.sleepcodex.controller;

import com.cpc.sleepcodex.model.SleepAnalyzeRequest;
import com.cpc.sleepcodex.model.SleepAnalysisResponse;
import com.cpc.sleepcodex.model.SleepBatchRequest;
import com.cpc.sleepcodex.service.SleepAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sleep")
public class SleepController {

    private final SleepAnalysisService sleepAnalysisService;

    public SleepController(SleepAnalysisService sleepAnalysisService) {
        this.sleepAnalysisService = sleepAnalysisService;
    }

    @PostMapping("/analyze")
    public SleepAnalysisResponse analyze(
            @RequestBody SleepAnalyzeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        return sleepAnalysisService.analyze(request, normalizeUserId(userId));
    }

    @PostMapping("/batch")
    public List<SleepAnalysisResponse> analyzeBatch(
            @RequestBody SleepBatchRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        return sleepAnalysisService.analyzeBatch(request.sleepSegments(), request.stepRecords(), normalizeUserId(userId));
    }

    private String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return "default-user";
        }
        return userId;
    }
}
