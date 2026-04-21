package com.cpc.sleepcodex.decision.smoothing;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

class ScoreSmoothingServiceTest {

    @Test
    void shouldSmoothScoresUsingThreeSegments() {
        Map<SleepStage, Double> current = Map.of(
                SleepStage.WAKE, 0.0,
                SleepStage.LIGHT, 1.0,
                SleepStage.DEEP, 3.0,
                SleepStage.REM, 0.5
        );

        SleepSegment prev1 = new SleepSegment(
                "seg-a",
                Instant.parse("2026-04-21T00:00:00Z"),
                Instant.parse("2026-04-21T00:05:00Z"),
                Map.of(SleepStage.WAKE, 0.0, SleepStage.LIGHT, 2.0, SleepStage.DEEP, 2.0, SleepStage.REM, 0.2)
        );

        SleepSegment prev2 = new SleepSegment(
                "seg-b",
                Instant.parse("2026-04-21T00:05:00Z"),
                Instant.parse("2026-04-21T00:10:00Z"),
                Map.of(SleepStage.WAKE, 0.0, SleepStage.LIGHT, 1.5, SleepStage.DEEP, 1.0, SleepStage.REM, 0.1)
        );

        SmoothingResult result = new ScoreSmoothingService().smooth(current, List.of(prev2, prev1));

        Assertions.assertTrue(result.smoothingApplied());
        Assertions.assertEquals(2.3, result.smoothedScores().get(SleepStage.DEEP), 0.0001);
    }
}
