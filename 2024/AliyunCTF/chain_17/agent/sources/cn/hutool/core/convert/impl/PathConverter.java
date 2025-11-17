package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/PathConverter.class */
public class PathConverter extends AbstractConverter<Path> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Path convertInternal(Object value) {
        try {
            if (value instanceof URI) {
                return Paths.get((URI) value);
            }
            if (value instanceof URL) {
                return Paths.get(((URL) value).toURI());
            }
            if (value instanceof File) {
                return ((File) value).toPath();
            }
            return Paths.get(convertToStr(value), new String[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
