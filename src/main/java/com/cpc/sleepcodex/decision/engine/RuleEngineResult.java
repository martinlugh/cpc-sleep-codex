package com.cpc.sleepcodex.decision.engine;

import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.rule.RuleHit;

import java.util.List;
import java.util.Map;

public record RuleEngineResult(
        Map<SleepStage, Double> scores,
        List<RuleHit> ruleHits
) {
}
