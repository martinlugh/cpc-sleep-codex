package com.cpc.sleepcodex.decision.domain;

import java.time.Instant;
import java.util.Map;

public record SleepSegment(
        String segmentId,
        Instant windowStart,
        Instant windowEnd,
        Map<SleepStage, Double> originalScores
) {
}
