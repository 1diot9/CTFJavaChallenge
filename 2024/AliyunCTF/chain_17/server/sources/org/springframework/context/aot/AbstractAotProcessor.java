package org.springframework.context.aot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;
import org.springframework.aot.generate.FileSystemGeneratedFiles;
import org.springframework.aot.generate.GeneratedFiles;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.nativex.FileNativeConfigurationWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/AbstractAotProcessor.class */
public abstract class AbstractAotProcessor<T> {
    private static final String AOT_PROCESSING = "spring.aot.processing";
    private final Settings settings;

    protected abstract T doProcess();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAotProcessor(Settings settings) {
        this.settings = settings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Settings getSettings() {
        return this.settings;
    }

    public final T process() {
        try {
            System.setProperty(AOT_PROCESSING, "true");
            T doProcess = doProcess();
            System.clearProperty(AOT_PROCESSING);
            return doProcess;
        } catch (Throwable th) {
            System.clearProperty(AOT_PROCESSING);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void deleteExistingOutput() {
        deleteExistingOutput(getSettings().getSourceOutput(), getSettings().getResourceOutput(), getSettings().getClassOutput());
    }

    private void deleteExistingOutput(Path... paths) {
        for (Path path : paths) {
            try {
                FileSystemUtils.deleteRecursively(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete existing output in '" + path + "'");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileSystemGeneratedFiles createFileSystemGeneratedFiles() {
        return new FileSystemGeneratedFiles((Function<GeneratedFiles.Kind, Path>) this::getRoot);
    }

    private Path getRoot(GeneratedFiles.Kind kind) {
        switch (kind) {
            case SOURCE:
                return getSettings().getSourceOutput();
            case RESOURCE:
                return getSettings().getResourceOutput();
            case CLASS:
                return getSettings().getClassOutput();
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeHints(RuntimeHints hints) {
        FileNativeConfigurationWriter writer = new FileNativeConfigurationWriter(getSettings().getResourceOutput(), getSettings().getGroupId(), getSettings().getArtifactId());
        writer.write(hints);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/AbstractAotProcessor$Settings.class */
    public static final class Settings {
        private final Path sourceOutput;
        private final Path resourceOutput;
        private final Path classOutput;
        private final String groupId;
        private final String artifactId;

        private Settings(Path sourceOutput, Path resourceOutput, Path classOutput, String groupId, String artifactId) {
            this.sourceOutput = sourceOutput;
            this.resourceOutput = resourceOutput;
            this.classOutput = classOutput;
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Path getSourceOutput() {
            return this.sourceOutput;
        }

        public Path getResourceOutput() {
            return this.resourceOutput;
        }

        public Path getClassOutput() {
            return this.classOutput;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public String getArtifactId() {
            return this.artifactId;
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/aot/AbstractAotProcessor$Settings$Builder.class */
        public static final class Builder {

            @Nullable
            private Path sourceOutput;

            @Nullable
            private Path resourceOutput;

            @Nullable
            private Path classOutput;

            @Nullable
            private String groupId;

            @Nullable
            private String artifactId;

            private Builder() {
            }

            public Builder sourceOutput(Path sourceOutput) {
                this.sourceOutput = sourceOutput;
                return this;
            }

            public Builder resourceOutput(Path resourceOutput) {
                this.resourceOutput = resourceOutput;
                return this;
            }

            public Builder classOutput(Path classOutput) {
                this.classOutput = classOutput;
                return this;
            }

            public Builder groupId(String groupId) {
                this.groupId = groupId;
                return this;
            }

            public Builder artifactId(String artifactId) {
                this.artifactId = artifactId;
                return this;
            }

            public Settings build() {
                Assert.notNull(this.sourceOutput, "'sourceOutput' must not be null");
                Assert.notNull(this.resourceOutput, "'resourceOutput' must not be null");
                Assert.notNull(this.classOutput, "'classOutput' must not be null");
                Assert.hasText(this.groupId, "'groupId' must not be null or empty");
                Assert.hasText(this.artifactId, "'artifactId' must not be null or empty");
                return new Settings(this.sourceOutput, this.resourceOutput, this.classOutput, this.groupId, this.artifactId);
            }
        }
    }
}
