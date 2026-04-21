package com.cpc.sleepcodex.baseline.model;

import java.time.Instant;
import java.util.Map;

public record BaselineSegment(
        Instant timestamp,
        String sleepStage,
        double confidence,
        Map<BaselineMetricType, Double> metrics
) {
}
