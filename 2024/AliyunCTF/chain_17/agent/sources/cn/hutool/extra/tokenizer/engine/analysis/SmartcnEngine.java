package cn.hutool.extra.tokenizer.engine.analysis;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/analysis/SmartcnEngine.class */
public class SmartcnEngine extends AnalysisEngine {
    public SmartcnEngine() {
        super(new SmartChineseAnalyzer());
    }
}
