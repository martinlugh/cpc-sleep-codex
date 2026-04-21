package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class LightRuleSet extends BaselineAwareRuleSupport implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        double lightEntropyLow = low(BaselineMetricType.SAMPLE_ENTROPY, RuleThresholds.ENTROPY_LOW);
        double lightEntropyHigh = high(BaselineMetricType.SAMPLE_ENTROPY, RuleThresholds.ENTROPY_HIGH);
        double lightCouplingLow = low(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_LOW);
        double lightCouplingHigh = high(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_HIGH);
        double lightHeartRateLow = low(BaselineMetricType.HEART_RATE, RuleThresholds.HEART_RATE_LOW);
        double lightHeartRateHigh = high(BaselineMetricType.HEART_RATE, RuleThresholds.HEART_RATE_HIGH);

        boolean moderateEntropy = context.sampleEntropy() > lightEntropyLow
                && context.sampleEntropy() < lightEntropyHigh;
        boolean moderateCoupling = context.couplingRatio() > lightCouplingLow
                && context.couplingRatio() < lightCouplingHigh;
        boolean moderateHeartRate = context.heartRate() > lightHeartRateLow
                && context.heartRate() < lightHeartRateHigh;
        boolean lowMotion = context.alignedStepCount() <= RuleThresholds.STEP_WAKE_THRESHOLD;

        if (moderateEntropy && moderateCoupling && moderateHeartRate && lowMotion) {
            hits.add(new RuleHit("LIGHT_001", "中间态LIGHT规则", "多项指标位于中间区间(" + source(BaselineMetricType.HEART_RATE) + ")", SleepStage.LIGHT, 1.6));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        double lightStableResp = stableRespStd(RuleThresholds.RESP_STABLE_STD);
        double lightUnstableResp = unstableRespStd(RuleThresholds.RESP_UNSTABLE_STD);
        if (respirationStd > lightStableResp && respirationStd < lightUnstableResp) {
            hits.add(new RuleHit("LIGHT_002", "中等呼吸波动LIGHT规则", "呼吸波动介于稳定与不稳定之间(" + source(BaselineMetricType.RESPIRATION_RATE) + ")", SleepStage.LIGHT, 0.9));
        }

        double heartStd = RuleMath.stdDev(context.recentHeartRates());
        if (heartStd > RuleThresholds.HEART_VAR_DEEP_MAX && heartStd < RuleThresholds.HEART_VAR_REM_MIN) {
            hits.add(new RuleHit("LIGHT_003", "中等心率变异LIGHT规则", "心率变异介于深睡与REM之间", SleepStage.LIGHT, 0.8));
        }

        return hits;
    }
}
