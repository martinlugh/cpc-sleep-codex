package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;

public abstract class BaselineAwareRuleSupport {

    private final DynamicThresholdResolver resolver = new DynamicThresholdResolver();

    protected double high(BaselineMetricType metricType, double generic) {
        return resolver.high(BaselineThresholdContextHolder.get(), metricType, generic);
    }

    protected double low(BaselineMetricType metricType, double generic) {
        return resolver.low(BaselineThresholdContextHolder.get(), metricType, generic);
    }

    protected double stableRespStd(double generic) {
        return resolver.stableRespStd(BaselineThresholdContextHolder.get(), generic);
    }

    protected double unstableRespStd(double generic) {
        return resolver.unstableRespStd(BaselineThresholdContextHolder.get(), generic);
    }

    protected String source(BaselineMetricType metricType) {
        return resolver.source(BaselineThresholdContextHolder.get(), metricType);
    }
}
