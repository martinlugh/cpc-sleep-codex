package com.cpc.sleepcodex.model;

import java.util.List;

public record SleepBatchRequest(
        List<SleepAnalyzeRequest> sleepSegments,
        List<StepRequest> stepRecords
) {
}
