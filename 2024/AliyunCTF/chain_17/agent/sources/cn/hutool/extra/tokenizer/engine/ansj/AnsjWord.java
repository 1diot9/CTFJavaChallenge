package cn.hutool.extra.tokenizer.engine.ansj;

import cn.hutool.extra.tokenizer.Word;
import org.ansj.domain.Term;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/ansj/AnsjWord.class */
public class AnsjWord implements Word {
    private static final long serialVersionUID = 1;
    private final Term term;

    public AnsjWord(Term term) {
        this.term = term;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.term.getName();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.term.getOffe();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return getStartOffset() + getText().length();
    }

    public String toString() {
        return getText();
    }
}
