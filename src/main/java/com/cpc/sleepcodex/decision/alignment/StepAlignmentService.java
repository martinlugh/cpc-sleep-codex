package com.cpc.sleepcodex.decision.alignment;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.StepRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class StepAlignmentService {

    public double alignStepCount(SleepSegment sleepSegment, List<StepRecord> stepRecords) {
        double alignedSteps = 0.0;
        for (StepRecord record : stepRecords) {
            long overlapSeconds = overlapSeconds(
                    sleepSegment.windowStart(), sleepSegment.windowEnd(),
                    record.windowStart(), record.windowEnd()
            );
            if (overlapSeconds > 0) {
                long stepWindowSeconds = Duration.between(record.windowStart(), record.windowEnd()).getSeconds();
                alignedSteps += record.stepCount() * (overlapSeconds * 1.0 / stepWindowSeconds);
            }
        }
        return alignedSteps;
    }

    private long overlapSeconds(Instant aStart, Instant aEnd, Instant bStart, Instant bEnd) {
        Instant start = aStart.isAfter(bStart) ? aStart : bStart;
        Instant end = aEnd.isBefore(bEnd) ? aEnd : bEnd;
        if (!start.isBefore(end)) {
            return 0;
        }
        return Duration.between(start, end).getSeconds();
    }
}
