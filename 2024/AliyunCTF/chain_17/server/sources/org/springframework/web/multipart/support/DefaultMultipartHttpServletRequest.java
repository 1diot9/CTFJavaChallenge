package org.springframework.web.multipart.support;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/DefaultMultipartHttpServletRequest.class */
public class DefaultMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {
    private static final String CONTENT_TYPE = "Content-Type";

    @Nullable
    private Map<String, String[]> multipartParameters;

    @Nullable
    private Map<String, String> multipartParameterContentTypes;

    public DefaultMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
        super(request);
        setMultipartFiles(mpFiles);
        setMultipartParameters(mpParams);
        setMultipartParameterContentTypes(mpParamContentTypes);
    }

    public DefaultMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    @Nullable
    public String getParameter(String name) {
        String[] values = getMultipartParameters().get(name);
        if (values != null) {
            if (values.length > 0) {
                return values[0];
            }
            return null;
        }
        return super.getParameter(name);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        String[] mpValues = getMultipartParameters().get(name);
        if (mpValues == null) {
            return parameterValues;
        }
        if (parameterValues == null || getQueryString() == null) {
            return mpValues;
        }
        String[] result = new String[mpValues.length + parameterValues.length];
        System.arraycopy(mpValues, 0, result, 0, mpValues.length);
        System.arraycopy(parameterValues, 0, result, mpValues.length, parameterValues.length);
        return result;
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        Map<String, String[]> multipartParameters = getMultipartParameters();
        if (multipartParameters.isEmpty()) {
            return super.getParameterNames();
        }
        Set<String> paramNames = new LinkedHashSet<>();
        paramNames.addAll(Collections.list(super.getParameterNames()));
        paramNames.addAll(multipartParameters.keySet());
        return Collections.enumeration(paramNames);
    }

    @Override // jakarta.servlet.ServletRequestWrapper, jakarta.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> result = new LinkedHashMap<>();
        Enumeration<String> names = getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, getParameterValues(name));
        }
        return result;
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public String getMultipartContentType(String paramOrFileName) {
        MultipartFile file = getFile(paramOrFileName);
        if (file != null) {
            return file.getContentType();
        }
        return getMultipartParameterContentTypes().get(paramOrFileName);
    }

    @Override // org.springframework.web.multipart.MultipartHttpServletRequest
    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        String contentType = getMultipartContentType(paramOrFileName);
        if (contentType != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", contentType);
            return headers;
        }
        return null;
    }

    protected final void setMultipartParameters(Map<String, String[]> multipartParameters) {
        this.multipartParameters = multipartParameters;
    }

    protected Map<String, String[]> getMultipartParameters() {
        if (this.multipartParameters == null) {
            initializeMultipart();
        }
        return this.multipartParameters;
    }

    protected final void setMultipartParameterContentTypes(Map<String, String> multipartParameterContentTypes) {
        this.multipartParameterContentTypes = multipartParameterContentTypes;
    }

    protected Map<String, String> getMultipartParameterContentTypes() {
        if (this.multipartParameterContentTypes == null) {
            initializeMultipart();
        }
        return this.multipartParameterContentTypes;
    }
}
