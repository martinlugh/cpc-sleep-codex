package com.cpc.sleepcodex.decision.domain;

import java.time.Instant;
import java.util.List;

public record AlignedSegmentContext(
        Instant timestamp,
        double hfc,
        double lfc,
        double vlfc,
        double couplingRatio,
        double sampleEntropy,
        double respirationRate,
        double heartRate,
        double rmssd,
        double alignedStepCount,
        List<Double> recentRespirationRates,
        List<Double> recentHeartRates
) {
}
