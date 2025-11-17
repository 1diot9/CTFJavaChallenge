package org.springframework.aot.generate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.aot.generate.GeneratedFiles;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/FileSystemGeneratedFiles.class */
public class FileSystemGeneratedFiles implements GeneratedFiles {
    private final Function<GeneratedFiles.Kind, Path> roots;

    public FileSystemGeneratedFiles(Path root) {
        this(conventionRoots(root));
    }

    public FileSystemGeneratedFiles(Function<GeneratedFiles.Kind, Path> roots) {
        Assert.notNull(roots, "'roots' must not be null");
        Assert.isTrue(Arrays.stream(GeneratedFiles.Kind.values()).map(roots).noneMatch((v0) -> {
            return Objects.isNull(v0);
        }), "'roots' must return a value for all file kinds");
        this.roots = roots;
    }

    private static Function<GeneratedFiles.Kind, Path> conventionRoots(Path root) {
        Assert.notNull(root, "'root' must not be null");
        return kind -> {
            switch (kind) {
                case SOURCE:
                    return root.resolve("sources");
                case RESOURCE:
                    return root.resolve("resources");
                case CLASS:
                    return root.resolve("classes");
                default:
                    throw new IncompatibleClassChangeError();
            }
        };
    }

    @Override // org.springframework.aot.generate.GeneratedFiles
    public void addFile(GeneratedFiles.Kind kind, String path, InputStreamSource content) {
        Assert.notNull(kind, "'kind' must not be null");
        Assert.hasLength(path, "'path' must not be empty");
        Assert.notNull(content, "'content' must not be null");
        Path root = this.roots.apply(kind).toAbsolutePath().normalize();
        Path relativePath = root.resolve(path).toAbsolutePath().normalize();
        Assert.isTrue(relativePath.startsWith(root), "'path' must be relative");
        try {
            InputStream inputStream = content.getInputStream();
            try {
                Files.createDirectories(relativePath.getParent(), new FileAttribute[0]);
                Files.copy(inputStream, relativePath, new CopyOption[0]);
                if (inputStream != null) {
                    inputStream.close();
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
