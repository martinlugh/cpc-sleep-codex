package com.cpc.sleepcodex.util;

import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.Comparator;
import java.util.Map;

public final class ScoreUtil {
    private ScoreUtil() {
    }

    public static SleepStage bestStage(Map<SleepStage, Double> scores) {
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(SleepStage.LIGHT);
    }

    public static double confidence(Map<SleepStage, Double> scores, boolean adjusted) {
        double[] top2 = scores.values().stream()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .mapToDouble(Double::doubleValue)
                .toArray();
        if (top2.length == 0) {
            return 0.0;
        }
        if (top2.length == 1) {
            return 1.0;
        }
        double margin = top2[0] - top2[1];
        double base = Math.max(0.0, Math.min(1.0, margin / (Math.abs(top2[0]) + 1.0)));
        return adjusted ? Math.max(0.0, base - 0.1) : base;
    }

    public static double confidenceForStage(Map<SleepStage, Double> scores, SleepStage stage) {
        double max = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        if (max <= 0) {
            return 0.0;
        }
        double stageScore = scores.getOrDefault(stage, 0.0);
        return Math.max(0.0, Math.min(1.0, stageScore / max));
    }
}
