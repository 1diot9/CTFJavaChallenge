package org.springframework.web.util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ContentCachingRequestWrapper.class */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {
    private final FastByteArrayOutputStream cachedContent;

    @Nullable
    private final Integer contentCacheLimit;

    @Nullable
    private ServletInputStream inputStream;

    @Nullable
    private BufferedReader reader;

    public ContentCachingRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = contentLength > 0 ? new FastByteArrayOutputStream(contentLength) : new FastByteArrayOutputStream();
        this.contentCacheLimit = null;
    }

    public ContentCachingRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = contentLength > 0 ? new FastByteArrayOutputStream(contentLength) : new FastByteArrayOutputStream();
        this.contentCacheLimit = Integer.valueOf(contentCacheLimit);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
        }
        return this.inputStream;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return enc != null ? enc : "ISO-8859-1";
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
        return this.reader;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) && HttpMethod.POST.matches(getMethod());
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                Iterator<String> nameIterator = form.keySet().iterator();
                while (nameIterator.hasNext()) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    Iterator<String> valueIterator = values.iterator();
                    while (valueIterator.hasNext()) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write(61);
                            this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write(38);
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write(38);
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    public byte[] getContentAsByteArray() {
        return this.cachedContent.toByteArray();
    }

    public String getContentAsString() {
        return this.cachedContent.toString(Charset.forName(getCharacterEncoding()));
    }

    protected void handleContentOverflow(int contentCacheLimit) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ContentCachingRequestWrapper$ContentCachingInputStream.class */
    public class ContentCachingInputStream extends ServletInputStream {
        private final ServletInputStream is;
        private boolean overflow = false;

        public ContentCachingInputStream(ServletInputStream is) {
            this.is = is;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ch2 = this.is.read();
            if (ch2 != -1 && !this.overflow) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit != null && ContentCachingRequestWrapper.this.cachedContent.size() == ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
                    this.overflow = true;
                    ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
                } else {
                    ContentCachingRequestWrapper.this.cachedContent.write(ch2);
                }
            }
            return ch2;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            int count = this.is.read(b);
            writeToCache(b, 0, count);
            return count;
        }

        private void writeToCache(final byte[] b, final int off, int count) throws IOException {
            if (!this.overflow && count > 0) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit != null && count + ContentCachingRequestWrapper.this.cachedContent.size() > ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
                    this.overflow = true;
                    ContentCachingRequestWrapper.this.cachedContent.write(b, off, ContentCachingRequestWrapper.this.contentCacheLimit.intValue() - ContentCachingRequestWrapper.this.cachedContent.size());
                    ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
                    return;
                }
                ContentCachingRequestWrapper.this.cachedContent.write(b, off, count);
            }
        }

        @Override // java.io.InputStream
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int count = this.is.read(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override // jakarta.servlet.ServletInputStream
        public int readLine(final byte[] b, final int off, final int len) throws IOException {
            int count = this.is.readLine(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override // jakarta.servlet.ServletInputStream
        public boolean isFinished() {
            return this.is.isFinished();
        }

        @Override // jakarta.servlet.ServletInputStream
        public boolean isReady() {
            return this.is.isReady();
        }

        @Override // jakarta.servlet.ServletInputStream
        public void setReadListener(ReadListener readListener) {
            this.is.setReadListener(readListener);
        }
    }
}
