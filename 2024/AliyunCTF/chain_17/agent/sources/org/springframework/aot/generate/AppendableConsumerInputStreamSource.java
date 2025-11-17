package org.springframework.aot.generate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.function.ThrowingConsumer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/AppendableConsumerInputStreamSource.class */
class AppendableConsumerInputStreamSource implements InputStreamSource {
    private final ThrowingConsumer<Appendable> content;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppendableConsumerInputStreamSource(ThrowingConsumer<Appendable> content) {
        this.content = content;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() {
        return new ByteArrayInputStream(toString().getBytes(StandardCharsets.UTF_8));
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        this.content.accept(buffer);
        return buffer.toString();
    }
}
