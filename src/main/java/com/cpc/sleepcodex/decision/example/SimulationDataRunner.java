package com.cpc.sleepcodex.decision.example;

import com.cpc.sleepcodex.decision.alignment.StepAlignmentService;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.domain.StepRecord;
import com.cpc.sleepcodex.decision.engine.RuleEngine;
import com.cpc.sleepcodex.decision.engine.RuleEngineResult;
import com.cpc.sleepcodex.decision.smoothing.ScoreSmoothingService;
import com.cpc.sleepcodex.decision.smoothing.SmoothingResult;
import com.cpc.sleepcodex.decision.statemachine.SleepStateMachine;
import com.cpc.sleepcodex.decision.statemachine.StateMachineResult;
import com.cpc.sleepcodex.model.SleepAnalysisResponse;
import com.cpc.sleepcodex.util.ExplainUtil;
import com.cpc.sleepcodex.util.ScoreUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SimulationDataRunner {

    public static void main(String[] args) {
        List<StepRecord> steps = List.of(
                new StepRecord(Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:08:00Z"), 40),
                new StepRecord(Instant.parse("2026-04-21T00:08:00Z"), Instant.parse("2026-04-21T00:16:00Z"), 4),
                new StepRecord(Instant.parse("2026-04-21T00:16:00Z"), Instant.parse("2026-04-21T00:24:00Z"), 0)
        );

        List<AlignedSegmentContext> segments = List.of(
                segment("2026-04-21T00:10:00Z", 0.30, 0.35, 0.72, 0.80, 0.86, 17.0, 78.0, 14.0, 0.35),
                segment("2026-04-21T00:15:00Z", 0.55, 0.30, 0.32, 1.10, 0.60, 13.5, 63.0, 28.0, 0.55),
                segment("2026-04-21T00:20:00Z", 0.76, 0.24, 0.20, 1.48, 0.39, 12.1, 54.0, 44.0, 0.78)
        );

        List<SleepAnalysisResponse> outputs = runSimulation(segments, steps);
        outputs.forEach(System.out::println);
    }

    public static List<SleepAnalysisResponse> runSimulation(List<AlignedSegmentContext> segments, List<StepRecord> steps) {
        RuleEngine ruleEngine = new RuleEngine();
        StepAlignmentService alignmentService = new StepAlignmentService();
        ScoreSmoothingService smoothingService = new ScoreSmoothingService();
        SleepStateMachine stateMachine = new SleepStateMachine();

        ArrayDeque<Double> recentResp = new ArrayDeque<>();
        ArrayDeque<Double> recentHeart = new ArrayDeque<>();
        List<SleepSegment> recentScoreSegments = new ArrayList<>();
        SleepStage previousFinal = null;

        List<SleepAnalysisResponse> results = new ArrayList<>();
        for (AlignedSegmentContext base : segments) {
            SleepSegment sleepSegment = new SleepSegment(
                    base.timestamp().toString(),
                    base.timestamp().minus(Duration.ofMinutes(5)),
                    base.timestamp(),
                    Map.of()
            );
            double alignedStep = alignmentService.alignStepCount(sleepSegment, steps);

            AlignedSegmentContext context = new AlignedSegmentContext(
                    base.timestamp(),
                    base.hfc(),
                    base.lfc(),
                    base.vlfc(),
                    base.couplingRatio(),
                    base.sampleEntropy(),
                    base.respirationRate(),
                    base.heartRate(),
                    base.rmssd(),
                    base.coherence(),
                    alignedStep,
                    appendRecent(recentResp, base.respirationRate()),
                    appendRecent(recentHeart, base.heartRate())
            );

            RuleEngineResult ruleResult = ruleEngine.evaluate(context);
            SmoothingResult smoothingResult = smoothingService.smooth(ruleResult.scores(), recentScoreSegments);
            SleepStage smoothedStage = ScoreUtil.bestStage(smoothingResult.smoothedScores());
            StateMachineResult stateResult = stateMachine.apply(smoothedStage, previousFinal);

            previousFinal = stateResult.outputStage();
            push(recentResp, base.respirationRate());
            push(recentHeart, base.heartRate());
            recentScoreSegments.add(new SleepSegment(
                    sleepSegment.segmentId(),
                    sleepSegment.windowStart(),
                    sleepSegment.windowEnd(),
                    new EnumMap<>(smoothingResult.smoothedScores())
            ));
            if (recentScoreSegments.size() > 2) {
                recentScoreSegments.remove(0);
            }

            results.add(new SleepAnalysisResponse(
                    stateResult.outputStage().name(),
                    ScoreUtil.confidence(smoothingResult.smoothedScores(), stateResult.stateMachineAdjusted()),
                    ExplainUtil.ruleExplanation(ruleResult.ruleHits(), stateResult.outputStage()),
                    ExplainUtil.featureSummary(base.heartRate(), base.respirationRate(), base.couplingRatio(), alignedStep),
                    smoothingResult.smoothingApplied(),
                    stateResult.stateMachineAdjusted()
            ));
        }
        return results;
    }

    private static AlignedSegmentContext segment(String ts, double hfc, double lfc, double vlfc,
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

    private static List<Double> appendRecent(ArrayDeque<Double> deque, double current) {
        List<Double> values = new ArrayList<>(deque);
        values.add(current);
        return values;
    }

    private static void push(ArrayDeque<Double> deque, double current) {
        deque.addLast(current);
        while (deque.size() > 2) {
            deque.removeFirst();
        }
    }
}
