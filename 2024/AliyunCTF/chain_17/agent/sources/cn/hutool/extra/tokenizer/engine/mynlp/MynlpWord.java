package cn.hutool.extra.tokenizer.engine.mynlp;

import cn.hutool.extra.tokenizer.Word;
import com.mayabot.nlp.segment.WordTerm;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/mynlp/MynlpWord.class */
public class MynlpWord implements Word {
    private static final long serialVersionUID = 1;
    private final WordTerm word;

    public MynlpWord(WordTerm word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.getWord();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.word.offset;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return getStartOffset() + this.word.word.length();
    }

    public String toString() {
        return getText();
    }
}
