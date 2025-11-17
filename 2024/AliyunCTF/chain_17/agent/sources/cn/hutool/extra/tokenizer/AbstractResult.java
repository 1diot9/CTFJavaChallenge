package cn.hutool.extra.tokenizer;

import cn.hutool.core.collection.ComputeIter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/AbstractResult.class */
public abstract class AbstractResult extends ComputeIter<Word> implements Result {
    protected abstract Word nextWord();

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.collection.ComputeIter
    public Word computeNext() {
        return nextWord();
    }
}
