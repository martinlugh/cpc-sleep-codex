# cpc-sleep-codex

This is an engineering approximation based on CPC-inspired features and rule-based modeling.

## 运行

```bash
mvn spring-boot:run
```

## API

### 1) POST /step

请求:
```json
{
  "timestamp": "2026-04-21T00:08:00Z",
  "stepCount": 36
}
```

响应:
```json
{
  "status": "OK"
}
```

### 2) POST /sleep/analyze

请求:
```json
{
  "timestamp": "2026-04-21T00:10:00Z",
  "hfc": 0.68,
  "lfc": 0.24,
  "vlfc": 0.21,
  "couplingRatio": 1.35,
  "sd1": 22.1,
  "sd2": 45.2,
  "sampleEntropy": 0.44,
  "respirationRate": 12.1,
  "heartRate": 56.0,
  "rmssd": 40.0,
  "crossSpectralPower": null
}
```

响应:
```json
{
  "sleepStage": "DEEP",
  "confidence": 0.42,
  "ruleExplanation": "高耦合比深睡规则(耦合比偏高，睡眠耦合稳定); 静息步数深睡规则(步数接近0，身体活动低)",
  "featureSummary": "heartRate=56.0, respirationRate=12.1, couplingRatio=1.35, alignedStepCount=22.50",
  "smoothingApplied": true,
  "stateMachineAdjusted": false
}
```

### 3) POST /sleep/batch

请求:
```json
{
  "stepRecords": [
    {
      "timestamp": "2026-04-21T00:08:00Z",
      "stepCount": 20
    },
    {
      "timestamp": "2026-04-21T00:16:00Z",
      "stepCount": 0
    }
  ],
  "sleepSegments": [
    {
      "timestamp": "2026-04-21T00:10:00Z",
      "hfc": 0.62,
      "lfc": 0.26,
      "vlfc": 0.20,
      "couplingRatio": 1.30,
      "sd1": 20.0,
      "sd2": 41.0,
      "sampleEntropy": 0.52,
      "respirationRate": 12.6,
      "heartRate": 58.0,
      "rmssd": 32.0,
      "crossSpectralPower": null
    }
  ]
}
```

响应:
```json
[
  {
    "sleepStage": "LIGHT",
    "confidence": 0.21,
    "ruleExplanation": "中间态LIGHT规则(多项指标位于中间区间)",
    "featureSummary": "heartRate=58.0, respirationRate=12.6, couplingRatio=1.3, alignedStepCount=12.50",
    "smoothingApplied": true,
    "stateMachineAdjusted": false
  }
]
```


## 模拟输入与输出测试

模拟输入（3段睡眠+3段步数）：

```json
{
  "stepRecords": [
    {"windowStart": "2026-04-21T00:00:00Z", "windowEnd": "2026-04-21T00:08:00Z", "stepCount": 40},
    {"windowStart": "2026-04-21T00:08:00Z", "windowEnd": "2026-04-21T00:16:00Z", "stepCount": 4},
    {"windowStart": "2026-04-21T00:16:00Z", "windowEnd": "2026-04-21T00:24:00Z", "stepCount": 0}
  ],
  "sleepSegments": [
    {"timestamp": "2026-04-21T00:10:00Z", "hfc": 0.30, "lfc": 0.35, "vlfc": 0.72, "couplingRatio": 0.80, "sampleEntropy": 0.86, "respirationRate": 17.0, "heartRate": 78.0, "rmssd": 14.0},
    {"timestamp": "2026-04-21T00:15:00Z", "hfc": 0.55, "lfc": 0.30, "vlfc": 0.32, "couplingRatio": 1.10, "sampleEntropy": 0.60, "respirationRate": 13.5, "heartRate": 63.0, "rmssd": 28.0},
    {"timestamp": "2026-04-21T00:20:00Z", "hfc": 0.76, "lfc": 0.24, "vlfc": 0.20, "couplingRatio": 1.48, "sampleEntropy": 0.39, "respirationRate": 12.1, "heartRate": 54.0, "rmssd": 44.0}
  ]
}
```

运行命令：

```bash
javac $(rg --files src/main/java/com/cpc/sleepcodex/decision src/main/java/com/cpc/sleepcodex/model src/main/java/com/cpc/sleepcodex/util | tr '\n' ' ') && \
java -cp src/main/java com.cpc.sleepcodex.decision.example.SimulationDataRunner
```

示例输出：

```text
SleepAnalysisResponse[sleepStage=WAKE, confidence=0.7659574468085106, ruleExplanation=活动步数唤醒规则(步数大于0，提示清醒活动); 高VLFC清醒规则(VLFC偏高，偏向清醒); 低耦合比清醒规则(耦合比偏低，睡眠耦合不足), featureSummary=heartRate=78.0, respirationRate=17.0, couplingRatio=0.8, alignedStepCount=16.00, smoothingApplied=false, stateMachineAdjusted=false]
SleepAnalysisResponse[sleepStage=WAKE, confidence=0.28888888888888864, ruleExplanation=活动步数唤醒规则(步数大于0，提示清醒活动); 呼吸不稳定清醒规则(近3段呼吸波动较大), featureSummary=heartRate=63.0, respirationRate=13.5, couplingRatio=1.1, alignedStepCount=2.50, smoothingApplied=true, stateMachineAdjusted=false]
SleepAnalysisResponse[sleepStage=LIGHT, confidence=0.0, ruleExplanation=, featureSummary=heartRate=54.0, respirationRate=12.1, couplingRatio=1.48, alignedStepCount=0.50, smoothingApplied=true, stateMachineAdjusted=true]
```

## 建议存储到数据库的内容

当前工程不包含数据库实现。若业务侧需要落库，建议存储以下数据：

1. 用户与时间维度
   - userId
   - timestamp
   - nightId（业务定义的夜间标识）

2. 原始输入数据
   - 5分钟睡眠特征：hfc、lfc、vlfc、couplingRatio、sd1、sd2、sampleEntropy、respirationRate、heartRate、rmssd、crossSpectralPower
   - 8分钟步数：stepCount

3. 决策输出数据
   - sleepStage
   - confidence
   - ruleExplanation
   - featureSummary
   - smoothingApplied
   - stateMachineAdjusted

4. 基线相关数据（按用户）
   - validBaselineDay 明细（nightId、acceptedSegmentCount、totalSegmentCount）
   - baseline level（GENERIC / DAY_3 / DAY_7 / DAY_21）
   - 基线统计值（8个指标的 median、IQR、MAD、sampleCount）
   - 段级隔离结果（accepted、reason）
   - 夜级隔离结果（accepted、reason、acceptedSegmentCount、totalSegmentCount）

5. 规则审计数据（可选）
   - 每段命中规则列表（ruleCode、ruleName、hitReason、scoreContribution）
   - 使用阈值来源（通用阈值 / 个体基线 + 基线等级）
