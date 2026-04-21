package com.cpc.sleepcodex.decision.statemachine;

import com.cpc.sleepcodex.decision.domain.SleepStage;

public class SleepStateMachine {

    public StateMachineResult apply(SleepStage candidateStage, SleepStage previousFinalStage) {
        if (previousFinalStage == null) {
            return new StateMachineResult(candidateStage, candidateStage, false, "首段无需状态机修正");
        }

        boolean wakeToDeep = previousFinalStage == SleepStage.WAKE && candidateStage == SleepStage.DEEP;
        boolean deepToWake = previousFinalStage == SleepStage.DEEP && candidateStage == SleepStage.WAKE;

        if (wakeToDeep || deepToWake) {
            return new StateMachineResult(candidateStage, SleepStage.LIGHT, true, "阻断WAKE与DEEP直接跳变，调整为LIGHT过渡");
        }

        return new StateMachineResult(candidateStage, candidateStage, false, "状态转移合理，无需修正");
    }
}
