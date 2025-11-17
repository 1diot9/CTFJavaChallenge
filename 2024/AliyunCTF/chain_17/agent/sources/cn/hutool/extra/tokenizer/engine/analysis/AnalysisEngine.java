package cn.hutool.extra.tokenizer.engine.analysis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/analysis/AnalysisEngine.class */
public class AnalysisEngine implements TokenizerEngine {
    private final Analyzer analyzer;

    public AnalysisEngine(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        try {
            TokenStream stream = this.analyzer.tokenStream("text", StrUtil.str(text));
            stream.reset();
            return new AnalysisResult(stream);
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
