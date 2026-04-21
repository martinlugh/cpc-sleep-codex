package com.cpc.sleepcodex.util;

import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.rule.RuleHit;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ExplainUtil {
    private ExplainUtil() {
    }

    public static String ruleExplanation(List<RuleHit> hits, SleepStage stage) {
        return hits.stream()
                .filter(hit -> hit.targetStage() == stage)
                .sorted(Comparator.comparingDouble(RuleHit::scoreContribution).reversed())
                .limit(3)
                .map(hit -> hit.ruleName() + "(" + hit.hitReason() + ")")
                .collect(Collectors.joining("; "));
    }

    public static String featureSummary(double heartRate, double respirationRate, double couplingRatio, double alignedStepCount) {
        return "heartRate=" + heartRate
                + ", respirationRate=" + respirationRate
                + ", couplingRatio=" + couplingRatio
                + ", alignedStepCount=" + String.format("%.2f", alignedStepCount);
    }
}
