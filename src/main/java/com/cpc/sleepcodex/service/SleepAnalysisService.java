package com.cpc.sleepcodex.service;

import com.cpc.sleepcodex.decision.alignment.StepAlignmentService;
import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.domain.StepRecord;
import com.cpc.sleepcodex.decision.engine.RuleEngine;
import com.cpc.sleepcodex.decision.engine.RuleEngineResult;
import com.cpc.sleepcodex.decision.history.InMemoryWindowManager;
import com.cpc.sleepcodex.decision.smoothing.ScoreSmoothingService;
import com.cpc.sleepcodex.decision.smoothing.SmoothingResult;
import com.cpc.sleepcodex.decision.statemachine.SleepStateMachine;
import com.cpc.sleepcodex.decision.statemachine.StateMachineResult;
import com.cpc.sleepcodex.model.SleepAnalyzeRequest;
import com.cpc.sleepcodex.model.SleepAnalysisResponse;
import com.cpc.sleepcodex.model.StepRequest;
import com.cpc.sleepcodex.util.ExplainUtil;
import com.cpc.sleepcodex.util.ScoreUtil;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class SleepAnalysisService {

    private final RuleEngine ruleEngine;
    private final StepAlignmentService stepAlignmentService;
    private final ScoreSmoothingService scoreSmoothingService;
    private final SleepStateMachine sleepStateMachine;
    private final InMemoryWindowManager windowManager;

    private final ArrayDeque<Double> recentRespRates = new ArrayDeque<>();
    private final ArrayDeque<Double> recentHeartRates = new ArrayDeque<>();
    private SleepStage previousFinalStage;

    public SleepAnalysisService(
            RuleEngine ruleEngine,
            StepAlignmentService stepAlignmentService,
            ScoreSmoothingService scoreSmoothingService,
            SleepStateMachine sleepStateMachine,
            InMemoryWindowManager windowManager
    ) {
        this.ruleEngine = ruleEngine;
        this.stepAlignmentService = stepAlignmentService;
        this.scoreSmoothingService = scoreSmoothingService;
        this.sleepStateMachine = sleepStateMachine;
        this.windowManager = windowManager;
    }

    public synchronized void addStep(StepRequest request) {
        windowManager.addStepRecord(new StepRecord(
                request.timestamp().minus(Duration.ofMinutes(8)),
                request.timestamp(),
                request.stepCount()
        ));
    }

    public synchronized SleepAnalysisResponse analyze(SleepAnalyzeRequest request) {
        SleepSegment segment = new SleepSegment(
                request.timestamp().toString(),
                request.timestamp().minus(Duration.ofMinutes(5)),
                request.timestamp(),
                Map.of()
        );

        double alignedStepCount = stepAlignmentService.alignStepCount(segment, windowManager.getStepRecordsSnapshot());

        AlignedSegmentContext context = new AlignedSegmentContext(
                request.timestamp(),
                request.hfc(),
                request.lfc(),
                request.vlfc(),
                request.couplingRatio(),
                request.sampleEntropy(),
                request.respirationRate(),
                request.heartRate(),
                request.rmssd(),
                request.coherence(),
                alignedStepCount,
                recentValues(recentRespRates, request.respirationRate()),
                recentValues(recentHeartRates, request.heartRate())
        );

        RuleEngineResult ruleResult = ruleEngine.evaluate(context);
        SmoothingResult smoothingResult = scoreSmoothingService.smooth(
                ruleResult.scores(),
                windowManager.getRecentSleepSegments(2)
        );

        SleepStage smoothedStage = ScoreUtil.bestStage(smoothingResult.smoothedScores());
        StateMachineResult machineResult = sleepStateMachine.apply(smoothedStage, previousFinalStage);

        previousFinalStage = machineResult.outputStage();
        pushRecent(recentRespRates, request.respirationRate());
        pushRecent(recentHeartRates, request.heartRate());

        windowManager.addSleepSegment(new SleepSegment(
                segment.segmentId(),
                segment.windowStart(),
                segment.windowEnd(),
                new EnumMap<>(smoothingResult.smoothedScores())
        ));

        double confidence = ScoreUtil.confidence(smoothingResult.smoothedScores(), machineResult.stateMachineAdjusted());
        String explanation = ExplainUtil.ruleExplanation(ruleResult.ruleHits(), machineResult.outputStage());
        String featureSummary = ExplainUtil.featureSummary(request.heartRate(), request.respirationRate(), request.couplingRatio(), alignedStepCount);

        return new SleepAnalysisResponse(
                machineResult.outputStage().name(),
                confidence,
                explanation,
                featureSummary,
                smoothingResult.smoothingApplied(),
                machineResult.stateMachineAdjusted()
        );
    }

    public synchronized List<SleepAnalysisResponse> analyzeBatch(List<SleepAnalyzeRequest> segments, List<StepRequest> stepRecords) {
        for (StepRequest stepRecord : stepRecords) {
            addStep(stepRecord);
        }
        List<SleepAnalysisResponse> results = new ArrayList<>();
        for (SleepAnalyzeRequest segment : segments) {
            results.add(analyze(segment));
        }
        return results;
    }

    private List<Double> recentValues(ArrayDeque<Double> values, double current) {
        List<Double> result = new ArrayList<>(values);
        result.add(current);
        return result;
    }

    private void pushRecent(ArrayDeque<Double> values, double current) {
        values.addLast(current);
        while (values.size() > 2) {
            values.removeFirst();
        }
    }
}
