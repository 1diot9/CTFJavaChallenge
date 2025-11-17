package cn.hutool.extra.tokenizer.engine.mmseg;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import com.chenlb.mmseg4j.MMSeg;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/mmseg/MmsegResult.class */
public class MmsegResult extends AbstractResult {
    private final MMSeg mmSeg;

    public MmsegResult(MMSeg mmSeg) {
        this.mmSeg = mmSeg;
    }

    @Override // cn.hutool.extra.tokenizer.AbstractResult
    protected Word nextWord() {
        try {
            com.chenlb.mmseg4j.Word next = this.mmSeg.next();
            if (null == next) {
                return null;
            }
            return new MmsegWord(next);
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
