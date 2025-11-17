package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import com.huaban.analysis.jieba.SegToken;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jieba/JiebaResult.class */
public class JiebaResult implements Result {
    Iterator<SegToken> result;

    public JiebaResult(List<SegToken> segTokenList) {
        this.result = segTokenList.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.result.hasNext();
    }

    @Override // java.util.Iterator
    public Word next() {
        return new JiebaWord(this.result.next());
    }

    @Override // java.util.Iterator
    public void remove() {
        this.result.remove();
    }
}
