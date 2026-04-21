package com.cpc.sleepcodex.decision.example;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.StepRecord;
import com.cpc.sleepcodex.model.SleepAnalysisResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class SimulationDataRunnerTest {

    @Test
    void shouldProduceDeterministicOutputs() {
        List<StepRecord> steps = List.of(
                new StepRecord(Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:08:00Z"), 40),
                new StepRecord(Instant.parse("2026-04-21T00:08:00Z"), Instant.parse("2026-04-21T00:16:00Z"), 4),
                new StepRecord(Instant.parse("2026-04-21T00:16:00Z"), Instant.parse("2026-04-21T00:24:00Z"), 0)
        );

        List<AlignedSegmentContext> segments = List.of(
                context("2026-04-21T00:10:00Z", 0.30, 0.35, 0.72, 0.80, 0.86, 17.0, 78.0, 14.0, 0.35),
                context("2026-04-21T00:15:00Z", 0.55, 0.30, 0.32, 1.10, 0.60, 13.5, 63.0, 28.0, 0.55),
                context("2026-04-21T00:20:00Z", 0.76, 0.24, 0.20, 1.48, 0.39, 12.1, 54.0, 44.0, 0.78)
        );

        List<SleepAnalysisResponse> result = SimulationDataRunner.runSimulation(segments, steps);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("WAKE", result.get(0).sleepStage());
        Assertions.assertEquals("WAKE", result.get(1).sleepStage());
        Assertions.assertEquals("LIGHT", result.get(2).sleepStage());
    }

    private AlignedSegmentContext context(String ts, double hfc, double lfc, double vlfc,
                                          double coupling, double entropy, double rr,
                                          double hr, double rmssd, Double coherence) {
        return new AlignedSegmentContext(
                Instant.parse(ts),
                hfc,
                lfc,
                vlfc,
                coupling,
                entropy,
                rr,
                hr,
                rmssd,
                coherence,
                0,
                List.of(),
                List.of()
        );
    }
}
