package org.springframework.aot.nativex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/FileNativeConfigurationWriter.class */
public class FileNativeConfigurationWriter extends NativeConfigurationWriter {
    private final Path basePath;

    @Nullable
    private final String groupId;

    @Nullable
    private final String artifactId;

    public FileNativeConfigurationWriter(Path basePath) {
        this(basePath, null, null);
    }

    public FileNativeConfigurationWriter(Path basePath, @Nullable String groupId, @Nullable String artifactId) {
        this.basePath = basePath;
        if ((groupId == null && artifactId != null) || (groupId != null && artifactId == null)) {
            throw new IllegalArgumentException("groupId and artifactId must be both null or both non-null");
        }
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    @Override // org.springframework.aot.nativex.NativeConfigurationWriter
    protected void writeTo(String fileName, Consumer<BasicJsonWriter> writer) {
        try {
            File file = createIfNecessary(fileName);
            FileWriter out = new FileWriter(file);
            try {
                writer.accept(createJsonWriter(out));
                out.close();
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write native configuration for " + fileName, ex);
        }
    }

    private File createIfNecessary(String filename) throws IOException {
        Path outputDirectory = this.basePath.resolve("META-INF").resolve("native-image");
        if (this.groupId != null && this.artifactId != null) {
            outputDirectory = outputDirectory.resolve(this.groupId).resolve(this.artifactId);
        }
        outputDirectory.toFile().mkdirs();
        File file = outputDirectory.resolve(filename).toFile();
        file.createNewFile();
        return file;
    }

    private BasicJsonWriter createJsonWriter(Writer out) {
        return new BasicJsonWriter(out);
    }
}
