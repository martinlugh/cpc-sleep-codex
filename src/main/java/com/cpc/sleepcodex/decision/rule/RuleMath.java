package com.cpc.sleepcodex.decision.rule;

import java.util.List;

public final class RuleMath {
    private RuleMath() {
    }

    public static double stdDev(List<Double> values) {
        if (values == null || values.size() < 2) {
            return 0.0;
        }
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
}
