package com.cpc.sleepcodex.service;

import com.cpc.sleepcodex.baseline.integration.BaselineThresholdContextFactory;
import com.cpc.sleepcodex.baseline.integration.NoopUserBaselineDataProvider;
import com.cpc.sleepcodex.decision.alignment.StepAlignmentService;
import com.cpc.sleepcodex.decision.engine.RuleEngine;
import com.cpc.sleepcodex.decision.history.InMemoryWindowManager;
import com.cpc.sleepcodex.decision.smoothing.ScoreSmoothingService;
import com.cpc.sleepcodex.decision.statemachine.SleepStateMachine;
import com.cpc.sleepcodex.model.SleepAnalysisResponse;
import com.cpc.sleepcodex.model.SleepAnalyzeRequest;
import com.cpc.sleepcodex.model.StepRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class SleepAnalysisServiceIsolationTest {

    @Test
    void shouldNotLeakHistoryBetweenAnalyzeCalls() {
        SleepAnalysisService serviceA = new SleepAnalysisService(
                new RuleEngine(),
                new StepAlignmentService(),
                new ScoreSmoothingService(),
                new SleepStateMachine(),
                new InMemoryWindowManager(),
                new NoopUserBaselineDataProvider(),
                new BaselineThresholdContextFactory()
        );

        serviceA.addStep(new StepRequest(Instant.parse("2026-04-21T00:08:00Z"), 10));
        serviceA.analyze(request("2026-04-21T00:10:00Z", 0.30, 0.35, 0.72, 0.80, 0.86, 17.0, 78.0, 14.0));
        SleepAnalysisResponse secondInSequence = serviceA.analyze(request("2026-04-21T00:15:00Z", 0.76, 0.24, 0.20, 1.48, 0.39, 12.1, 54.0, 44.0));

        SleepAnalysisService serviceB = new SleepAnalysisService(
                new RuleEngine(),
                new StepAlignmentService(),
                new ScoreSmoothingService(),
                new SleepStateMachine(),
                new InMemoryWindowManager(),
                new NoopUserBaselineDataProvider(),
                new BaselineThresholdContextFactory()
        );
        serviceB.addStep(new StepRequest(Instant.parse("2026-04-21T00:08:00Z"), 10));
        SleepAnalysisResponse singleRun = serviceB.analyze(request("2026-04-21T00:15:00Z", 0.76, 0.24, 0.20, 1.48, 0.39, 12.1, 54.0, 44.0));

        Assertions.assertEquals(singleRun.sleepStage(), secondInSequence.sleepStage());
        Assertions.assertEquals(singleRun.stateMachineAdjusted(), secondInSequence.stateMachineAdjusted());
        Assertions.assertEquals(singleRun.smoothingApplied(), secondInSequence.smoothingApplied());
    }

    @Test
    void shouldUseConfidenceOfFinalAdjustedStage() {
        SleepAnalysisService service = new SleepAnalysisService(
                new RuleEngine(),
                new StepAlignmentService(),
                new ScoreSmoothingService(),
                new SleepStateMachine(),
                new InMemoryWindowManager(),
                new NoopUserBaselineDataProvider(),
                new BaselineThresholdContextFactory()
        );
        service.addStep(new StepRequest(Instant.parse("2026-04-21T00:08:00Z"), 30));
        service.addStep(new StepRequest(Instant.parse("2026-04-21T00:16:00Z"), 0));

        SleepAnalyzeRequest firstWake = request("2026-04-21T00:10:00Z", 0.30, 0.35, 0.72, 0.80, 0.86, 17.0, 78.0, 14.0);
        SleepAnalyzeRequest secondDeepLike = request("2026-04-21T00:15:00Z", 0.76, 0.24, 0.20, 1.48, 0.39, 12.1, 54.0, 44.0);

        SleepAnalysisResponse response = service.analyzeBatch(List.of(firstWake, secondDeepLike), List.of()).get(1);

        Assertions.assertEquals("LIGHT", response.sleepStage());
        Assertions.assertTrue(response.stateMachineAdjusted());
        Assertions.assertTrue(response.confidence() < 0.5);
    }

    private SleepAnalyzeRequest request(String ts, double hfc, double lfc, double vlfc,
                                        double couplingRatio, double sampleEntropy,
                                        double respirationRate, double heartRate, double rmssd) {
        return new SleepAnalyzeRequest(
                Instant.parse(ts),
                hfc,
                lfc,
                vlfc,
                couplingRatio,
                0,
                0,
                sampleEntropy,
                respirationRate,
                heartRate,
                rmssd,
                null
        );
    }
}
