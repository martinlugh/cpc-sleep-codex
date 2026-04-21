package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.BaselineRuleReference;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;
import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;

public class BaselineRuleReferenceMapper {

    private static final RobustMetricStats EMPTY = new RobustMetricStats(0.0, 0.0, 0.0, 0);

    public BaselineRuleReference toRuleReference(UserBaselineSnapshot snapshot) {
        if (snapshot.level() == BaselineLevel.GENERIC) {
            return new BaselineRuleReference(BaselineLevel.GENERIC, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
        }

        return new BaselineRuleReference(
                snapshot.level(),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.HEART_RATE, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.RESPIRATION_RATE, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.RMSSD, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.SAMPLE_ENTROPY, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.COUPLING_RATIO, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.HFC, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.LFC, EMPTY),
                snapshot.statsByMetric().getOrDefault(BaselineMetricType.VLFC, EMPTY)
        );
    }
}
