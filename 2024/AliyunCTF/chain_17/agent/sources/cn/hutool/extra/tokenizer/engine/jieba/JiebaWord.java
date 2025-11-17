package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.extra.tokenizer.Word;
import com.huaban.analysis.jieba.SegToken;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jieba/JiebaWord.class */
public class JiebaWord implements Word {
    private static final long serialVersionUID = 1;
    private final SegToken segToken;

    public JiebaWord(SegToken segToken) {
        this.segToken = segToken;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.segToken.word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.segToken.startOffset;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return this.segToken.endOffset;
    }

    public String toString() {
        return getText();
    }
}
