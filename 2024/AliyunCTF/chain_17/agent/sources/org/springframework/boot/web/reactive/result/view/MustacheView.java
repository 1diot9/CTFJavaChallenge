package org.springframework.boot.web.reactive.result.view;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/result/view/MustacheView.class */
public class MustacheView extends AbstractUrlBasedView {
    private Mustache.Compiler compiler;
    private String charset;

    public void setCompiler(Mustache.Compiler compiler) {
        this.compiler = compiler;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean checkResourceExists(Locale locale) throws Exception {
        return resolveResource() != null;
    }

    protected Mono<Void> renderInternal(Map<String, Object> model, MediaType contentType, ServerWebExchange exchange) {
        Resource resource = resolveResource();
        if (resource == null) {
            return Mono.error(new IllegalStateException("Could not find Mustache template with URL [" + getUrl() + "]"));
        }
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().allocateBuffer(256);
        try {
            Reader reader = getReader(resource);
            try {
                Template template = this.compiler.compile(reader);
                Charset charset = getCharset(contentType).orElseGet(this::getDefaultCharset);
                Writer writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);
                try {
                    template.execute(model, writer);
                    writer.flush();
                    writer.close();
                    if (reader != null) {
                        reader.close();
                    }
                    return exchange.getResponse().writeWith(Flux.just(dataBuffer));
                } catch (Throwable th) {
                    try {
                        writer.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (Exception ex) {
            DataBufferUtils.release(dataBuffer);
            return Mono.error(ex);
        }
    }

    private Resource resolveResource() {
        Resource resource = getApplicationContext().getResource(getUrl());
        if (resource == null || !resource.exists()) {
            return null;
        }
        return resource;
    }

    private Reader getReader(Resource resource) throws IOException {
        if (this.charset != null) {
            return new InputStreamReader(resource.getInputStream(), this.charset);
        }
        return new InputStreamReader(resource.getInputStream());
    }

    private Optional<Charset> getCharset(MediaType mediaType) {
        return Optional.ofNullable(mediaType != null ? mediaType.getCharset() : null);
    }
}
