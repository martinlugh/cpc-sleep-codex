package com.cpc.sleepcodex.decision.statemachine;

import com.cpc.sleepcodex.decision.domain.SleepStage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SleepStateMachineTest {

    @Test
    void shouldBlockDirectWakeDeepJump() {
        StateMachineResult result = new SleepStateMachine().apply(SleepStage.DEEP, SleepStage.WAKE);

        Assertions.assertTrue(result.stateMachineAdjusted());
        Assertions.assertEquals(SleepStage.LIGHT, result.outputStage());
    }

    @Test
    void shouldKeepReasonableTransition() {
        StateMachineResult result = new SleepStateMachine().apply(SleepStage.REM, SleepStage.DEEP);

        Assertions.assertFalse(result.stateMachineAdjusted());
        Assertions.assertEquals(SleepStage.REM, result.outputStage());
    }
}
