package cn.hutool.extra.tokenizer.engine.analysis;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/analysis/AnalysisResult.class */
public class AnalysisResult extends AbstractResult {
    private final TokenStream stream;

    public AnalysisResult(TokenStream stream) {
        this.stream = stream;
    }

    @Override // cn.hutool.extra.tokenizer.AbstractResult
    protected Word nextWord() {
        try {
            if (this.stream.incrementToken()) {
                return new AnalysisWord(this.stream.getAttribute(CharTermAttribute.class));
            }
            return null;
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
