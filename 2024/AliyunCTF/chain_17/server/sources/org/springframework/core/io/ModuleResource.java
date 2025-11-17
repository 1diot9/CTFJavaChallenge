package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ModuleResource.class */
public class ModuleResource extends AbstractResource {
    private final Module module;
    private final String path;

    public ModuleResource(Module module, String path) {
        Assert.notNull(module, "Module must not be null");
        Assert.notNull(path, "Path must not be null");
        this.module = module;
        this.path = path;
    }

    public final Module getModule() {
        return this.module;
    }

    public final String getPath() {
        return this.path;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        InputStream is = this.module.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return new ModuleResource(this.module, pathToUse);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "module resource [" + this.path + "]" + (this.module.isNamed() ? " from module [" + this.module.getName() + "]" : "");
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof ModuleResource) {
                ModuleResource that = (ModuleResource) obj;
                if (!this.module.equals(that.module) || !this.path.equals(that.path)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return (this.module.hashCode() * 31) + this.path.hashCode();
    }
}
