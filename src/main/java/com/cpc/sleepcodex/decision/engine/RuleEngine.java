package com.cpc.sleepcodex.decision.engine;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.domain.SleepStage;
import com.cpc.sleepcodex.decision.rule.DeepRuleSet;
import com.cpc.sleepcodex.decision.rule.LightRuleSet;
import com.cpc.sleepcodex.decision.rule.RemRuleSet;
import com.cpc.sleepcodex.decision.rule.RuleHit;
import com.cpc.sleepcodex.decision.rule.SleepStageRule;
import com.cpc.sleepcodex.decision.rule.WakeRuleSet;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RuleEngine {

    private final List<SleepStageRule> rules;

    public RuleEngine() {
        this.rules = List.of(
                new WakeRuleSet(),
                new DeepRuleSet(),
                new RemRuleSet(),
                new LightRuleSet()
        );
    }

    public RuleEngine(List<SleepStageRule> rules) {
        this.rules = rules;
    }

    public RuleEngineResult evaluate(AlignedSegmentContext context) {
        Map<SleepStage, Double> scores = new EnumMap<>(SleepStage.class);
        for (SleepStage stage : SleepStage.values()) {
            scores.put(stage, 0.0);
        }

        List<RuleHit> allHits = new ArrayList<>();
        for (SleepStageRule rule : rules) {
            List<RuleHit> hits = rule.evaluate(context);
            allHits.addAll(hits);
            for (RuleHit hit : hits) {
                scores.compute(hit.targetStage(), (k, v) -> v == null ? hit.scoreContribution() : v + hit.scoreContribution());
            }
        }

        return new RuleEngineResult(scores, allHits);
    }
}
