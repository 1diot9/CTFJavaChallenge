package org.springframework.boot.convert;

import java.io.IOException;
import org.springframework.boot.origin.Origin;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/InputStreamSourceToByteArrayConverter.class */
public class InputStreamSourceToByteArrayConverter implements Converter<InputStreamSource, byte[]> {
    @Override // org.springframework.core.convert.converter.Converter
    public byte[] convert(InputStreamSource source) {
        try {
            return FileCopyUtils.copyToByteArray(source.getInputStream());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read from " + getName(source), ex);
        }
    }

    private String getName(InputStreamSource source) {
        Origin origin = Origin.from(source);
        if (origin != null) {
            return origin.toString();
        }
        if (source instanceof Resource) {
            Resource resource = (Resource) source;
            return resource.getDescription();
        }
        return "input stream source";
    }
}
