package com.cpc.sleepcodex.controller;

import com.cpc.sleepcodex.model.StepRequest;
import com.cpc.sleepcodex.service.SleepAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StepController {

    private final SleepAnalysisService sleepAnalysisService;

    public StepController(SleepAnalysisService sleepAnalysisService) {
        this.sleepAnalysisService = sleepAnalysisService;
    }

    @PostMapping("/step")
    public Map<String, String> addStep(
            @RequestBody StepRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        sleepAnalysisService.addStep(request, normalizeUserId(userId));
        return Map.of("status", "OK");
    }

    private String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return "default-user";
        }
        return userId;
    }
}
