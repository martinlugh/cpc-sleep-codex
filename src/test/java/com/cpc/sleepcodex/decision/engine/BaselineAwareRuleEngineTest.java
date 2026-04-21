package com.cpc.sleepcodex.decision.engine;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.rule.BaselineThresholdContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class BaselineAwareRuleEngineTest {

    @Test
    void shouldUseBaselineThresholdAndExplainIt() {
        RuleEngine ruleEngine = new RuleEngine();
        AlignedSegmentContext context = new AlignedSegmentContext(
                Instant.parse("2026-04-21T00:10:00Z"),
                0.68,
                0.22,
                0.18,
                1.42,
                0.41,
                12.0,
                55.0,
                38.0,
                0.0,
                List.of(12.1, 11.9, 12.0),
                List.of(54.0, 55.0, 56.0)
        );

        Map<BaselineMetricType, RobustMetricStats> stats = new EnumMap<>(BaselineMetricType.class);
        stats.put(BaselineMetricType.HFC, new RobustMetricStats(0.60, 0.08, 0.06, 7));
        stats.put(BaselineMetricType.COUPLING_RATIO, new RobustMetricStats(1.30, 0.15, 0.10, 7));
        stats.put(BaselineMetricType.SAMPLE_ENTROPY, new RobustMetricStats(0.50, 0.10, 0.07, 7));
        stats.put(BaselineMetricType.HEART_RATE, new RobustMetricStats(60.0, 6.0, 3.0, 7));
        stats.put(BaselineMetricType.RESPIRATION_RATE, new RobustMetricStats(13.0, 1.6, 0.8, 7));
        stats.put(BaselineMetricType.RMSSD, new RobustMetricStats(34.0, 8.0, 4.0, 7));
        stats.put(BaselineMetricType.LFC, new RobustMetricStats(0.30, 0.07, 0.05, 7));
        stats.put(BaselineMetricType.VLFC, new RobustMetricStats(0.28, 0.09, 0.06, 7));

        RuleEngineResult result = ruleEngine.evaluate(context, new BaselineThresholdContext(BaselineLevel.DAY_7, stats));

        Assertions.assertTrue(result.scores().containsKey(SleepStage.DEEP));
        Assertions.assertTrue(result.ruleHits().stream().anyMatch(h -> h.hitReason().contains("个体基线(DAY_7)")));
    }
}
