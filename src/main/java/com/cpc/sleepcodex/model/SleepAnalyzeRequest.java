package com.cpc.sleepcodex.model;

import java.time.Instant;

public record SleepAnalyzeRequest(
        Instant timestamp,
        double hfc,
        double lfc,
        double vlfc,
        double couplingRatio,
        double sd1,
        double sd2,
        double sampleEntropy,
        double respirationRate,
        double heartRate,
        double rmssd,
        Double crossSpectralPower
) {
}
