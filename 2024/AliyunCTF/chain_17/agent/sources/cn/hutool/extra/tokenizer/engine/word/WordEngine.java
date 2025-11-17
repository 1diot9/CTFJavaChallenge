package cn.hutool.extra.tokenizer.engine.word;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/word/WordEngine.class */
public class WordEngine implements TokenizerEngine {
    private final Segmentation segmentation;

    public WordEngine() {
        this(SegmentationAlgorithm.BidirectionalMaximumMatching);
    }

    public WordEngine(SegmentationAlgorithm algorithm) {
        this(SegmentationFactory.getSegmentation(algorithm));
    }

    public WordEngine(Segmentation segmentation) {
        this.segmentation = segmentation;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        return new WordResult(this.segmentation.seg(StrUtil.str(text)));
    }
}
