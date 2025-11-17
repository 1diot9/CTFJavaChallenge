package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import java.io.IOException;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jcseg/JcsegResult.class */
public class JcsegResult extends AbstractResult {
    private final ISegment result;

    public JcsegResult(ISegment segment) {
        this.result = segment;
    }

    @Override // cn.hutool.extra.tokenizer.AbstractResult
    protected Word nextWord() {
        try {
            IWord word = this.result.next();
            if (null == word) {
                return null;
            }
            return new JcsegWord(word);
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
