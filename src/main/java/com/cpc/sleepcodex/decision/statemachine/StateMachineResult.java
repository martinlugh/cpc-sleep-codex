package com.cpc.sleepcodex.decision.statemachine;

import com.cpc.sleepcodex.decision.domain.SleepStage;

public record StateMachineResult(
        SleepStage inputStage,
        SleepStage outputStage,
        boolean stateMachineAdjusted,
        String reason
) {
}
