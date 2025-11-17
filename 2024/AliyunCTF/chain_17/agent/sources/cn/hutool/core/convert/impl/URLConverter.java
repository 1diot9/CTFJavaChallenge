package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/URLConverter.class */
public class URLConverter extends AbstractConverter<URL> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public URL convertInternal(Object value) {
        try {
            if (value instanceof File) {
                return ((File) value).toURI().toURL();
            }
            if (value instanceof URI) {
                return ((URI) value).toURL();
            }
            return new URL(convertToStr(value));
        } catch (Exception e) {
            return null;
        }
    }
}
