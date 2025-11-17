package cn.hutool.extra.tokenizer.engine.ansj;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import java.util.Iterator;
import org.ansj.domain.Term;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/ansj/AnsjResult.class */
public class AnsjResult implements Result {
    private final Iterator<Term> result;

    public AnsjResult(org.ansj.domain.Result ansjResult) {
        this.result = ansjResult.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.result.hasNext();
    }

    @Override // java.util.Iterator
    public Word next() {
        return new AnsjWord(this.result.next());
    }

    @Override // java.util.Iterator
    public void remove() {
        this.result.remove();
    }

    @Override // cn.hutool.core.collection.IterableIter, java.lang.Iterable
    public Iterator<Word> iterator() {
        return this;
    }
}
