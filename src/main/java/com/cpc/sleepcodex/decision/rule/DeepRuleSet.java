package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.ArrayList;
import java.util.List;

public class DeepRuleSet extends BaselineAwareRuleSupport implements SleepStageRule {

    @Override
    public List<RuleHit> evaluate(AlignedSegmentContext context) {
        List<RuleHit> hits = new ArrayList<>();

        double deepHfcHigh = high(BaselineMetricType.HFC, RuleThresholds.HFC_HIGH);
        if (context.hfc() >= deepHfcHigh) {
            hits.add(new RuleHit("DEEP_001", "高HFC深睡规则", "HFC偏高，支持深睡(" + source(BaselineMetricType.HFC) + ")", SleepStage.DEEP, 1.4));
        }
        double deepLfcLow = low(BaselineMetricType.LFC, RuleThresholds.LFC_LOW);
        double deepVlfcLow = low(BaselineMetricType.VLFC, RuleThresholds.VLFC_LOW);
        if (context.lfc() <= deepLfcLow && context.vlfc() <= deepVlfcLow) {
            hits.add(new RuleHit("DEEP_002", "低LFC/VLFC深睡规则", "LFC与VLFC均偏低(" + source(BaselineMetricType.LFC) + "/" + source(BaselineMetricType.VLFC) + ")", SleepStage.DEEP, 1.2));
        }
        double deepCouplingHigh = high(BaselineMetricType.COUPLING_RATIO, RuleThresholds.COUPLING_HIGH);
        if (context.couplingRatio() >= deepCouplingHigh) {
            hits.add(new RuleHit("DEEP_003", "高耦合比深睡规则", "耦合比偏高，睡眠耦合稳定(" + source(BaselineMetricType.COUPLING_RATIO) + ")", SleepStage.DEEP, 1.6));
        }
        double deepEntropyLow = low(BaselineMetricType.SAMPLE_ENTROPY, RuleThresholds.ENTROPY_LOW);
        if (context.sampleEntropy() <= deepEntropyLow) {
            hits.add(new RuleHit("DEEP_004", "低熵深睡规则", "样本熵偏低，节律更规律(" + source(BaselineMetricType.SAMPLE_ENTROPY) + ")", SleepStage.DEEP, 1.1));
        }

        double respirationStd = RuleMath.stdDev(context.recentRespirationRates());
        double deepStableResp = stableRespStd(RuleThresholds.RESP_STABLE_STD);
        if (context.recentRespirationRates() != null
                && context.recentRespirationRates().size() >= 2
                && respirationStd <= deepStableResp) {
            hits.add(new RuleHit("DEEP_005", "呼吸稳定深睡规则", "近3段呼吸波动较小(" + source(BaselineMetricType.RESPIRATION_RATE) + ")", SleepStage.DEEP, 1.0));
        }
        double deepHeartRateLow = low(BaselineMetricType.HEART_RATE, RuleThresholds.HEART_RATE_LOW);
        if (context.heartRate() <= deepHeartRateLow) {
            hits.add(new RuleHit("DEEP_006", "低心率深睡规则", "心率偏低，符合深睡特征(" + source(BaselineMetricType.HEART_RATE) + ")", SleepStage.DEEP, 1.0));
        }
        double deepHeartRateHigh = high(BaselineMetricType.HEART_RATE, RuleThresholds.HEART_RATE_HIGH);
        if (context.heartRate() >= deepHeartRateHigh) {
            hits.add(new RuleHit("DEEP_009", "高心率抑制深睡规则", "心率偏高，不支持深睡(" + source(BaselineMetricType.HEART_RATE) + ")", SleepStage.DEEP, -1.8));
        }
        if (context.respirationRate() >= RuleThresholds.RESP_WAKE_HIGH) {
            hits.add(new RuleHit("DEEP_010", "高呼吸率抑制深睡规则", "呼吸率偏高，不符合深睡呼吸模式", SleepStage.DEEP, -1.3));
        }
        if (context.alignedStepCount() <= RuleThresholds.STEP_QUIET_THRESHOLD) {
            hits.add(new RuleHit("DEEP_007", "静息步数深睡规则", "步数接近0，身体活动低", SleepStage.DEEP, 1.3));
        }
        double deepRmssdHigh = high(BaselineMetricType.RMSSD, RuleThresholds.RMSSD_HIGH);
        if (context.rmssd() >= deepRmssdHigh) {
            hits.add(new RuleHit("DEEP_008", "高RMSSD深睡规则", "RMSSD偏高，支持深睡(" + source(BaselineMetricType.RMSSD) + ")", SleepStage.DEEP, 0.6));
        }
        return hits;
    }
}
