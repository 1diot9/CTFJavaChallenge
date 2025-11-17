package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import com.huaban.analysis.jieba.JiebaSegmenter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jieba/JiebaEngine.class */
public class JiebaEngine implements TokenizerEngine {
    private final JiebaSegmenter jiebaSegmenter;
    private final JiebaSegmenter.SegMode mode;

    public JiebaEngine() {
        this(JiebaSegmenter.SegMode.SEARCH);
    }

    public JiebaEngine(JiebaSegmenter.SegMode mode) {
        this.jiebaSegmenter = new JiebaSegmenter();
        this.mode = mode;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        return new JiebaResult(this.jiebaSegmenter.process(StrUtil.str(text), this.mode));
    }
}
