package com.cpc.sleepcodex.service;

import com.cpc.sleepcodex.baseline.integration.BaselineThresholdContextFactory;
import com.cpc.sleepcodex.baseline.integration.UserBaselineDataProvider;
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
    private final UserBaselineDataProvider userBaselineDataProvider;
    private final BaselineThresholdContextFactory baselineThresholdContextFactory;

    public SleepAnalysisService(
            RuleEngine ruleEngine,
            StepAlignmentService stepAlignmentService,
            ScoreSmoothingService scoreSmoothingService,
            SleepStateMachine sleepStateMachine,
            InMemoryWindowManager windowManager,
            UserBaselineDataProvider userBaselineDataProvider,
            BaselineThresholdContextFactory baselineThresholdContextFactory
    ) {
        this.ruleEngine = ruleEngine;
        this.stepAlignmentService = stepAlignmentService;
        this.scoreSmoothingService = scoreSmoothingService;
        this.sleepStateMachine = sleepStateMachine;
        this.windowManager = windowManager;
        this.userBaselineDataProvider = userBaselineDataProvider;
        this.baselineThresholdContextFactory = baselineThresholdContextFactory;
    }

    public synchronized void addStep(StepRequest request) {
        windowManager.addStepRecord(new StepRecord(
                request.timestamp().minus(Duration.ofMinutes(8)),
                request.timestamp(),
                request.stepCount()
        ));
    }

    public synchronized SleepAnalysisResponse analyze(SleepAnalyzeRequest request) {
        return analyze(request, "default-user");
    }

    public synchronized SleepAnalysisResponse analyze(SleepAnalyzeRequest request, String userId) {
        StreamState streamState = new StreamState();
        List<SleepSegment> localSleepHistory = new ArrayList<>();
        return analyzeOne(request, streamState, localSleepHistory, userId, windowManager.getStepRecordsSnapshot());
    }

    private SleepAnalysisResponse analyzeOne(
            SleepAnalyzeRequest request,
            StreamState streamState,
            List<SleepSegment> localSleepHistory,
            String userId,
            List<StepRecord> stepRecords
    ) {
        SleepSegment segment = new SleepSegment(
                request.timestamp().toString(),
                request.timestamp().minus(Duration.ofMinutes(5)),
                request.timestamp(),
                Map.of()
        );

        double alignedStepCount = stepAlignmentService.alignStepCount(segment, stepRecords);

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
                alignedStepCount,
                recentValues(streamState.recentRespRates, request.respirationRate()),
                recentValues(streamState.recentHeartRates, request.heartRate())
        );

        RuleEngineResult ruleResult = ruleEngine.evaluate(
                context,
                baselineThresholdContextFactory.fromSnapshot(userBaselineDataProvider.load(userId))
        );
        SmoothingResult smoothingResult = scoreSmoothingService.smooth(
                ruleResult.scores(),
                localSleepHistory
        );

        SleepStage smoothedStage = ScoreUtil.bestStage(smoothingResult.smoothedScores());
        StateMachineResult machineResult = sleepStateMachine.apply(smoothedStage, streamState.previousFinalStage);

        streamState.previousFinalStage = machineResult.outputStage();
        pushRecent(streamState.recentRespRates, request.respirationRate());
        pushRecent(streamState.recentHeartRates, request.heartRate());

        localSleepHistory.add(new SleepSegment(
                segment.segmentId(),
                segment.windowStart(),
                segment.windowEnd(),
                new EnumMap<>(smoothingResult.smoothedScores())
        ));
        while (localSleepHistory.size() > 2) {
            localSleepHistory.remove(0);
        }

        double confidence = machineResult.outputStage() == smoothedStage
                ? ScoreUtil.confidence(smoothingResult.smoothedScores(), machineResult.stateMachineAdjusted())
                : ScoreUtil.confidenceForStage(smoothingResult.smoothedScores(), machineResult.outputStage());
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
        return analyzeBatch(segments, stepRecords, "default-user");
    }

    public synchronized List<SleepAnalysisResponse> analyzeBatch(List<SleepAnalyzeRequest> segments, List<StepRequest> stepRecords, String userId) {
        List<StepRecord> requestScopedSteps = stepRecords.stream()
                .map(stepRecord -> new StepRecord(
                        stepRecord.timestamp().minus(Duration.ofMinutes(8)),
                        stepRecord.timestamp(),
                        stepRecord.stepCount()
                ))
                .toList();
        StreamState streamState = new StreamState();
        List<SleepSegment> localSleepHistory = new ArrayList<>();
        List<SleepAnalysisResponse> results = new ArrayList<>();
        for (SleepAnalyzeRequest segment : segments) {
            results.add(analyzeOne(segment, streamState, localSleepHistory, userId, requestScopedSteps));
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

    private static class StreamState {
        private final ArrayDeque<Double> recentRespRates = new ArrayDeque<>();
        private final ArrayDeque<Double> recentHeartRates = new ArrayDeque<>();
        private SleepStage previousFinalStage;
    }
}
