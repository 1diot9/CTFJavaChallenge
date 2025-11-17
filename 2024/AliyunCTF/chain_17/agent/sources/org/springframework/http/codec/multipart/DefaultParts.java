package org.springframework.http.codec.multipart;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts.class */
public abstract class DefaultParts {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$Content.class */
    public interface Content {
        Flux<DataBuffer> content();

        Mono<Void> transferTo(Path dest);

        Mono<Void> delete();
    }

    DefaultParts() {
    }

    public static FormFieldPart formFieldPart(HttpHeaders headers, String value) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(value, "Value must not be null");
        return new DefaultFormFieldPart(headers, value);
    }

    public static Part part(HttpHeaders headers, Flux<DataBuffer> dataBuffers) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(dataBuffers, "DataBuffers must not be null");
        return partInternal(headers, new FluxContent(dataBuffers));
    }

    public static Part part(HttpHeaders headers, Path file, Scheduler scheduler) {
        Assert.notNull(headers, "Headers must not be null");
        Assert.notNull(file, "File must not be null");
        Assert.notNull(scheduler, "Scheduler must not be null");
        return partInternal(headers, new FileContent(file, scheduler));
    }

    private static Part partInternal(HttpHeaders headers, Content content) {
        String filename = headers.getContentDisposition().getFilename();
        if (filename != null) {
            return new DefaultFilePart(headers, content);
        }
        return new DefaultPart(headers, content);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$AbstractPart.class */
    private static abstract class AbstractPart implements Part {
        private final HttpHeaders headers;

        protected AbstractPart(HttpHeaders headers) {
            Assert.notNull(headers, "HttpHeaders is required");
            this.headers = headers;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public String name() {
            String name = headers().getContentDisposition().getName();
            Assert.state(name != null, "No name available");
            return name;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public HttpHeaders headers() {
            return this.headers;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$DefaultFormFieldPart.class */
    public static class DefaultFormFieldPart extends AbstractPart implements FormFieldPart {
        private final String value;

        public DefaultFormFieldPart(HttpHeaders headers, String value) {
            super(headers);
            this.value = value;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public Flux<DataBuffer> content() {
            return Flux.defer(() -> {
                byte[] bytes = this.value.getBytes(MultipartUtils.charset(headers()));
                return Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
            });
        }

        @Override // org.springframework.http.codec.multipart.FormFieldPart
        public String value() {
            return this.value;
        }

        public String toString() {
            String name = headers().getContentDisposition().getName();
            if (name != null) {
                return "DefaultFormFieldPart{" + name() + "}";
            }
            return "DefaultFormFieldPart";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$DefaultPart.class */
    public static class DefaultPart extends AbstractPart {
        protected final Content content;

        public DefaultPart(HttpHeaders headers, Content content) {
            super(headers);
            this.content = content;
        }

        @Override // org.springframework.http.codec.multipart.Part
        public Flux<DataBuffer> content() {
            return this.content.content();
        }

        @Override // org.springframework.http.codec.multipart.Part
        public Mono<Void> delete() {
            return this.content.delete();
        }

        public String toString() {
            String name = headers().getContentDisposition().getName();
            if (name != null) {
                return "DefaultPart{" + name + "}";
            }
            return "DefaultPart";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$DefaultFilePart.class */
    public static final class DefaultFilePart extends DefaultPart implements FilePart {
        public DefaultFilePart(HttpHeaders headers, Content content) {
            super(headers, content);
        }

        @Override // org.springframework.http.codec.multipart.FilePart
        public String filename() {
            String filename = headers().getContentDisposition().getFilename();
            Assert.state(filename != null, "No filename found");
            return filename;
        }

        @Override // org.springframework.http.codec.multipart.FilePart
        public Mono<Void> transferTo(Path dest) {
            return this.content.transferTo(dest);
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.DefaultPart
        public String toString() {
            ContentDisposition contentDisposition = headers().getContentDisposition();
            String name = contentDisposition.getName();
            String filename = contentDisposition.getFilename();
            if (name != null) {
                return "DefaultFilePart{" + name + " (" + filename + ")}";
            }
            return "DefaultFilePart{(" + filename + ")}";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$FluxContent.class */
    public static final class FluxContent implements Content {
        private final Flux<DataBuffer> content;

        public FluxContent(Flux<DataBuffer> content) {
            this.content = content;
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Flux<DataBuffer> content() {
            return this.content;
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Mono<Void> transferTo(Path dest) {
            return DataBufferUtils.write((Publisher<DataBuffer>) this.content, dest, new OpenOption[0]);
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Mono<Void> delete() {
            return Mono.empty();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/DefaultParts$FileContent.class */
    public static final class FileContent implements Content {
        private final Path file;
        private final Scheduler scheduler;

        public FileContent(Path file, Scheduler scheduler) {
            this.file = file;
            this.scheduler = scheduler;
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Flux<DataBuffer> content() {
            return DataBufferUtils.readByteChannel(() -> {
                return Files.newByteChannel(this.file, StandardOpenOption.READ);
            }, DefaultDataBufferFactory.sharedInstance, 1024).subscribeOn(this.scheduler);
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Mono<Void> transferTo(Path dest) {
            return blockingOperation(() -> {
                return Files.copy(this.file, dest, StandardCopyOption.REPLACE_EXISTING);
            });
        }

        @Override // org.springframework.http.codec.multipart.DefaultParts.Content
        public Mono<Void> delete() {
            return blockingOperation(() -> {
                Files.delete(this.file);
                return null;
            });
        }

        private Mono<Void> blockingOperation(Callable<?> callable) {
            return Mono.create(sink -> {
                try {
                    callable.call();
                    sink.success();
                } catch (Exception ex) {
                    sink.error(ex);
                }
            }).subscribeOn(this.scheduler);
        }
    }
}
