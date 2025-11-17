package org.springframework.boot.context.config;

import java.io.File;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/StandardConfigDataResource.class */
public class StandardConfigDataResource extends ConfigDataResource {
    private final StandardConfigDataReference reference;
    private final Resource resource;
    private final boolean emptyDirectory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardConfigDataResource(StandardConfigDataReference reference, Resource resource) {
        this(reference, resource, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardConfigDataResource(StandardConfigDataReference reference, Resource resource, boolean emptyDirectory) {
        Assert.notNull(reference, "Reference must not be null");
        Assert.notNull(resource, "Resource must not be null");
        this.reference = reference;
        this.resource = resource;
        this.emptyDirectory = emptyDirectory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardConfigDataReference getReference() {
        return this.reference;
    }

    public Resource getResource() {
        return this.resource;
    }

    public String getProfile() {
        return this.reference.getProfile();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmptyDirectory() {
        return this.emptyDirectory;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StandardConfigDataResource other = (StandardConfigDataResource) obj;
        return this.emptyDirectory == other.emptyDirectory && isSameUnderlyingResource(this.resource, other.resource);
    }

    private boolean isSameUnderlyingResource(Resource ours, Resource other) {
        return ours.equals(other) || isSameFile(getUnderlyingFile(ours), getUnderlyingFile(other));
    }

    private boolean isSameFile(File ours, File other) {
        return ours != null && ours.equals(other);
    }

    public int hashCode() {
        File underlyingFile = getUnderlyingFile(this.resource);
        return underlyingFile != null ? underlyingFile.hashCode() : this.resource.hashCode();
    }

    public String toString() {
        if ((this.resource instanceof FileSystemResource) || (this.resource instanceof FileUrlResource)) {
            try {
                return "file [" + this.resource.getFile() + "]";
            } catch (IOException e) {
            }
        }
        return this.resource.toString();
    }

    private File getUnderlyingFile(Resource resource) {
        try {
            if ((resource instanceof ClassPathResource) || (resource instanceof FileSystemResource) || (resource instanceof FileUrlResource)) {
                return resource.getFile().getAbsoluteFile();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
