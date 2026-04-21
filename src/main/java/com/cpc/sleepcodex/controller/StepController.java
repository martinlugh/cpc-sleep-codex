package com.cpc.sleepcodex.controller;

import com.cpc.sleepcodex.model.StepRequest;
import com.cpc.sleepcodex.service.SleepAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StepController {

    private final SleepAnalysisService sleepAnalysisService;

    public StepController(SleepAnalysisService sleepAnalysisService) {
        this.sleepAnalysisService = sleepAnalysisService;
    }

    @PostMapping("/step")
    public Map<String, String> addStep(@RequestBody StepRequest request) {
        sleepAnalysisService.addStep(request);
        return Map.of("status", "OK");
    }
}
