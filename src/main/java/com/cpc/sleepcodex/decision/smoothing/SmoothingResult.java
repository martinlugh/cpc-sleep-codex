package com.cpc.sleepcodex.decision.smoothing;

import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.Map;

public record SmoothingResult(
        Map<SleepStage, Double> originalScores,
        Map<SleepStage, Double> smoothedScores,
        boolean smoothingApplied
) {
}
