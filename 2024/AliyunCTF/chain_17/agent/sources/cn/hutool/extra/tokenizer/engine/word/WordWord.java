package cn.hutool.extra.tokenizer.engine.word;

import cn.hutool.extra.tokenizer.Word;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/word/WordWord.class */
public class WordWord implements Word {
    private static final long serialVersionUID = 1;
    private final org.apdplat.word.segmentation.Word word;

    public WordWord(org.apdplat.word.segmentation.Word word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.getText();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return -1;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return -1;
    }

    public String toString() {
        return getText();
    }
}
