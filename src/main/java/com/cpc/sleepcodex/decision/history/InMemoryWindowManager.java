package com.cpc.sleepcodex.decision.history;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.StepRecord;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class InMemoryWindowManager {

    private static final int MAX_SLEEP_SEGMENTS = 36;
    private static final int MAX_STEP_RECORDS = 36;

    private final Deque<SleepSegment> sleepSegments = new ArrayDeque<>();
    private final Deque<StepRecord> stepRecords = new ArrayDeque<>();

    public synchronized void addSleepSegment(SleepSegment segment) {
        sleepSegments.addLast(segment);
        while (sleepSegments.size() > MAX_SLEEP_SEGMENTS) {
            sleepSegments.removeFirst();
        }
    }

    public synchronized void addStepRecord(StepRecord record) {
        stepRecords.addLast(record);
        while (stepRecords.size() > MAX_STEP_RECORDS) {
            stepRecords.removeFirst();
        }
    }

    public synchronized List<SleepSegment> getRecentSleepSegments(int count) {
        List<SleepSegment> snapshot = new ArrayList<>(sleepSegments);
        int from = Math.max(0, snapshot.size() - count);
        return snapshot.subList(from, snapshot.size());
    }

    public synchronized List<StepRecord> getStepRecordsSnapshot() {
        return new ArrayList<>(stepRecords);
    }
}
