package com.cpc.sleepcodex.baseline.integration;

import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;
import com.cpc.sleepcodex.decision.rule.BaselineThresholdContext;

public class BaselineThresholdContextFactory {

    public BaselineThresholdContext fromSnapshot(UserBaselineSnapshot snapshot) {
        return new BaselineThresholdContext(snapshot.level(), snapshot.statsByMetric());
    }

    // 说明：这是基于CPC启发特征与个体基线修正的工程实现，不代表对任何厂商专有算法的精确复现。
}
