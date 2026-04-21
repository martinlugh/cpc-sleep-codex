package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;

public class DynamicThresholdResolver {

    public double high(BaselineThresholdContext context, BaselineMetricType metricType, double generic) {
        if (!context.hasBaseline(metricType)) {
            return generic;
        }
        RobustMetricStats stats = context.statsByMetric().get(metricType);
        return stats.median() + spread(stats) * levelScale(context.level());
    }

    public double low(BaselineThresholdContext context, BaselineMetricType metricType, double generic) {
        if (!context.hasBaseline(metricType)) {
            return generic;
        }
        RobustMetricStats stats = context.statsByMetric().get(metricType);
        return stats.median() - spread(stats) * levelScale(context.level());
    }

    public double stableRespStd(BaselineThresholdContext context, double generic) {
        if (!context.hasBaseline(BaselineMetricType.RESPIRATION_RATE)) {
            return generic;
        }
        RobustMetricStats stats = context.statsByMetric().get(BaselineMetricType.RESPIRATION_RATE);
        return Math.max(0.4, spread(stats) * 0.8);
    }

    public double unstableRespStd(BaselineThresholdContext context, double generic) {
        if (!context.hasBaseline(BaselineMetricType.RESPIRATION_RATE)) {
            return generic;
        }
        RobustMetricStats stats = context.statsByMetric().get(BaselineMetricType.RESPIRATION_RATE);
        return Math.max(0.8, spread(stats) * 1.4);
    }

    public String source(BaselineThresholdContext context, BaselineMetricType metricType) {
        if (!context.hasBaseline(metricType)) {
            return "通用阈值";
        }
        return "个体基线(" + context.level() + ")";
    }

    private double spread(RobustMetricStats stats) {
        return Math.max(0.05, Math.max(stats.mad(), stats.iqr() / 1.349));
    }

    private double levelScale(BaselineLevel level) {
        return switch (level) {
            case DAY_3 -> 1.2;
            case DAY_7 -> 1.0;
            case DAY_21 -> 0.8;
            case GENERIC -> 1.0;
        };
    }
}
