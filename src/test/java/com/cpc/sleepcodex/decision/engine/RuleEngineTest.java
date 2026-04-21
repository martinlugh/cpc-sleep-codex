package com.cpc.sleepcodex.decision.engine;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.util.ScoreUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class RuleEngineTest {

    @Test
    void shouldGenerateScoresAndRuleHits() {
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

        RuleEngineResult result = ruleEngine.evaluate(context);

        Assertions.assertFalse(result.ruleHits().isEmpty());
        Assertions.assertTrue(result.scores().containsKey(SleepStage.WAKE));
        Assertions.assertTrue(result.scores().containsKey(SleepStage.LIGHT));
        Assertions.assertTrue(result.scores().containsKey(SleepStage.DEEP));
        Assertions.assertTrue(result.scores().containsKey(SleepStage.REM));
        Assertions.assertTrue(result.scores().get(SleepStage.DEEP) > result.scores().get(SleepStage.WAKE));
    }

    @Test
    void shouldReduceDeepAndLightScoreWhenHeartRateIsHigh() {
        RuleEngine ruleEngine = new RuleEngine();
        AlignedSegmentContext context = new AlignedSegmentContext(
                Instant.parse("2026-04-21T00:10:00Z"),
                0.70,
                0.30,
                0.25,
                1.10,
                0.60,
                14.0,
                78.0,
                20.0,
                0.0,
                List.of(14.2, 14.0, 13.8),
                List.of(75.0, 78.0, 81.0)
        );

        RuleEngineResult result = ruleEngine.evaluate(context);

        Assertions.assertTrue(result.ruleHits().stream().anyMatch(h -> "DEEP_009".equals(h.ruleCode())));
        Assertions.assertTrue(result.ruleHits().stream().anyMatch(h -> "LIGHT_004".equals(h.ruleCode())));
        Assertions.assertTrue(result.scores().get(SleepStage.DEEP) < 0.0);
    }

    @Test
    void shouldPreferWakeForHighHeartAndHighRespEvenWhenStepIsZero() {
        RuleEngine ruleEngine = new RuleEngine();
        AlignedSegmentContext context = new AlignedSegmentContext(
                Instant.parse("2026-04-21T12:11:04.370569800Z"),
                29201.14711820579,
                17145.713317423084,
                372014.5983261486,
                1.7031164920115776,
                0.2411620568168881,
                18.080906225357204,
                95.61947018194901,
                294.85376364476656,
                0.0,
                List.of(18.080906225357204),
                List.of(95.61947018194901)
        );

        RuleEngineResult result = ruleEngine.evaluate(context);

        Assertions.assertTrue(result.ruleHits().stream().anyMatch(h -> "WAKE_009".equals(h.ruleCode())));
        Assertions.assertTrue(result.ruleHits().stream().anyMatch(h -> "DEEP_010".equals(h.ruleCode())));
        Assertions.assertEquals(SleepStage.WAKE, ScoreUtil.bestStage(result.scores()));
        Assertions.assertTrue(result.scores().get(SleepStage.WAKE) > result.scores().get(SleepStage.DEEP));
    }
}
