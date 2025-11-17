package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FilePartEvent.class */
public interface FilePartEvent extends PartEvent {
    default String filename() {
        String filename = headers().getContentDisposition().getFilename();
        Assert.state(filename != null, "No filename found");
        return filename;
    }

    static Flux<FilePartEvent> create(String name, Resource resource) {
        return create(name, resource, (Consumer<HttpHeaders>) null);
    }

    static Flux<FilePartEvent> create(String name, Resource resource, @Nullable Consumer<HttpHeaders> headersConsumer) {
        try {
            return create(name, resource.getFile().toPath(), headersConsumer);
        } catch (IOException ex) {
            return Flux.error(ex);
        }
    }

    static Flux<FilePartEvent> create(String name, Path path) {
        return create(name, path, (Consumer<HttpHeaders>) null);
    }

    static Flux<FilePartEvent> create(String name, Path path, @Nullable Consumer<HttpHeaders> headersConsumer) {
        Assert.hasLength(name, "Name must not be empty");
        Assert.notNull(path, "Path must not be null");
        return Flux.defer(() -> {
            String pathName = StringUtils.cleanPath(path.toString());
            MediaType contentType = MediaTypeFactory.getMediaType(pathName).orElse(MediaType.APPLICATION_OCTET_STREAM);
            String filename = StringUtils.getFilename(pathName);
            if (filename == null) {
                return Flux.error(new IllegalArgumentException("Invalid file: " + pathName));
            }
            Flux<DataBuffer> contents = DataBufferUtils.read(path, DefaultDataBufferFactory.sharedInstance, 8192, new OpenOption[0]);
            return create(name, filename, contentType, contents, headersConsumer);
        });
    }

    static Flux<FilePartEvent> create(String partName, String filename, MediaType contentType, Flux<DataBuffer> contents) {
        return create(partName, filename, contentType, contents, null);
    }

    static Flux<FilePartEvent> create(String partName, String filename, MediaType contentType, Flux<DataBuffer> contents, @Nullable Consumer<HttpHeaders> headersConsumer) {
        Assert.hasLength(partName, "PartName must not be empty");
        Assert.hasLength(filename, "Filename must not be empty");
        Assert.notNull(contentType, "ContentType must not be null");
        Assert.notNull(contents, "Contents must not be null");
        return Flux.defer(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setContentDisposition(ContentDisposition.formData().name(partName).filename(filename, StandardCharsets.UTF_8).build());
            if (headersConsumer != null) {
                headersConsumer.accept(headers);
            }
            return contents.map(content -> {
                return DefaultPartEvents.file(headers, content, false);
            }).concatWith(Mono.just(DefaultPartEvents.file(headers)));
        });
    }
}
