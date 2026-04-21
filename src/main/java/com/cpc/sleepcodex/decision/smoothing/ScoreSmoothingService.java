package com.cpc.sleepcodex.decision.smoothing;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScoreSmoothingService {

    public SmoothingResult smooth(Map<SleepStage, Double> currentScores, List<SleepSegment> recentSegments) {
        EnumMap<SleepStage, Double> smoothed = new EnumMap<>(SleepStage.class);
        int useHistory = Math.min(2, recentSegments.size());
        double currentWeight = 0.6;
        double prev1Weight = useHistory >= 1 ? 0.3 : 0.0;
        double prev2Weight = useHistory >= 2 ? 0.1 : 0.0;
        double totalWeight = currentWeight + prev1Weight + prev2Weight;

        Map<SleepStage, Double> prev1 = useHistory >= 1
                ? recentSegments.get(recentSegments.size() - 1).originalScores()
                : Map.of();
        Map<SleepStage, Double> prev2 = useHistory >= 2
                ? recentSegments.get(recentSegments.size() - 2).originalScores()
                : Map.of();

        for (SleepStage stage : SleepStage.values()) {
            double value = currentWeight * currentScores.getOrDefault(stage, 0.0)
                    + prev1Weight * prev1.getOrDefault(stage, 0.0)
                    + prev2Weight * prev2.getOrDefault(stage, 0.0);
            smoothed.put(stage, value / totalWeight);
        }

        return new SmoothingResult(currentScores, smoothed, useHistory > 0);
    }
}
