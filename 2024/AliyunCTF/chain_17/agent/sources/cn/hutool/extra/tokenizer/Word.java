package cn.hutool.extra.tokenizer;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/Word.class */
public interface Word extends Serializable {
    String getText();

    int getStartOffset();

    int getEndOffset();
}
