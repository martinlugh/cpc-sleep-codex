package com.cpc.sleepcodex.decision.domain;

import java.time.Instant;

public record StepRecord(
        Instant windowStart,
        Instant windowEnd,
        int stepCount
) {
}
