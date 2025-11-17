package cn.hutool.http.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/HttpServerResponse.class */
public class HttpServerResponse extends HttpServerBase {
    private Charset charset;
    private boolean isSendCode;

    public HttpServerResponse(HttpExchange httpExchange) {
        super(httpExchange);
    }

    public HttpServerResponse send(int httpStatusCode) {
        return send(httpStatusCode, 0L);
    }

    public HttpServerResponse sendOk() {
        return send(200);
    }

    public HttpServerResponse sendOk(int bodyLength) {
        return send(200, bodyLength);
    }

    public HttpServerResponse send404(String content) {
        return sendError(404, content);
    }

    public HttpServerResponse sendError(int errorCode, String content) {
        send(errorCode);
        setContentType(ContentType.TEXT_HTML.toString());
        return write(content);
    }

    public HttpServerResponse send(int httpStatusCode, long bodyLength) {
        if (this.isSendCode) {
            throw new IORuntimeException("Http status code has been send!");
        }
        try {
            this.httpExchange.sendResponseHeaders(httpStatusCode, bodyLength);
            this.isSendCode = true;
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public Headers getHeaders() {
        return this.httpExchange.getResponseHeaders();
    }

    public HttpServerResponse addHeader(String header, String value) {
        getHeaders().add(header, value);
        return this;
    }

    public HttpServerResponse setHeader(Header header, String value) {
        return setHeader(header.getValue(), value);
    }

    public HttpServerResponse setHeader(String header, String value) {
        getHeaders().set(header, value);
        return this;
    }

    public HttpServerResponse setHeader(String header, List<String> value) {
        getHeaders().put(header, value);
        return this;
    }

    public HttpServerResponse setHeaders(Map<String, List<String>> headers) {
        getHeaders().putAll(headers);
        return this;
    }

    public HttpServerResponse setContentType(String contentType) {
        if (null != contentType && null != this.charset && false == contentType.contains(WebUtils.CONTENT_TYPE_CHARSET_PREFIX)) {
            contentType = ContentType.build(contentType, this.charset);
        }
        return setHeader(Header.CONTENT_TYPE, contentType);
    }

    public HttpServerResponse setContentLength(long contentLength) {
        return setHeader(Header.CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public HttpServerResponse setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public HttpServerResponse setAttr(String name, Object value) {
        this.httpExchange.setAttribute(name, value);
        return this;
    }

    public OutputStream getOut() {
        if (false == this.isSendCode) {
            sendOk();
        }
        return this.httpExchange.getResponseBody();
    }

    public PrintWriter getWriter() {
        Charset charset = (Charset) ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
        return new PrintWriter(new OutputStreamWriter(getOut(), charset));
    }

    public HttpServerResponse write(String data, String contentType) {
        setContentType(contentType);
        return write(data);
    }

    public HttpServerResponse write(String data) {
        Charset charset = (Charset) ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
        return write(StrUtil.bytes(data, charset));
    }

    public HttpServerResponse write(byte[] data, String contentType) {
        setContentType(contentType);
        return write(data);
    }

    public HttpServerResponse write(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return write(in, in.available());
    }

    public HttpServerResponse write(InputStream in, String contentType) {
        return write(in, 0, contentType);
    }

    public HttpServerResponse write(InputStream in, int length, String contentType) {
        setContentType(contentType);
        return write(in, length);
    }

    public HttpServerResponse write(InputStream in) {
        return write(in, 0);
    }

    public HttpServerResponse write(InputStream in, int length) {
        if (false == this.isSendCode) {
            sendOk(Math.max(0, length));
        }
        OutputStream out = null;
        try {
            out = this.httpExchange.getResponseBody();
            IoUtil.copy(in, out);
            IoUtil.close((Closeable) out);
            IoUtil.close((Closeable) in);
            return this;
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public HttpServerResponse write(File file) {
        return write(file, (String) null);
    }

    public HttpServerResponse write(File file, String fileName) {
        long fileSize = file.length();
        if (fileSize > 2147483647L) {
            throw new IllegalArgumentException("File size is too bigger than 2147483647");
        }
        if (StrUtil.isBlank(fileName)) {
            fileName = file.getName();
        }
        String contentType = (String) ObjectUtil.defaultIfNull(HttpUtil.getMimeType(fileName), "application/octet-stream");
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            write(in, (int) fileSize, contentType, fileName);
            IoUtil.close((Closeable) in);
            return this;
        } catch (Throwable th) {
            IoUtil.close((Closeable) in);
            throw th;
        }
    }

    public void write(InputStream in, String contentType, String fileName) {
        write(in, 0, contentType, fileName);
    }

    public HttpServerResponse write(InputStream in, int length, String contentType, String fileName) {
        Charset charset = (Charset) ObjectUtil.defaultIfNull(this.charset, DEFAULT_CHARSET);
        if (false == contentType.startsWith("text/")) {
            setHeader(Header.CONTENT_DISPOSITION, StrUtil.format("attachment;filename={}", URLUtil.encode(fileName, charset)));
        }
        return write(in, length, contentType);
    }
}
