package com.cpc.sleepcodex.decision.history;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.StepRecord;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryWindowManager {

    private static final int MAX_SLEEP_SEGMENTS = 36;
    private static final int MAX_STEP_RECORDS = 36;
    private static final String DEFAULT_USER = "default-user";

    private final Deque<SleepSegment> sleepSegments = new ArrayDeque<>();
    private final Map<String, Deque<StepRecord>> stepRecordsByUser = new HashMap<>();

    public synchronized void addSleepSegment(SleepSegment segment) {
        sleepSegments.addLast(segment);
        while (sleepSegments.size() > MAX_SLEEP_SEGMENTS) {
            sleepSegments.removeFirst();
        }
    }

    public synchronized void addStepRecord(StepRecord record) {
        addStepRecord(DEFAULT_USER, record);
    }

    public synchronized void addStepRecord(String userId, StepRecord record) {
        Deque<StepRecord> stepRecords = stepRecordsByUser.computeIfAbsent(normalizeUserId(userId), k -> new ArrayDeque<>());
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
        return getStepRecordsSnapshot(DEFAULT_USER);
    }

    public synchronized List<StepRecord> getStepRecordsSnapshot(String userId) {
        Deque<StepRecord> stepRecords = stepRecordsByUser.getOrDefault(normalizeUserId(userId), new ArrayDeque<>());
        return new ArrayList<>(stepRecords);
    }

    private String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return DEFAULT_USER;
        }
        return userId;
    }
}
