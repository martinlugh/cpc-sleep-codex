package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class LightRuleSet implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        boolean moderateEntropy = context.sampleEntropy() > RuleThresholds.ENTROPY_LOW
                && context.sampleEntropy() < RuleThresholds.ENTROPY_HIGH;
        boolean moderateCoupling = context.couplingRatio() > RuleThresholds.COUPLING_LOW
                && context.couplingRatio() < RuleThresholds.COUPLING_HIGH;
        boolean moderateHeartRate = context.heartRate() > RuleThresholds.HEART_RATE_LOW
                && context.heartRate() < RuleThresholds.HEART_RATE_HIGH;
        boolean lowMotion = context.alignedStepCount() <= RuleThresholds.STEP_WAKE_THRESHOLD;

        if (moderateEntropy && moderateCoupling && moderateHeartRate && lowMotion) {
            hits.add(new RuleHit("LIGHT_001", "中间态LIGHT规则", "多项指标位于中间区间", SleepStage.LIGHT, 1.6));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        if (respirationStd > RuleThresholds.RESP_STABLE_STD && respirationStd < RuleThresholds.RESP_UNSTABLE_STD) {
            hits.add(new RuleHit("LIGHT_002", "中等呼吸波动LIGHT规则", "呼吸波动介于稳定与不稳定之间", SleepStage.LIGHT, 0.9));
        }

        double heartStd = RuleMath.stdDev(context.recentHeartRates());
        if (heartStd > RuleThresholds.HEART_VAR_DEEP_MAX && heartStd < RuleThresholds.HEART_VAR_REM_MIN) {
            hits.add(new RuleHit("LIGHT_003", "中等心率变异LIGHT规则", "心率变异介于深睡与REM之间", SleepStage.LIGHT, 0.8));
        }

        return hits;
    }
}
