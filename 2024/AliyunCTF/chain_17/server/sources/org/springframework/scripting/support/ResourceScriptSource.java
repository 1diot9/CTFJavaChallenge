package org.springframework.scripting.support;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/support/ResourceScriptSource.class */
public class ResourceScriptSource implements ScriptSource {
    private EncodedResource resource;
    protected final Log logger = LogFactory.getLog(getClass());
    private long lastModified = -1;
    private final Object lastModifiedMonitor = new Object();

    public ResourceScriptSource(EncodedResource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }

    public ResourceScriptSource(Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = new EncodedResource(resource, StandardCharsets.UTF_8);
    }

    public final Resource getResource() {
        return this.resource.getResource();
    }

    public void setEncoding(@Nullable String encoding) {
        this.resource = new EncodedResource(this.resource.getResource(), encoding);
    }

    @Override // org.springframework.scripting.ScriptSource
    public String getScriptAsString() throws IOException {
        synchronized (this.lastModifiedMonitor) {
            this.lastModified = retrieveLastModifiedTime();
        }
        Reader reader = this.resource.getReader();
        return FileCopyUtils.copyToString(reader);
    }

    @Override // org.springframework.scripting.ScriptSource
    public boolean isModified() {
        boolean z;
        synchronized (this.lastModifiedMonitor) {
            z = this.lastModified < 0 || retrieveLastModifiedTime() > this.lastModified;
        }
        return z;
    }

    protected long retrieveLastModifiedTime() {
        try {
            return getResource().lastModified();
        } catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(getResource() + " could not be resolved in the file system - current timestamp not available for script modification check", ex);
                return 0L;
            }
            return 0L;
        }
    }

    @Override // org.springframework.scripting.ScriptSource
    @Nullable
    public String suggestedClassName() {
        String filename = getResource().getFilename();
        if (filename != null) {
            return StringUtils.stripFilenameExtension(filename);
        }
        return null;
    }

    public String toString() {
        return this.resource.toString();
    }
}
