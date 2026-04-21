package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class RemRuleSet extends BaselineAwareRuleSupport implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        if (context.alignedStepCount() <= RuleThresholds.STEP_QUIET_THRESHOLD) {
            hits.add(new RuleHit("REM_001", "低活动REM规则", "步数接近0，满足REM静息条件", SleepStage.REM, 1.1));
        }
        double remEntropyMid = high(BaselineMetricType.SAMPLE_ENTROPY, RuleThresholds.ENTROPY_MID);
        if (context.sampleEntropy() >= remEntropyMid) {
            hits.add(new RuleHit("REM_002", "中高熵REM规则", "样本熵处于中高区间(" + source(BaselineMetricType.SAMPLE_ENTROPY) + ")", SleepStage.REM, 1.2));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        double remStableResp = stableRespStd(RuleThresholds.RESP_STABLE_STD);
        double remUnstableResp = unstableRespStd(RuleThresholds.RESP_UNSTABLE_STD);
        if (respirationStd > remStableResp && respirationStd < remUnstableResp + 0.8) {
            hits.add(new RuleHit("REM_003", "呼吸较深睡不稳REM规则", "呼吸波动高于深睡稳定阈值(" + source(BaselineMetricType.RESPIRATION_RATE) + ")", SleepStage.REM, 1.0));
        }
        double remCouplingLow = low(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_MODERATE_LOW);
        double remCouplingHigh = high(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_MODERATE_HIGH);
        if (context.couplingRatio() >= remCouplingLow
                && context.couplingRatio() <= remCouplingHigh) {
            hits.add(new RuleHit("REM_004", "中等耦合REM规则", "耦合比位于中间区间(" + source(BaselineMetricType.COUPLING_RATIO) + ")", SleepStage.REM, 1.0));
        }

        double heartStd = RuleMath.stdDev(context.recentHeartRates());
        if (heartStd >= RuleThresholds.HEART_VAR_REM_MIN) {
            hits.add(new RuleHit("REM_005", "心率变异偏高REM规则", "心率变异高于深睡水平", SleepStage.REM, 1.1));
        }

        return hits;
    }
}
