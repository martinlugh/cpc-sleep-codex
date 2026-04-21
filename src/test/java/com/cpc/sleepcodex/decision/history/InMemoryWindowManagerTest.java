package com.cpc.sleepcodex.decision.history;

import com.cpc.sleepcodex.decision.domain.SleepSegment;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.domain.StepRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

class InMemoryWindowManagerTest {

    @Test
    void shouldStoreRecentRecords() {
        InMemoryWindowManager manager = new InMemoryWindowManager();
        manager.addSleepSegment(new SleepSegment("s1", Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:05:00Z"), Map.of(SleepStage.WAKE, 1.0)));
        manager.addStepRecord(new StepRecord(Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:08:00Z"), 10));

        Assertions.assertEquals(1, manager.getRecentSleepSegments(3).size());
        Assertions.assertEquals(1, manager.getStepRecordsSnapshot().size());
    }

    @Test
    void shouldPartitionStepRecordsByUser() {
        InMemoryWindowManager manager = new InMemoryWindowManager();
        manager.addStepRecord("user-A", new StepRecord(Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:08:00Z"), 10));
        manager.addStepRecord("user-B", new StepRecord(Instant.parse("2026-04-21T00:00:00Z"), Instant.parse("2026-04-21T00:08:00Z"), 20));

        Assertions.assertEquals(1, manager.getStepRecordsSnapshot("user-A").size());
        Assertions.assertEquals(10, manager.getStepRecordsSnapshot("user-A").get(0).stepCount());
        Assertions.assertEquals(1, manager.getStepRecordsSnapshot("user-B").size());
        Assertions.assertEquals(20, manager.getStepRecordsSnapshot("user-B").get(0).stepCount());
    }
}
