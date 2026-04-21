package com.cpc.sleepcodex.baseline.model;

public record BaselineRuleReference(
        BaselineLevel level,
        RobustMetricStats heartRate,
        RobustMetricStats respirationRate,
        RobustMetricStats rmssd,
        RobustMetricStats sampleEntropy,
        RobustMetricStats couplingRatio,
        RobustMetricStats hfc,
        RobustMetricStats lfc,
        RobustMetricStats vlfc
) {
}
