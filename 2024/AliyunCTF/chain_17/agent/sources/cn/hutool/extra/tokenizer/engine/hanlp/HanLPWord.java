package cn.hutool.extra.tokenizer.engine.hanlp;

import cn.hutool.extra.tokenizer.Word;
import com.hankcs.hanlp.seg.common.Term;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/hanlp/HanLPWord.class */
public class HanLPWord implements Word {
    private static final long serialVersionUID = 1;
    private final Term term;

    public HanLPWord(Term term) {
        this.term = term;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.term.word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.term.offset;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return getStartOffset() + this.term.length();
    }

    public String toString() {
        return getText();
    }
}
