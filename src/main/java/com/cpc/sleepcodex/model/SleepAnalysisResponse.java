package com.cpc.sleepcodex.model;

public record SleepAnalysisResponse(
        String sleepStage,
        double confidence,
        String ruleExplanation,
        String featureSummary,
        boolean smoothingApplied,
        boolean stateMachineAdjusted
) {
}
