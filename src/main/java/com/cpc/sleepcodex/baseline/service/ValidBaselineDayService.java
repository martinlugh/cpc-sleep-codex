package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.BaselineSegment;
import com.cpc.sleepcodex.baseline.model.NightIsolationResult;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;
import com.cpc.sleepcodex.baseline.model.SegmentIsolationResult;
import com.cpc.sleepcodex.baseline.model.ValidBaselineDay;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class ValidBaselineDayService {

    private final RobustStatisticsService robustStatisticsService;

    public ValidBaselineDayService(RobustStatisticsService robustStatisticsService) {
        this.robustStatisticsService = robustStatisticsService;
    }

    public Optional<ValidBaselineDay> buildValidDay(
            String nightId,
            List<BaselineSegment> segments,
            List<SegmentIsolationResult> segmentResults,
            NightIsolationResult nightResult
    ) {
        if (!nightResult.accepted()) {
            return Optional.empty();
        }

        List<BaselineSegment> acceptedSegments = IntStream.range(0, segments.size())
                .filter(i -> segmentResults.get(i).accepted())
                .mapToObj(segments::get)
                .toList();

        Map<BaselineMetricType, RobustMetricStats> stats = new EnumMap<>(BaselineMetricType.class);
        for (BaselineMetricType metricType : BaselineMetricType.values()) {
            List<Double> values = acceptedSegments.stream()
                    .map(s -> s.metrics().get(metricType))
                    .toList();
            stats.put(metricType, robustStatisticsService.compute(values));
        }

        return Optional.of(new ValidBaselineDay(
                nightId,
                stats,
                nightResult.acceptedSegmentCount(),
                nightResult.totalSegmentCount()
        ));
    }
}
