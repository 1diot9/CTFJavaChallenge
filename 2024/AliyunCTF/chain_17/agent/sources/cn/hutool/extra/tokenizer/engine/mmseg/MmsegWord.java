package cn.hutool.extra.tokenizer.engine.mmseg;

import cn.hutool.extra.tokenizer.Word;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/mmseg/MmsegWord.class */
public class MmsegWord implements Word {
    private static final long serialVersionUID = 1;
    private final com.chenlb.mmseg4j.Word word;

    public MmsegWord(com.chenlb.mmseg4j.Word word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.getString();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.word.getStartOffset();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return this.word.getEndOffset();
    }

    public String toString() {
        return getText();
    }
}
