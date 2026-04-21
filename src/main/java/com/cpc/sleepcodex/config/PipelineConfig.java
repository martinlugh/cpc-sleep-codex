package com.cpc.sleepcodex.config;

import com.cpc.sleepcodex.decision.alignment.StepAlignmentService;
import com.cpc.sleepcodex.decision.engine.RuleEngine;
import com.cpc.sleepcodex.decision.history.InMemoryWindowManager;
import com.cpc.sleepcodex.decision.smoothing.ScoreSmoothingService;
import com.cpc.sleepcodex.decision.statemachine.SleepStateMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineConfig {

    @Bean
    public RuleEngine ruleEngine() {
        return new RuleEngine();
    }

    @Bean
    public StepAlignmentService stepAlignmentService() {
        return new StepAlignmentService();
    }

    @Bean
    public ScoreSmoothingService scoreSmoothingService() {
        return new ScoreSmoothingService();
    }

    @Bean
    public SleepStateMachine sleepStateMachine() {
        return new SleepStateMachine();
    }

    @Bean
    public InMemoryWindowManager inMemoryWindowManager() {
        return new InMemoryWindowManager();
    }
}
