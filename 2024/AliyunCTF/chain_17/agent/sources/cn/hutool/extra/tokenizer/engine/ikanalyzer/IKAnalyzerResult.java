package cn.hutool.extra.tokenizer.engine.ikanalyzer;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import java.io.IOException;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/ikanalyzer/IKAnalyzerResult.class */
public class IKAnalyzerResult extends AbstractResult {
    private final IKSegmenter seg;

    public IKAnalyzerResult(IKSegmenter seg) {
        this.seg = seg;
    }

    @Override // cn.hutool.extra.tokenizer.AbstractResult
    protected Word nextWord() {
        try {
            Lexeme next = this.seg.next();
            if (null == next) {
                return null;
            }
            return new IKAnalyzerWord(next);
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
