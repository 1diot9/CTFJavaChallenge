package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.CharsetUtil;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/CharsetConverter.class */
public class CharsetConverter extends AbstractConverter<Charset> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Charset convertInternal(Object value) {
        return CharsetUtil.charset(convertToStr(value));
    }
}
