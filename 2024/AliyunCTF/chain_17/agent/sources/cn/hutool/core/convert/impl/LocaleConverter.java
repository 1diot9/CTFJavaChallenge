package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.StrUtil;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/LocaleConverter.class */
public class LocaleConverter extends AbstractConverter<Locale> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Locale convertInternal(Object value) {
        try {
            String str = convertToStr(value);
            if (StrUtil.isEmpty(str)) {
                return null;
            }
            String[] items = str.split("_");
            if (items.length == 1) {
                return new Locale(items[0]);
            }
            if (items.length == 2) {
                return new Locale(items[0], items[1]);
            }
            return new Locale(items[0], items[1], items[2]);
        } catch (Exception e) {
            return null;
        }
    }
}
