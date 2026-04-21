package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class DeepRuleSet implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        if (context.hfc() >= RuleThresholds.HFC_HIGH) {
            hits.add(new RuleHit("DEEP_001", "高HFC深睡规则", "HFC偏高，支持深睡", SleepStage.DEEP, 1.4));
        }
        if (context.lfc() <= RuleThresholds.LFC_LOW && context.vlfc() <= RuleThresholds.VLFC_LOW) {
            hits.add(new RuleHit("DEEP_002", "低LFC/VLFC深睡规则", "LFC与VLFC均偏低", SleepStage.DEEP, 1.2));
        }
        if (context.couplingRatio() >= RuleThresholds.COUPLING_HIGH) {
            hits.add(new RuleHit("DEEP_003", "高耦合比深睡规则", "耦合比偏高，睡眠耦合稳定", SleepStage.DEEP, 1.6));
        }
        if (context.sampleEntropy() <= RuleThresholds.ENTROPY_LOW) {
            hits.add(new RuleHit("DEEP_004", "低熵深睡规则", "样本熵偏低，节律更规律", SleepStage.DEEP, 1.1));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        if (respirationStd <= RuleThresholds.RESP_STABLE_STD) {
            hits.add(new RuleHit("DEEP_005", "呼吸稳定深睡规则", "近3段呼吸波动较小", SleepStage.DEEP, 1.0));
        }
        if (context.heartRate() <= RuleThresholds.HEART_RATE_LOW) {
            hits.add(new RuleHit("DEEP_006", "低心率深睡规则", "心率偏低，符合深睡特征", SleepStage.DEEP, 1.0));
        }
        if (context.alignedStepCount() <= RuleThresholds.STEP_QUIET_THRESHOLD) {
            hits.add(new RuleHit("DEEP_007", "静息步数深睡规则", "步数接近0，身体活动低", SleepStage.DEEP, 1.3));
        }
        if (context.coherence() != null && context.coherence() >= RuleThresholds.COHERENCE_HIGH) {
            hits.add(new RuleHit("DEEP_008", "高相干深睡规则", "相干性较高，支持深睡", SleepStage.DEEP, 0.8));
        }

        return hits;
    }
}
