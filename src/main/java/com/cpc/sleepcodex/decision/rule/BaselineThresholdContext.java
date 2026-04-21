package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;

import java.util.Map;

public record BaselineThresholdContext(
        BaselineLevel level,
        Map<BaselineMetricType, RobustMetricStats> statsByMetric
) {
    public static BaselineThresholdContext generic() {
        return new BaselineThresholdContext(BaselineLevel.GENERIC, Map.of());
    }

    public boolean hasBaseline(BaselineMetricType metricType) {
        return level != BaselineLevel.GENERIC && statsByMetric.containsKey(metricType);
    }
}
