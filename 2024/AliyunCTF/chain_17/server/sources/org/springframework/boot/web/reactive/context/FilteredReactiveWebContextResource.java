package org.springframework.boot.web.reactive.context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/FilteredReactiveWebContextResource.class */
class FilteredReactiveWebContextResource extends AbstractResource {
    private final String path;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilteredReactiveWebContextResource(String path) {
        this.path = path;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return false;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws IOException {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new FilteredReactiveWebContextResource(pathToUse);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "ReactiveWebContext resource [" + this.path + "]";
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
    }
}
