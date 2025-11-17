package cn.hutool.extra.tokenizer.engine.mynlp;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import com.mayabot.nlp.segment.Sentence;
import com.mayabot.nlp.segment.WordTerm;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/mynlp/MynlpResult.class */
public class MynlpResult implements Result {
    private final Iterator<WordTerm> result;

    public MynlpResult(Sentence sentence) {
        this.result = sentence.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.result.hasNext();
    }

    @Override // java.util.Iterator
    public Word next() {
        return new MynlpWord(this.result.next());
    }

    @Override // java.util.Iterator
    public void remove() {
        this.result.remove();
    }
}
