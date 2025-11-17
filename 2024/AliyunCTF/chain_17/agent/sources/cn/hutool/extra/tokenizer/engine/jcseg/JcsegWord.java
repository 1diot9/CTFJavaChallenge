package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.extra.tokenizer.Word;
import org.lionsoul.jcseg.IWord;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jcseg/JcsegWord.class */
public class JcsegWord implements Word {
    private static final long serialVersionUID = 1;
    private final IWord word;

    public JcsegWord(IWord word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.getValue();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.word.getPosition();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return getStartOffset() + this.word.getLength();
    }

    public String toString() {
        return getText();
    }
}
