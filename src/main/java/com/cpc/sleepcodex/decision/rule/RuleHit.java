package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.SleepStage;

public record RuleHit(
        String ruleCode,
        String ruleName,
        String hitReason,
        SleepStage targetStage,
        double scoreContribution
) {
}
