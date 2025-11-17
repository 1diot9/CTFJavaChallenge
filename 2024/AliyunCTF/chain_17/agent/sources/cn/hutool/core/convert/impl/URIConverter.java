package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/URIConverter.class */
public class URIConverter extends AbstractConverter<URI> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public URI convertInternal(Object value) {
        try {
            if (value instanceof File) {
                return ((File) value).toURI();
            }
            if (value instanceof URL) {
                return ((URL) value).toURI();
            }
            return new URI(convertToStr(value));
        } catch (Exception e) {
            return null;
        }
    }
}
