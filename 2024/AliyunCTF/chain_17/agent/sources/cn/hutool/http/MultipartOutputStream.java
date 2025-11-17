package cn.hutool.http;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.MultiResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.StringResource;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/MultipartOutputStream.class */
public class MultipartOutputStream extends OutputStream {
    private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"\r\n";
    private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n";
    private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: {}\r\n";
    private final OutputStream out;
    private final Charset charset;
    private final String boundary;
    private boolean isFinish;

    public MultipartOutputStream(OutputStream out, Charset charset) {
        this(out, charset, HttpGlobalConfig.getBoundary());
    }

    public MultipartOutputStream(OutputStream out, Charset charset, String boundary) {
        this.out = out;
        this.charset = charset;
        this.boundary = boundary;
    }

    public MultipartOutputStream write(String formFieldName, Object value) throws IORuntimeException {
        if (value instanceof MultiResource) {
            Iterator<Resource> it = ((MultiResource) value).iterator();
            while (it.hasNext()) {
                Resource subResource = it.next();
                write(formFieldName, subResource);
            }
            return this;
        }
        beginPart();
        if (value instanceof Resource) {
            appendResource(formFieldName, (Resource) value);
        } else {
            appendResource(formFieldName, new StringResource(Convert.toStr(value), null, this.charset));
        }
        write("\r\n");
        return this;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    public void finish() throws IORuntimeException {
        if (false == this.isFinish) {
            write(StrUtil.format("--{}--\r\n", this.boundary));
            this.isFinish = true;
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        finish();
        IoUtil.close((Closeable) this.out);
    }

    private void appendResource(String formFieldName, Resource resource) throws IORuntimeException {
        String fileName = resource.getName();
        if (null == fileName) {
            write(StrUtil.format(CONTENT_DISPOSITION_TEMPLATE, formFieldName));
        } else {
            write(StrUtil.format(CONTENT_DISPOSITION_FILE_TEMPLATE, formFieldName, fileName));
        }
        if (resource instanceof HttpResource) {
            String contentType = ((HttpResource) resource).getContentType();
            if (StrUtil.isNotBlank(contentType)) {
                write(StrUtil.format(CONTENT_TYPE_FILE_TEMPLATE, contentType));
            }
        } else if (StrUtil.isNotEmpty(fileName)) {
            write(StrUtil.format(CONTENT_TYPE_FILE_TEMPLATE, HttpUtil.getMimeType(fileName, ContentType.OCTET_STREAM.getValue())));
        }
        write("\r\n");
        resource.writeTo(this);
    }

    private void beginPart() {
        write("--", this.boundary, "\r\n");
    }

    private void write(Object... objs) {
        IoUtil.write((OutputStream) this, this.charset, false, objs);
    }
}
