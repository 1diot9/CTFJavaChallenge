package org.springframework.web.servlet.resource;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.resource.AbstractVersionStrategy;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ContentVersionStrategy.class */
public class ContentVersionStrategy extends AbstractVersionStrategy {
    public ContentVersionStrategy() {
        super(new AbstractVersionStrategy.FileNameVersionPathStrategy());
    }

    @Override // org.springframework.web.servlet.resource.VersionStrategy
    public String getResourceVersion(Resource resource) {
        try {
            byte[] content = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return DigestUtils.md5DigestAsHex(content);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to calculate hash for " + resource, ex);
        }
    }
}
