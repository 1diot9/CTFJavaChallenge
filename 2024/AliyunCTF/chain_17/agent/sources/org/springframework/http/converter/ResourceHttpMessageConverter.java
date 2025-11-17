package org.springframework.http.converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/ResourceHttpMessageConverter.class */
public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Resource> {
    private final boolean supportsReadStreaming;

    public ResourceHttpMessageConverter() {
        super(MediaType.ALL);
        this.supportsReadStreaming = true;
    }

    public ResourceHttpMessageConverter(boolean supportsReadStreaming) {
        super(MediaType.ALL);
        this.supportsReadStreaming = supportsReadStreaming;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Resource readInternal(Class<? extends Resource> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (this.supportsReadStreaming && InputStreamResource.class == clazz) {
            return new InputStreamResource(inputMessage.getBody()) { // from class: org.springframework.http.converter.ResourceHttpMessageConverter.1
                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                public String getFilename() {
                    return inputMessage.getHeaders().getContentDisposition().getFilename();
                }

                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                public long contentLength() throws IOException {
                    long length = inputMessage.getHeaders().getContentLength();
                    return length != -1 ? length : super.contentLength();
                }
            };
        }
        if (Resource.class == clazz || ByteArrayResource.class.isAssignableFrom(clazz)) {
            byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
            return new ByteArrayResource(body) { // from class: org.springframework.http.converter.ResourceHttpMessageConverter.2
                @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
                @Nullable
                public String getFilename() {
                    return inputMessage.getHeaders().getContentDisposition().getFilename();
                }
            };
        }
        throw new HttpMessageNotReadableException("Unsupported resource class: " + clazz, inputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public MediaType getDefaultContentType(Resource resource) {
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(Resource resource, @Nullable MediaType contentType) throws IOException {
        if (InputStreamResource.class == resource.getClass()) {
            return null;
        }
        long contentLength = resource.contentLength();
        if (contentLength < 0) {
            return null;
        }
        return Long.valueOf(contentLength);
    }

    public void addDefaultHeaders(HttpOutputMessage message, Resource resource, @Nullable MediaType contentType) throws IOException {
        addDefaultHeaders(message.getHeaders(), (HttpHeaders) resource, contentType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeContent(resource, outputMessage);
    }

    protected void writeContent(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            InputStream in = resource.getInputStream();
            try {
                OutputStream out = outputMessage.getBody();
                in.transferTo(out);
                out.flush();
                try {
                    in.close();
                } catch (Throwable th) {
                }
            } catch (NullPointerException e) {
                try {
                    in.close();
                } catch (Throwable th2) {
                }
            } catch (Throwable th3) {
                try {
                    in.close();
                } catch (Throwable th4) {
                }
                throw th3;
            }
        } catch (FileNotFoundException e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Resource resource) {
        return !(resource instanceof InputStreamResource);
    }
}
