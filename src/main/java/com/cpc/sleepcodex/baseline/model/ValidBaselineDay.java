package com.cpc.sleepcodex.baseline.model;

import java.util.Map;

public record ValidBaselineDay(
        String nightId,
        Map<BaselineMetricType, RobustMetricStats> statsByMetric,
        int acceptedSegmentCount,
        int totalSegmentCount
) {
}
