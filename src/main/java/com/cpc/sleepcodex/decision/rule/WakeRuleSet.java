package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class WakeRuleSet extends BaselineAwareRuleSupport implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        if (context.alignedStepCount() > RuleThresholds.STEP_WAKE_THRESHOLD) {
            hits.add(new RuleHit("WAKE_001", "活动步数唤醒规则", "步数大于0，提示清醒活动", SleepStage.WAKE, 2.4));
        }
        double wakeVlfcHigh = high(BaselineMetricType.VLFC, RuleThresholds.VLFC_HIGH);
        if (context.vlfc() >= wakeVlfcHigh) {
            hits.add(new RuleHit("WAKE_002", "高VLFC清醒规则", "VLFC偏高，偏向清醒(" + source(BaselineMetricType.VLFC) + ")", SleepStage.WAKE, 1.2));
        }
        double wakeHfcLow = low(BaselineMetricType.HFC, RuleThresholds.HFC_LOW);
        if (context.hfc() <= wakeHfcLow) {
            hits.add(new RuleHit("WAKE_003", "低HFC清醒规则", "HFC偏低，不支持深睡(" + source(BaselineMetricType.HFC) + ")", SleepStage.WAKE, 1.0));
        }
        double wakeCouplingLow = low(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_LOW);
        if (context.couplingRatio() <= wakeCouplingLow) {
            hits.add(new RuleHit("WAKE_004", "低耦合比清醒规则", "耦合比偏低，睡眠耦合不足(" + source(BaselineMetricType.COUPLING_RATIO) + ")", SleepStage.WAKE, 1.1));
        }
        double wakeEntropyHigh = high(BaselineMetricType.SAMPLE_ENTROPY, RuleThresholds.ENTROPY_HIGH);
        if (context.sampleEntropy() >= wakeEntropyHigh) {
            hits.add(new RuleHit("WAKE_005", "高熵清醒规则", "样本熵偏高，节律复杂度更高(" + source(BaselineMetricType.SAMPLE_ENTROPY) + ")", SleepStage.WAKE, 1.0));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        double wakeUnstableResp = unstableRespStd(RuleThresholds.RESP_UNSTABLE_STD);
        if (respirationStd >= wakeUnstableResp) {
            hits.add(new RuleHit("WAKE_006", "呼吸不稳定清醒规则", "近3段呼吸波动较大(" + source(BaselineMetricType.RESPIRATION_RATE) + ")", SleepStage.WAKE, 0.9));
        }
        double wakeHeartRateHigh = high(BaselineMetricType.HEART_RATE, RuleThresholds.HEART_RATE_HIGH);
        if (context.heartRate() >= wakeHeartRateHigh) {
            hits.add(new RuleHit("WAKE_007", "高心率清醒规则", "心率较高，清醒概率增加(" + source(BaselineMetricType.HEART_RATE) + ")", SleepStage.WAKE, 1.0));
        }
        double wakeRmssdLow = low(BaselineMetricType.RMSSD, RuleThresholds.RMSSD_LOW);
        if (context.rmssd() <= wakeRmssdLow) {
            hits.add(new RuleHit("WAKE_008", "低RMSSD清醒规则", "RMSSD偏低，偏向清醒(" + source(BaselineMetricType.RMSSD) + ")", SleepStage.WAKE, 0.6));
        }
        return hits;
    }
}
