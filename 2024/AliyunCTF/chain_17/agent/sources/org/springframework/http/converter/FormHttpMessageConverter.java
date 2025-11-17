package org.springframework.http.converter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/FormHttpMessageConverter.class */
public class FormHttpMessageConverter implements HttpMessageConverter<MultiValueMap<String, ?>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);
    private List<MediaType> supportedMediaTypes = new ArrayList();
    private List<HttpMessageConverter<?>> partConverters = new ArrayList();
    private Charset charset = DEFAULT_CHARSET;

    @Nullable
    private Charset multipartCharset;

    public FormHttpMessageConverter() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        this.supportedMediaTypes.add(MediaType.MULTIPART_MIXED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_RELATED);
        this.partConverters.add(new ByteArrayHttpMessageConverter());
        this.partConverters.add(new StringHttpMessageConverter());
        this.partConverters.add(new ResourceHttpMessageConverter());
        applyDefaultCharset();
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        Assert.notNull(supportedMediaTypes, "'supportedMediaTypes' must not be null");
        this.supportedMediaTypes = new ArrayList(supportedMediaTypes);
    }

    public void addSupportedMediaTypes(MediaType... supportedMediaTypes) {
        Assert.notNull(supportedMediaTypes, "'supportedMediaTypes' must not be null");
        Assert.noNullElements(supportedMediaTypes, "'supportedMediaTypes' must not contain null elements");
        Collections.addAll(this.supportedMediaTypes, supportedMediaTypes);
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, "'partConverters' must not be empty");
        this.partConverters = partConverters;
    }

    public List<HttpMessageConverter<?>> getPartConverters() {
        return Collections.unmodifiableList(this.partConverters);
    }

    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be null");
        this.partConverters.add(partConverter);
    }

    public void setCharset(@Nullable Charset charset) {
        if (charset != this.charset) {
            this.charset = charset != null ? charset : DEFAULT_CHARSET;
            applyDefaultCharset();
        }
    }

    private void applyDefaultCharset() {
        for (HttpMessageConverter<?> candidate : this.partConverters) {
            if (candidate instanceof AbstractHttpMessageConverter) {
                AbstractHttpMessageConverter<?> converter = (AbstractHttpMessageConverter) candidate;
                if (converter.getDefaultCharset() != null) {
                    converter.setDefaultCharset(this.charset);
                }
            }
        }
    }

    public void setMultipartCharset(Charset charset) {
        this.multipartCharset = charset;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (!supportedMediaType.getType().equalsIgnoreCase("multipart") && supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    /* renamed from: read, reason: merged with bridge method [inline-methods] */
    public MultiValueMap<String, ?> read2(@Nullable Class<? extends MultiValueMap<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType == null || contentType.getCharset() == null) ? this.charset : contentType.getCharset();
        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);
        String[] pairs = StringUtils.tokenizeToStringArray(body, BeanFactory.FACTORY_BEAN_PREFIX);
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf(61);
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset), null);
            } else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset);
                String value = URLDecoder.decode(pair.substring(idx + 1), charset);
                result.add(name, value);
            }
        }
        return result;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public void write(MultiValueMap<String, ?> map, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (isMultipart(map, contentType)) {
            writeMultipart(map, contentType, outputMessage);
        } else {
            writeForm(map, contentType, outputMessage);
        }
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return contentType.getType().equalsIgnoreCase("multipart");
        }
        Iterator<?> it = map.values().iterator();
        while (it.hasNext()) {
            List<?> values = (List) it.next();
            for (Object value : values) {
                if (value != null && !(value instanceof String)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writeForm(MultiValueMap<String, Object> formData, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        MediaType contentType2 = getFormContentType(contentType);
        outputMessage.getHeaders().setContentType(contentType2);
        Charset charset = contentType2.getCharset();
        Assert.notNull(charset, "No charset");
        final byte[] bytes = serializeForm(formData, charset).getBytes(charset);
        outputMessage.getHeaders().setContentLength(bytes.length);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() { // from class: org.springframework.http.converter.FormHttpMessageConverter.1
                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public void writeTo(OutputStream outputStream) throws IOException {
                    StreamUtils.copy(bytes, outputStream);
                }

                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public boolean repeatable() {
                    return true;
                }
            });
        } else {
            StreamUtils.copy(bytes, outputMessage.getBody());
        }
    }

    protected MediaType getFormContentType(@Nullable MediaType contentType) {
        if (contentType == null) {
            return DEFAULT_FORM_DATA_MEDIA_TYPE;
        }
        if (contentType.getCharset() == null) {
            return new MediaType(contentType, this.charset);
        }
        return contentType;
    }

    protected String serializeForm(MultiValueMap<String, Object> formData, Charset charset) {
        StringBuilder builder = new StringBuilder();
        formData.forEach((name, values) -> {
            if (name == null) {
                Assert.isTrue(CollectionUtils.isEmpty(values), (Supplier<String>) () -> {
                    return "Null name in form data: " + formData;
                });
            } else {
                values.forEach(value -> {
                    if (builder.length() != 0) {
                        builder.append('&');
                    }
                    builder.append(URLEncoder.encode(name, charset));
                    if (value != null) {
                        builder.append('=');
                        builder.append(URLEncoder.encode(String.valueOf(value), charset));
                    }
                });
            }
        });
        return builder.toString();
    }

    private void writeMultipart(MultiValueMap<String, Object> parts, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        if (contentType == null) {
            contentType = MediaType.MULTIPART_FORM_DATA;
        }
        Map<String, String> parameters = new LinkedHashMap<>(contentType.getParameters().size() + 2);
        parameters.putAll(contentType.getParameters());
        byte[] boundary = generateMultipartBoundary();
        if (!isFilenameCharsetSet() && !this.charset.equals(StandardCharsets.UTF_8) && !this.charset.equals(StandardCharsets.US_ASCII)) {
            parameters.put(BasicAuthenticator.charsetparam, this.charset.name());
        }
        parameters.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        outputMessage.getHeaders().setContentType(new MediaType(contentType, parameters));
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(outputStream -> {
                writeParts(outputStream, parts, boundary);
                writeEnd(outputStream, boundary);
            });
        } else {
            writeParts(outputMessage.getBody(), parts, boundary);
            writeEnd(outputMessage.getBody(), boundary);
        }
    }

    private boolean isFilenameCharsetSet() {
        return this.multipartCharset != null;
    }

    private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
        for (Map.Entry<String, Object> entry : parts.entrySet()) {
            String name = entry.getKey();
            for (Object part : (List) entry.getValue()) {
                if (part != null) {
                    writeBoundary(os, boundary);
                    writePart(name, getHttpEntity(part), os);
                    writeNewLine(os);
                }
            }
        }
    }

    private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
        Object partBody = partEntity.getBody();
        if (partBody == null) {
            throw new IllegalStateException("Empty body for part '" + name + "': " + partEntity);
        }
        Class<?> partType = partBody.getClass();
        HttpHeaders partHeaders = partEntity.getHeaders();
        MediaType partContentType = partHeaders.getContentType();
        for (HttpMessageConverter<?> messageConverter : this.partConverters) {
            if (messageConverter.canWrite(partType, partContentType)) {
                Charset charset = isFilenameCharsetSet() ? StandardCharsets.US_ASCII : this.charset;
                HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, charset);
                String filename = getFilename(partBody);
                ContentDisposition.Builder cd = ContentDisposition.formData().name(name);
                if (filename != null) {
                    cd.filename(filename, this.multipartCharset);
                }
                multipartMessage.getHeaders().setContentDisposition(cd.build());
                if (!partHeaders.isEmpty()) {
                    multipartMessage.getHeaders().putAll(partHeaders);
                }
                messageConverter.write(partBody, partContentType, multipartMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter found for request type [" + partType.getName() + "]");
    }

    protected byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    protected HttpEntity<?> getHttpEntity(Object part) {
        if (!(part instanceof HttpEntity)) {
            return new HttpEntity<>(part);
        }
        HttpEntity<?> httpEntity = (HttpEntity) part;
        return httpEntity;
    }

    @Nullable
    protected String getFilename(Object part) {
        if (part instanceof Resource) {
            Resource resource = (Resource) part;
            return resource.getFilename();
        }
        return null;
    }

    private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        writeNewLine(os);
    }

    private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        os.write(45);
        os.write(45);
        writeNewLine(os);
    }

    private static void writeNewLine(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/FormHttpMessageConverter$MultipartHttpOutputMessage.class */
    public static class MultipartHttpOutputMessage implements HttpOutputMessage {
        private final OutputStream outputStream;
        private final Charset charset;
        private final HttpHeaders headers = new HttpHeaders();
        private boolean headersWritten = false;

        public MultipartHttpOutputMessage(OutputStream outputStream, Charset charset) {
            this.outputStream = new MultipartOutputStream(outputStream);
            this.charset = charset;
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        @Override // org.springframework.http.HttpOutputMessage
        public OutputStream getBody() throws IOException {
            writeHeaders();
            return this.outputStream;
        }

        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    byte[] headerName = getBytes(entry.getKey());
                    for (String headerValueString : entry.getValue()) {
                        byte[] headerValue = getBytes(headerValueString);
                        this.outputStream.write(headerName);
                        this.outputStream.write(58);
                        this.outputStream.write(32);
                        this.outputStream.write(headerValue);
                        FormHttpMessageConverter.writeNewLine(this.outputStream);
                    }
                }
                FormHttpMessageConverter.writeNewLine(this.outputStream);
                this.headersWritten = true;
            }
        }

        private byte[] getBytes(String name) {
            return name.getBytes(this.charset);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/FormHttpMessageConverter$MultipartOutputStream.class */
    private static class MultipartOutputStream extends FilterOutputStream {
        public MultipartOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream
        public void write(byte[] b, int off, int let) throws IOException {
            this.out.write(b, off, let);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() {
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }
}
