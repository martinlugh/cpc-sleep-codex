package com.cpc.sleepcodex.baseline.model;

import java.util.Map;

public record UserBaselineSnapshot(
        String userId,
        BaselineLevel level,
        int validDayCount,
        Map<BaselineMetricType, RobustMetricStats> statsByMetric
) {
}
