package cn.hutool.extra.tokenizer.engine.hanlp;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import com.hankcs.hanlp.seg.common.Term;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/hanlp/HanLPResult.class */
public class HanLPResult implements Result {
    Iterator<Term> result;

    public HanLPResult(List<Term> termList) {
        this.result = termList.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.result.hasNext();
    }

    @Override // java.util.Iterator
    public Word next() {
        return new HanLPWord(this.result.next());
    }

    @Override // java.util.Iterator
    public void remove() {
        this.result.remove();
    }
}
