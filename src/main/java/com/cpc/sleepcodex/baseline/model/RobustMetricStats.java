package com.cpc.sleepcodex.baseline.model;

public record RobustMetricStats(
        double median,
        double iqr,
        double mad,
        int sampleCount
) {
}
