package cn.hutool.extra.tokenizer.engine.analysis;

import cn.hutool.extra.tokenizer.Word;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Attribute;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/analysis/AnalysisWord.class */
public class AnalysisWord implements Word {
    private static final long serialVersionUID = 1;
    private final Attribute word;

    public AnalysisWord(CharTermAttribute word) {
        this.word = word;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public String getText() {
        return this.word.toString();
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getStartOffset() {
        if (this.word instanceof OffsetAttribute) {
            return this.word.startOffset();
        }
        return -1;
    }

    @Override // cn.hutool.extra.tokenizer.Word
    public int getEndOffset() {
        if (this.word instanceof OffsetAttribute) {
            return this.word.endOffset();
        }
        return -1;
    }

    public String toString() {
        return getText();
    }
}
