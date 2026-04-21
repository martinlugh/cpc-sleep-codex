package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.BaselineSegment;
import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class UserBaselineServiceTest {

    @Test
    void shouldUpgradeLevelByValidDaysOnly() {
        UserBaselineService service = new UserBaselineService();

        for (int i = 0; i < 3; i++) {
            service.updateNight("u1", "n" + i, stableNight(Instant.parse("2026-04-2" + (i + 1) + "T00:00:00Z")));
        }

        UserBaselineSnapshot snapshot = service.snapshot("u1");
        Assertions.assertEquals(BaselineLevel.DAY_3, snapshot.level());
        Assertions.assertFalse(snapshot.statsByMetric().isEmpty());
    }

    @Test
    void shouldRejectWakeDominatedNight() {
        UserBaselineService service = new UserBaselineService();
        List<BaselineSegment> wakeNight = List.of(
                segment("WAKE", 0.95, 80, 18, 12, 1.1, 0.3, 0.4, 0.7),
                segment("WAKE", 0.96, 82, 19, 11, 1.0, 0.2, 0.3, 0.8)
        );

        UserBaselineSnapshot snapshot = service.updateNight("u2", "bad-night", wakeNight);

        Assertions.assertEquals(BaselineLevel.GENERIC, snapshot.level());
        Assertions.assertEquals(0, snapshot.validDayCount());
    }

    @Test
    void shouldRejectNightWithMissingBaselineMetric() {
        UserBaselineService service = new UserBaselineService();
        List<BaselineSegment> badNight = stableNight(Instant.parse("2026-04-21T00:00:00Z")).stream()
                .map(s -> new BaselineSegment(s.timestamp(), s.sleepStage(), s.confidence(), dropRmssd(s.metrics())))
                .toList();

        UserBaselineSnapshot snapshot = service.updateNight("u3", "missing-metric-night", badNight);

        Assertions.assertEquals(BaselineLevel.GENERIC, snapshot.level());
        Assertions.assertEquals(0, snapshot.validDayCount());
    }

    private List<BaselineSegment> stableNight(Instant start) {
        return List.of(
                segment("LIGHT", 0.75, 61, 13.2, 32, 0.58, 1.15, 0.61, 0.32),
                segment("DEEP", 0.81, 56, 12.3, 42, 0.41, 1.38, 0.72, 0.22),
                segment("LIGHT", 0.77, 60, 13.0, 35, 0.54, 1.21, 0.63, 0.29),
                segment("DEEP", 0.84, 55, 12.1, 44, 0.39, 1.45, 0.74, 0.21),
                segment("REM", 0.70, 63, 14.1, 30, 0.65, 1.12, 0.57, 0.34),
                segment("LIGHT", 0.76, 59, 13.1, 33, 0.53, 1.20, 0.64, 0.30)
        ).stream().map(s -> new BaselineSegment(start, s.sleepStage(), s.confidence(), s.metrics())).toList();
    }

    private BaselineSegment segment(
            String stage,
            double confidence,
            double heartRate,
            double respirationRate,
            double rmssd,
            double entropy,
            double couplingRatio,
            double hfc,
            double lfc
    ) {
        Map<BaselineMetricType, Double> metrics = new EnumMap<>(BaselineMetricType.class);
        metrics.put(BaselineMetricType.HEART_RATE, heartRate);
        metrics.put(BaselineMetricType.RESPIRATION_RATE, respirationRate);
        metrics.put(BaselineMetricType.RMSSD, rmssd);
        metrics.put(BaselineMetricType.SAMPLE_ENTROPY, entropy);
        metrics.put(BaselineMetricType.COUPLING_RATIO, couplingRatio);
        metrics.put(BaselineMetricType.HFC, hfc);
        metrics.put(BaselineMetricType.LFC, lfc);
        metrics.put(BaselineMetricType.VLFC, 0.22);
        return new BaselineSegment(Instant.parse("2026-04-21T00:00:00Z"), stage, confidence, metrics);
    }

    private Map<BaselineMetricType, Double> dropRmssd(Map<BaselineMetricType, Double> metrics) {
        Map<BaselineMetricType, Double> result = new EnumMap<>(metrics);
        result.remove(BaselineMetricType.RMSSD);
        return result;
    }
}
