package org.springframework.boot.convert;

import java.io.File;
import java.io.IOException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/StringToFileConverter.class */
public class StringToFileConverter implements Converter<String, File> {
    private static final ResourceLoader resourceLoader = new DefaultResourceLoader(null);

    @Override // org.springframework.core.convert.converter.Converter
    public File convert(String source) {
        if (ResourceUtils.isUrl(source)) {
            return getFile(resourceLoader.getResource(source));
        }
        File file = new File(source);
        if (file.exists()) {
            return file;
        }
        Resource resource = resourceLoader.getResource(source);
        if (resource.exists()) {
            return getFile(resource);
        }
        return file;
    }

    private File getFile(Resource resource) {
        try {
            return resource.getFile();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not retrieve file for " + resource + ": " + ex.getMessage());
        }
    }
}
