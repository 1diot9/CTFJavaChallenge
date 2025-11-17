package org.springframework.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/RequestPartServletServerHttpRequest.class */
public class RequestPartServletServerHttpRequest extends ServletServerHttpRequest {
    private final MultipartHttpServletRequest multipartRequest;
    private final String requestPartName;
    private final HttpHeaders multipartHeaders;

    public RequestPartServletServerHttpRequest(HttpServletRequest request, String requestPartName) throws MissingServletRequestPartException {
        super(request);
        this.multipartRequest = MultipartResolutionDelegate.asMultipartHttpServletRequest(request);
        this.requestPartName = requestPartName;
        HttpHeaders multipartHeaders = this.multipartRequest.getMultipartHeaders(requestPartName);
        if (multipartHeaders == null) {
            throw new MissingServletRequestPartException(requestPartName);
        }
        this.multipartHeaders = multipartHeaders;
    }

    @Override // org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.multipartHeaders;
    }

    @Override // org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        Part part;
        Part part2;
        boolean servletParts = this.multipartRequest instanceof StandardMultipartHttpServletRequest;
        if (servletParts && (part2 = retrieveServletPart()) != null) {
            return part2.getInputStream();
        }
        MultipartFile file = this.multipartRequest.getFile(this.requestPartName);
        if (file != null) {
            return file.getInputStream();
        }
        String paramValue = this.multipartRequest.getParameter(this.requestPartName);
        if (paramValue != null) {
            return new ByteArrayInputStream(paramValue.getBytes(determineCharset()));
        }
        if (!servletParts && (part = retrieveServletPart()) != null) {
            return part.getInputStream();
        }
        throw new IllegalStateException("No body available for request part '" + this.requestPartName + "'");
    }

    @Nullable
    private Part retrieveServletPart() {
        try {
            return this.multipartRequest.getPart(this.requestPartName);
        } catch (Exception ex) {
            throw new MultipartException("Failed to retrieve request part '" + this.requestPartName + "'", ex);
        }
    }

    private Charset determineCharset() {
        Charset charset;
        MediaType contentType = getHeaders().getContentType();
        if (contentType != null && (charset = contentType.getCharset()) != null) {
            return charset;
        }
        String encoding = this.multipartRequest.getCharacterEncoding();
        return encoding != null ? Charset.forName(encoding) : FORM_CHARSET;
    }
}
