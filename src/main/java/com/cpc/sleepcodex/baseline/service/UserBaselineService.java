package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.BaselineSegment;
import com.cpc.sleepcodex.baseline.model.NightIsolationResult;
import com.cpc.sleepcodex.baseline.model.RobustMetricStats;
import com.cpc.sleepcodex.baseline.model.SegmentIsolationResult;
import com.cpc.sleepcodex.baseline.model.UserBaselineProfile;
import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;
import com.cpc.sleepcodex.baseline.model.ValidBaselineDay;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserBaselineService {

    private final ConcurrentHashMap<String, UserBaselineProfile> userProfiles = new ConcurrentHashMap<>();
    private final SegmentIsolationService segmentIsolationService;
    private final NightIsolationService nightIsolationService;
    private final ValidBaselineDayService validBaselineDayService;
    private final BaselineLevelSelector baselineLevelSelector;
    private final RobustStatisticsService robustStatisticsService;

    public UserBaselineService() {
        this.segmentIsolationService = new SegmentIsolationService();
        this.nightIsolationService = new NightIsolationService();
        this.robustStatisticsService = new RobustStatisticsService();
        this.validBaselineDayService = new ValidBaselineDayService(robustStatisticsService);
        this.baselineLevelSelector = new BaselineLevelSelector();
    }

    public UserBaselineSnapshot updateNight(String userId, String nightId, List<BaselineSegment> segments) {
        UserBaselineProfile profile = userProfiles.computeIfAbsent(userId, UserBaselineProfile::new);
        synchronized (profile) {
            List<SegmentIsolationResult> segmentResults = new ArrayList<>();
            for (BaselineSegment segment : segments) {
                segmentResults.add(segmentIsolationService.isolate(segment));
            }

            NightIsolationResult nightResult = nightIsolationService.isolate(segmentResults);
            Optional<ValidBaselineDay> validDay = validBaselineDayService.buildValidDay(
                    nightId,
                    segments,
                    segmentResults,
                    nightResult
            );
            validDay.ifPresent(profile::addValidDay);

            return snapshotOf(profile);
        }
    }

    public UserBaselineSnapshot snapshot(String userId) {
        UserBaselineProfile profile = userProfiles.computeIfAbsent(userId, UserBaselineProfile::new);
        synchronized (profile) {
            return snapshotOf(profile);
        }
    }

    private UserBaselineSnapshot snapshotOf(UserBaselineProfile profile) {
        List<ValidBaselineDay> days = profile.validDaysSnapshot();
        BaselineLevel level = baselineLevelSelector.select(days.size());
        if (level == BaselineLevel.GENERIC) {
            return new UserBaselineSnapshot(profile.userId(), level, days.size(), Map.of());
        }

        int window = switch (level) {
            case DAY_3 -> 3;
            case DAY_7 -> 7;
            case DAY_21 -> 21;
            case GENERIC -> 0;
        };

        List<ValidBaselineDay> selected = days.subList(Math.max(0, days.size() - window), days.size());
        Map<BaselineMetricType, RobustMetricStats> stats = new EnumMap<>(BaselineMetricType.class);
        for (BaselineMetricType metricType : BaselineMetricType.values()) {
            List<Double> medians = selected.stream()
                    .map(day -> day.statsByMetric().get(metricType).median())
                    .toList();
            stats.put(metricType, robustStatisticsService.compute(medians));
        }

        return new UserBaselineSnapshot(profile.userId(), level, days.size(), stats);
    }
}
