package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class WakeRuleSet implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        if (context.alignedStepCount() > RuleThresholds.STEP_WAKE_THRESHOLD) {
            hits.add(new RuleHit("WAKE_001", "活动步数唤醒规则", "步数大于0，提示清醒活动", SleepStage.WAKE, 2.4));
        }
        if (context.vlfc() >= RuleThresholds.VLFC_HIGH) {
            hits.add(new RuleHit("WAKE_002", "高VLFC清醒规则", "VLFC偏高，偏向清醒", SleepStage.WAKE, 1.2));
        }
        if (context.hfc() <= RuleThresholds.HFC_LOW) {
            hits.add(new RuleHit("WAKE_003", "低HFC清醒规则", "HFC偏低，不支持深睡", SleepStage.WAKE, 1.0));
        }
        if (context.couplingRatio() <= RuleThresholds.COUPLING_LOW) {
            hits.add(new RuleHit("WAKE_004", "低耦合比清醒规则", "耦合比偏低，睡眠耦合不足", SleepStage.WAKE, 1.1));
        }
        if (context.sampleEntropy() >= RuleThresholds.ENTROPY_HIGH) {
            hits.add(new RuleHit("WAKE_005", "高熵清醒规则", "样本熵偏高，节律复杂度更高", SleepStage.WAKE, 1.0));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        if (respirationStd >= RuleThresholds.RESP_UNSTABLE_STD) {
            hits.add(new RuleHit("WAKE_006", "呼吸不稳定清醒规则", "近3段呼吸波动较大", SleepStage.WAKE, 0.9));
        }
        if (context.heartRate() >= RuleThresholds.HEART_RATE_HIGH) {
            hits.add(new RuleHit("WAKE_007", "高心率清醒规则", "心率较高，清醒概率增加", SleepStage.WAKE, 1.0));
        }
        return hits;
    }
}
