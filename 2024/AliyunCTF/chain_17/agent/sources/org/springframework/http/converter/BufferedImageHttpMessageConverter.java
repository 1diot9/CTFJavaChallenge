package org.springframework.http.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/BufferedImageHttpMessageConverter.class */
public class BufferedImageHttpMessageConverter implements HttpMessageConverter<BufferedImage> {
    private final List<MediaType> readableMediaTypes = new ArrayList();

    @Nullable
    private MediaType defaultContentType;

    @Nullable
    private File cacheDir;

    public BufferedImageHttpMessageConverter() {
        String[] readerMediaTypes = ImageIO.getReaderMIMETypes();
        for (String mediaType : readerMediaTypes) {
            if (StringUtils.hasText(mediaType)) {
                this.readableMediaTypes.add(MediaType.parseMediaType(mediaType));
            }
        }
        String[] writerMediaTypes = ImageIO.getWriterMIMETypes();
        for (String mediaType2 : writerMediaTypes) {
            if (StringUtils.hasText(mediaType2)) {
                this.defaultContentType = MediaType.parseMediaType(mediaType2);
                return;
            }
        }
    }

    public void setDefaultContentType(@Nullable MediaType defaultContentType) {
        if (defaultContentType != null) {
            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(defaultContentType.toString());
            if (!imageWriters.hasNext()) {
                throw new IllegalArgumentException("Content-Type [" + defaultContentType + "] is not supported by the Java Image I/O API");
            }
        }
        this.defaultContentType = defaultContentType;
    }

    @Nullable
    public MediaType getDefaultContentType() {
        return this.defaultContentType;
    }

    public void setCacheDir(File cacheDir) {
        Assert.notNull(cacheDir, "'cacheDir' must not be null");
        Assert.isTrue(cacheDir.isDirectory(), (Supplier<String>) () -> {
            return "'cacheDir' is not a directory: " + cacheDir;
        });
        this.cacheDir = cacheDir;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return BufferedImage.class == clazz && isReadable(mediaType);
    }

    private boolean isReadable(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(mediaType.toString());
        return imageReaders.hasNext();
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return BufferedImage.class == clazz && isWritable(mediaType);
    }

    private boolean isWritable(@Nullable MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equalsTypeAndSubtype(mediaType)) {
            return true;
        }
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(mediaType.toString());
        return imageWriters.hasNext();
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.readableMediaTypes);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.HttpMessageConverter
    /* renamed from: read */
    public BufferedImage read2(@Nullable Class<? extends BufferedImage> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ImageInputStream imageInputStream = null;
        ImageReader imageReader = null;
        try {
            ImageInputStream imageInputStream2 = createImageInputStream(inputMessage.getBody());
            MediaType contentType = inputMessage.getHeaders().getContentType();
            if (contentType == null) {
                throw new HttpMessageNotReadableException("No Content-Type header", inputMessage);
            }
            Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(contentType.toString());
            if (imageReaders.hasNext()) {
                ImageReader imageReader2 = imageReaders.next();
                ImageReadParam irp = imageReader2.getDefaultReadParam();
                process(irp);
                imageReader2.setInput(imageInputStream2, true);
                BufferedImage read = imageReader2.read(0, irp);
                if (imageReader2 != null) {
                    imageReader2.dispose();
                }
                if (imageInputStream2 != null) {
                    try {
                        imageInputStream2.close();
                    } catch (IOException e) {
                    }
                }
                return read;
            }
            throw new HttpMessageNotReadableException("Could not find javax.imageio.ImageReader for Content-Type [" + contentType + "]", inputMessage);
        } catch (Throwable th) {
            if (0 != 0) {
                imageReader.dispose();
            }
            if (0 != 0) {
                try {
                    imageInputStream.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    private ImageInputStream createImageInputStream(InputStream is) throws IOException {
        InputStream is2 = StreamUtils.nonClosing(is);
        if (this.cacheDir != null) {
            return new FileCacheImageInputStream(is2, this.cacheDir);
        }
        return new MemoryCacheImageInputStream(is2);
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public void write(final BufferedImage image, @Nullable final MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final MediaType selectedContentType = getContentType(contentType);
        outputMessage.getHeaders().setContentType(selectedContentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() { // from class: org.springframework.http.converter.BufferedImageHttpMessageConverter.1
                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public void writeTo(OutputStream outputStream) throws IOException {
                    BufferedImageHttpMessageConverter.this.writeInternal(image, selectedContentType, outputStream);
                }

                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public boolean repeatable() {
                    return true;
                }
            });
        } else {
            writeInternal(image, selectedContentType, outputMessage.getBody());
        }
    }

    private MediaType getContentType(@Nullable MediaType contentType) {
        if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
            contentType = getDefaultContentType();
        }
        Assert.notNull(contentType, "Could not select Content-Type. Please specify one through the 'defaultContentType' property.");
        return contentType;
    }

    private void writeInternal(BufferedImage image, MediaType contentType, OutputStream body) throws IOException, HttpMessageNotWritableException {
        ImageOutputStream imageOutputStream = null;
        ImageWriter imageWriter = null;
        try {
            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(contentType.toString());
            if (imageWriters.hasNext()) {
                ImageWriter imageWriter2 = imageWriters.next();
                ImageWriteParam iwp = imageWriter2.getDefaultWriteParam();
                process(iwp);
                ImageOutputStream imageOutputStream2 = createImageOutputStream(body);
                imageWriter2.setOutput(imageOutputStream2);
                imageWriter2.write((IIOMetadata) null, new IIOImage(image, (List) null, (IIOMetadata) null), iwp);
                if (imageWriter2 != null) {
                    imageWriter2.dispose();
                }
                if (imageOutputStream2 != null) {
                    try {
                        imageOutputStream2.close();
                        return;
                    } catch (IOException e) {
                        return;
                    }
                }
                return;
            }
            throw new HttpMessageNotWritableException("Could not find javax.imageio.ImageWriter for Content-Type [" + contentType + "]");
        } catch (Throwable th) {
            if (0 != 0) {
                imageWriter.dispose();
            }
            if (0 != 0) {
                try {
                    imageOutputStream.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    private ImageOutputStream createImageOutputStream(OutputStream os) throws IOException {
        if (this.cacheDir != null) {
            return new FileCacheImageOutputStream(os, this.cacheDir);
        }
        return new MemoryCacheImageOutputStream(os);
    }

    protected void process(ImageReadParam irp) {
    }

    protected void process(ImageWriteParam iwp) {
    }
}
