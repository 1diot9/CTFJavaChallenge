package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import java.io.IOException;
import java.io.StringReader;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/jcseg/JcsegEngine.class */
public class JcsegEngine implements TokenizerEngine {
    private final ISegment segment;

    public JcsegEngine() {
        SegmenterConfig config = new SegmenterConfig(true);
        ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
        this.segment = ISegment.COMPLEX.factory.create(config, dic);
    }

    public JcsegEngine(ISegment segment) {
        this.segment = segment;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        try {
            this.segment.reset(new StringReader(StrUtil.str(text)));
            return new JcsegResult(this.segment);
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
    }
}
