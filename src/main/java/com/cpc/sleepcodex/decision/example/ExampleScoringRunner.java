package com.cpc.sleepcodex.decision.example;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;
import com.cpc.sleepcodex.decision.engine.RuleEngine;
import com.cpc.sleepcodex.decision.engine.RuleEngineResult;

import java.time.Instant;
import java.util.List;

public class ExampleScoringRunner {
    public static void main(String[] args) {
        RuleEngine ruleEngine = new RuleEngine();

        AlignedSegmentContext context = new AlignedSegmentContext(
                Instant.parse("2026-04-21T00:05:00Z"),
                0.72,
                0.26,
                0.22,
                1.46,
                0.40,
                12.4,
                54.0,
                42.0,
                0.0,
                List.of(12.2, 12.5, 12.4),
                List.of(52.0, 54.0, 56.0)
        );

        RuleEngineResult result = ruleEngine.evaluate(context);

        // 输出示例分数结果，便于人工核对规则得分
        System.out.println("scores=" + result.scores());
        // 输出命中规则明细，便于解释性追踪
        result.ruleHits().forEach(hit -> System.out.println(hit.ruleCode() + "|" + hit.ruleName() + "|" + hit.hitReason() + "|" + hit.scoreContribution()));
    }
}
