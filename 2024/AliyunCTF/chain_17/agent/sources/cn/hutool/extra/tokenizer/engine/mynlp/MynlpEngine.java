package cn.hutool.extra.tokenizer.engine.mynlp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import com.mayabot.nlp.segment.Lexer;
import com.mayabot.nlp.segment.Lexers;
import com.mayabot.nlp.segment.Sentence;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/mynlp/MynlpEngine.class */
public class MynlpEngine implements TokenizerEngine {
    private final Lexer lexer;

    public MynlpEngine() {
        this.lexer = Lexers.core();
    }

    public MynlpEngine(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override // cn.hutool.extra.tokenizer.TokenizerEngine
    public Result parse(CharSequence text) {
        Sentence sentence = this.lexer.scan(StrUtil.str(text));
        return new MynlpResult(sentence);
    }
}
