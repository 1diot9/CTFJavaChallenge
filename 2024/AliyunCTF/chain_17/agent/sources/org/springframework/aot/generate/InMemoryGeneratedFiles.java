package org.springframework.aot.generate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.aot.generate.GeneratedFiles;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/InMemoryGeneratedFiles.class */
public class InMemoryGeneratedFiles implements GeneratedFiles {
    private final Map<GeneratedFiles.Kind, Map<String, InputStreamSource>> files = new HashMap();

    @Override // org.springframework.aot.generate.GeneratedFiles
    public void addFile(GeneratedFiles.Kind kind, String path, InputStreamSource content) {
        Assert.notNull(kind, "'kind' must not be null");
        Assert.hasLength(path, "'path' must not be empty");
        Assert.notNull(content, "'content' must not be null");
        Map<String, InputStreamSource> paths = this.files.computeIfAbsent(kind, key -> {
            return new LinkedHashMap();
        });
        Assert.state(!paths.containsKey(path), (Supplier<String>) () -> {
            return "Path '" + path + "' already in use";
        });
        paths.put(path, content);
    }

    public Map<String, InputStreamSource> getGeneratedFiles(GeneratedFiles.Kind kind) {
        Assert.notNull(kind, "'kind' must not be null");
        return Collections.unmodifiableMap(this.files.getOrDefault(kind, Collections.emptyMap()));
    }

    @Nullable
    public String getGeneratedFileContent(GeneratedFiles.Kind kind, String path) throws IOException {
        InputStreamSource source = getGeneratedFile(kind, path);
        if (source != null) {
            return new String(source.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
        return null;
    }

    @Nullable
    public InputStreamSource getGeneratedFile(GeneratedFiles.Kind kind, String path) {
        Assert.notNull(kind, "'kind' must not be null");
        Assert.hasLength(path, "'path' must not be empty");
        Map<String, InputStreamSource> paths = this.files.get(kind);
        if (paths != null) {
            return paths.get(path);
        }
        return null;
    }
}
