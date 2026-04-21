package com.cpc.sleepcodex.baseline.model;

public record NightIsolationResult(
        boolean accepted,
        String reason,
        int acceptedSegmentCount,
        int totalSegmentCount
) {
}
