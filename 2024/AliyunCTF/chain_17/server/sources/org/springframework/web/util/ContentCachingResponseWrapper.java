package org.springframework.web.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ContentCachingResponseWrapper.class */
public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {
    private final FastByteArrayOutputStream content;

    @Nullable
    private ServletOutputStream outputStream;

    @Nullable
    private PrintWriter writer;

    @Nullable
    private Integer contentLength;

    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
        this.content = new FastByteArrayOutputStream(1024);
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc);
        } catch (IllegalStateException e) {
            super.setStatus(sc);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc, msg);
        } catch (IllegalStateException e) {
            super.setStatus(sc);
        }
    }

    @Override // jakarta.servlet.http.HttpServletResponseWrapper, jakarta.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        copyBodyToResponse(false);
        super.sendRedirect(location);
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.outputStream == null) {
            this.outputStream = new ResponseServletOutputStream(getResponse().getOutputStream());
        }
        return this.outputStream;
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            String characterEncoding = getCharacterEncoding();
            this.writer = characterEncoding != null ? new ResponsePrintWriter(characterEncoding) : new ResponsePrintWriter("ISO-8859-1");
        }
        return this.writer;
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void flushBuffer() throws IOException {
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLength(int len) {
        if (len > this.content.size()) {
            this.content.resize(len);
        }
        this.contentLength = Integer.valueOf(len);
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (len > 2147483647L) {
            throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (2147483647): " + len);
        }
        int lenInt = (int) len;
        if (lenInt > this.content.size()) {
            this.content.resize(lenInt);
        }
        this.contentLength = Integer.valueOf(lenInt);
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (size > this.content.size()) {
            this.content.resize(size);
        }
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void resetBuffer() {
        this.content.reset();
    }

    @Override // jakarta.servlet.ServletResponseWrapper, jakarta.servlet.ServletResponse
    public void reset() {
        super.reset();
        this.content.reset();
    }

    public byte[] getContentAsByteArray() {
        return this.content.toByteArray();
    }

    public InputStream getContentInputStream() {
        return this.content.getInputStream();
    }

    public int getContentSize() {
        return this.content.size();
    }

    public void copyBodyToResponse() throws IOException {
        copyBodyToResponse(true);
    }

    protected void copyBodyToResponse(boolean complete) throws IOException {
        if (this.content.size() > 0) {
            HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
            if ((complete || this.contentLength != null) && !rawResponse.isCommitted()) {
                if (rawResponse.getHeader("Transfer-Encoding") == null) {
                    rawResponse.setContentLength(complete ? this.content.size() : this.contentLength.intValue());
                }
                this.contentLength = null;
            }
            this.content.writeTo(rawResponse.getOutputStream());
            this.content.reset();
            if (complete) {
                super.flushBuffer();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ContentCachingResponseWrapper$ResponseServletOutputStream.class */
    private class ResponseServletOutputStream extends ServletOutputStream {
        private final ServletOutputStream os;

        public ResponseServletOutputStream(ServletOutputStream os) {
            this.os = os;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b, off, len);
        }

        @Override // jakarta.servlet.ServletOutputStream
        public boolean isReady() {
            return this.os.isReady();
        }

        @Override // jakarta.servlet.ServletOutputStream
        public void setWriteListener(WriteListener writeListener) {
            this.os.setWriteListener(writeListener);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ContentCachingResponseWrapper$ResponsePrintWriter.class */
    private class ResponsePrintWriter extends PrintWriter {
        public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter(ContentCachingResponseWrapper.this.content, characterEncoding));
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }
}
