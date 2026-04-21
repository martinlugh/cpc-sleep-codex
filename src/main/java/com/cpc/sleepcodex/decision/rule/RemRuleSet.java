package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class RemRuleSet implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        if (context.alignedStepCount() <= RuleThresholds.STEP_QUIET_THRESHOLD) {
            hits.add(new RuleHit("REM_001", "低活动REM规则", "步数接近0，满足REM静息条件", SleepStage.REM, 1.1));
        }
        if (context.sampleEntropy() >= RuleThresholds.ENTROPY_MID) {
            hits.add(new RuleHit("REM_002", "中高熵REM规则", "样本熵处于中高区间", SleepStage.REM, 1.2));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        if (respirationStd > RuleThresholds.RESP_STABLE_STD && respirationStd < RuleThresholds.RESP_UNSTABLE_STD + 0.8) {
            hits.add(new RuleHit("REM_003", "呼吸较深睡不稳REM规则", "呼吸波动高于深睡稳定阈值", SleepStage.REM, 1.0));
        }
        if (context.couplingRatio() >= RuleThresholds.COUPLING_MODERATE_LOW
                && context.couplingRatio() <= RuleThresholds.COUPLING_MODERATE_HIGH) {
            hits.add(new RuleHit("REM_004", "中等耦合REM规则", "耦合比位于中间区间", SleepStage.REM, 1.0));
        }

        double heartStd = RuleMath.stdDev(context.recentHeartRates());
        if (heartStd >= RuleThresholds.HEART_VAR_REM_MIN) {
            hits.add(new RuleHit("REM_005", "心率变异偏高REM规则", "心率变异高于深睡水平", SleepStage.REM, 1.1));
        }

        return hits;
    }
}
