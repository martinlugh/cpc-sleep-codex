package com.cpc.sleepcodex.decision.engine;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;
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
}
