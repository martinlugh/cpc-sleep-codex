package com.cpc.sleepcodex.baseline.model;

public record SegmentIsolationResult(
        boolean accepted,
        String reason
) {
}
