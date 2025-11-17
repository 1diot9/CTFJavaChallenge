package cn.hutool.extra.tokenizer.engine.ikanalyzer;

import cn.hutool.extra.tokenizer.Word;
import org.wltea.analyzer.core.Lexeme;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/ikanalyzer/IKAnalyzerWord.class */
public class IKAnalyzerWord implements Word {
    private static final long serialVersionUID = 1;
    private final Lexeme word;

    public IKAnalyzerWord(Lexeme word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.getLexemeText();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        return this.word.getBeginPosition();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        return this.word.getEndPosition();
    }

    public String toString() {
        return getText();
    }
}
