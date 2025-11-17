package cn.hutool.extra.tokenizer.engine.ikanalyzer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import java.io.Reader;
import org.wltea.analyzer.core.IKSegmenter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/ikanalyzer/IKAnalyzerEngine.class */
public class IKAnalyzerEngine implements TokenizerEngine {
    private final IKSegmenter seg;

    public IKAnalyzerEngine() {
        this(new IKSegmenter((Reader) null, true));
    }

    public IKAnalyzerEngine(IKSegmenter seg) {
        this.seg = seg;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        this.seg.reset(StrUtil.getReader(text));
        return new IKAnalyzerResult(this.seg);
    }
}
