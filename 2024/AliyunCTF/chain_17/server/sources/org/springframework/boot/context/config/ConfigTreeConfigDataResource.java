package org.springframework.boot.context.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigTreeConfigDataResource.class */
public class ConfigTreeConfigDataResource extends ConfigDataResource {
    private final Path path;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigTreeConfigDataResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = Paths.get(path, new String[0]).toAbsolutePath();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigTreeConfigDataResource(Path path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path.toAbsolutePath();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Path getPath() {
        return this.path;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConfigTreeConfigDataResource other = (ConfigTreeConfigDataResource) obj;
        return Objects.equals(this.path, other.path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return "config tree [" + this.path + "]";
    }
}
