package com.cpc.sleepcodex.decision.alignment;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.domain.StepRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

class StepAlignmentServiceTest {

    @Test
    void shouldAlignByTimeOverlap() {
        SleepSegment segment = new SleepSegment(
                "seg-1",
                Instant.parse("2026-04-21T00:00:00Z"),
                Instant.parse("2026-04-21T00:05:00Z"),
                Map.of(SleepStage.LIGHT, 1.0)
        );
        StepRecord step = new StepRecord(
                Instant.parse("2026-04-20T23:58:00Z"),
                Instant.parse("2026-04-21T00:06:00Z"),
                80
        );

        double aligned = new StepAlignmentService().alignStepCount(segment, List.of(step));

        Assertions.assertEquals(50.0, aligned, 0.0001);
    }
}
