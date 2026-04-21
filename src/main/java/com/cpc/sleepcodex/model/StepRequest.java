package com.cpc.sleepcodex.model;

import java.time.Instant;

public record StepRequest(
        Instant timestamp,
        int stepCount
) {
}
