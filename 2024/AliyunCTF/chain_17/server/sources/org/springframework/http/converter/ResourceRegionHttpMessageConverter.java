package org.springframework.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/ResourceRegionHttpMessageConverter.class */
public class ResourceRegionHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected /* bridge */ /* synthetic */ Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readInternal((Class<?>) clazz, inputMessage);
    }

    public ResourceRegionHttpMessageConverter() {
        super(MediaType.ALL);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public MediaType getDefaultContentType(Object object) {
        Resource resource = null;
        if (object instanceof ResourceRegion) {
            ResourceRegion resourceRegion = (ResourceRegion) object;
            resource = resourceRegion.getResource();
        } else {
            Collection<ResourceRegion> regions = (Collection) object;
            if (!regions.isEmpty()) {
                resource = regions.iterator().next().getResource();
            }
        }
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected ResourceRegion readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(clazz, null, mediaType);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            if (type instanceof Class) {
                Class<?> c = (Class) type;
                if (ResourceRegion.class.isAssignableFrom(c)) {
                    return true;
                }
            }
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            return false;
        }
        Class<?> rawType2 = (Class) rawType;
        if (!Collection.class.isAssignableFrom(rawType2) || parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class<?> typeArgumentClass = (Class) typeArgument;
        return ResourceRegion.class.isAssignableFrom(typeArgumentClass);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof ResourceRegion) {
            ResourceRegion resourceRegion = (ResourceRegion) object;
            writeResourceRegion(resourceRegion, outputMessage);
            return;
        }
        Collection<ResourceRegion> regions = (Collection) object;
        if (regions.size() == 1) {
            writeResourceRegion(regions.iterator().next(), outputMessage);
        } else {
            writeResourceRegionCollection(regions, outputMessage);
        }
    }

    protected void writeResourceRegion(ResourceRegion region, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(region, "ResourceRegion must not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        long start = region.getPosition();
        long end = (start + region.getCount()) - 1;
        long resourceLength = region.getResource().contentLength();
        long end2 = Math.min(end, resourceLength - 1);
        long rangeLength = (end2 - start) + 1;
        responseHeaders.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + responseHeaders + "/" + end2);
        responseHeaders.setContentLength(rangeLength);
        InputStream in = region.getResource().getInputStream();
        try {
            StreamUtils.copyRange(in, outputMessage.getBody(), start, end2);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    private void writeResourceRegionCollection(Collection<ResourceRegion> resourceRegions, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(resourceRegions, "Collection of ResourceRegion should not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        MediaType contentType = responseHeaders.getContentType();
        String boundaryString = MimeTypeUtils.generateMultipartBoundaryString();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "multipart/byteranges; boundary=" + boundaryString);
        OutputStream out = outputMessage.getBody();
        Resource resource = null;
        InputStream in = null;
        long inputStreamPosition = 0;
        try {
            for (ResourceRegion region : resourceRegions) {
                long start = region.getPosition() - inputStreamPosition;
                if (start < 0 || resource != region.getResource()) {
                    if (in != null) {
                        in.close();
                    }
                    resource = region.getResource();
                    in = resource.getInputStream();
                    inputStreamPosition = 0;
                    start = region.getPosition();
                }
                long end = (start + region.getCount()) - 1;
                println(out);
                print(out, "--" + boundaryString);
                println(out);
                if (contentType != null) {
                    print(out, "Content-Type: " + contentType);
                    println(out);
                }
                long resourceLength = region.getResource().contentLength();
                long end2 = Math.min(end, (resourceLength - inputStreamPosition) - 1);
                print(out, "Content-Range: bytes " + region.getPosition() + "-" + out + "/" + ((region.getPosition() + region.getCount()) - 1));
                println(out);
                println(out);
                StreamUtils.copyRange(in, out, start, end2);
                inputStreamPosition += end2 + 1;
            }
            println(out);
            print(out, "--" + boundaryString + "--");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void println(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    private static void print(OutputStream os, String buf) throws IOException {
        os.write(buf.getBytes(StandardCharsets.US_ASCII));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object object) {
        if (object instanceof ResourceRegion) {
            ResourceRegion resourceRegion = (ResourceRegion) object;
            return supportsRepeatableWrites(resourceRegion);
        }
        Collection<ResourceRegion> regions = (Collection) object;
        for (ResourceRegion region : regions) {
            if (!supportsRepeatableWrites(region)) {
                return false;
            }
        }
        return true;
    }

    private boolean supportsRepeatableWrites(ResourceRegion region) {
        return !(region.getResource() instanceof InputStreamResource);
    }
}
