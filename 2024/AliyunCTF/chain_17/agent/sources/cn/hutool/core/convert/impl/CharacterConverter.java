package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/CharacterConverter.class */
public class CharacterConverter extends AbstractConverter<Character> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Character convertInternal(Object value) {
        if (value instanceof Boolean) {
            return BooleanUtil.toCharacter(((Boolean) value).booleanValue());
        }
        String valueStr = convertToStr(value);
        if (StrUtil.isNotBlank(valueStr)) {
            return Character.valueOf(valueStr.charAt(0));
        }
        return null;
    }
}
