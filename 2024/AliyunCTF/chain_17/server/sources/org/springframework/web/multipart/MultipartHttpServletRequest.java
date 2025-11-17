package org.springframework.web.multipart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/MultipartHttpServletRequest.class */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {
    HttpMethod getRequestMethod();

    HttpHeaders getRequestHeaders();

    @Nullable
    HttpHeaders getMultipartHeaders(String paramOrFileName);
}
