package cn.hutool.extra.tokenizer.engine.word;

import cn.hutool.extra.tokenizer.Result;
import java.util.Iterator;
import java.util.List;
import org.apdplat.word.segmentation.Word;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/word/WordResult.class */
public class WordResult implements Result {
    private final Iterator<Word> wordIter;

    public WordResult(List<Word> result) {
        this.wordIter = result.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.wordIter.hasNext();
    }

    @Override // java.util.Iterator
    public cn.hutool.extra.tokenizer.Word next() {
        return new WordWord(this.wordIter.next());
    }

    @Override // java.util.Iterator
    public void remove() {
        this.wordIter.remove();
    }
}
