package org.springframework.web.context.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/ServletWebRequest.class */
public class ServletWebRequest extends ServletRequestAttributes implements NativeWebRequest {
    private static final Set<String> SAFE_METHODS = Set.of("GET", WebContentGenerator.METHOD_HEAD);
    private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
    private static final String[] DATE_FORMATS = {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy"};
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private boolean notModified;

    public ServletWebRequest(HttpServletRequest request) {
        super(request);
        this.notModified = false;
    }

    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
        this.notModified = false;
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeRequest() {
        return getRequest();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeResponse() {
        return getResponse();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeRequest(@Nullable Class<T> cls) {
        return (T) WebUtils.getNativeRequest(getRequest(), cls);
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeResponse(@Nullable Class<T> cls) {
        HttpServletResponse response = getResponse();
        if (response != null) {
            return (T) WebUtils.getNativeResponse(response, cls);
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.valueOf(getRequest().getMethod());
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getHeaderValues(String headerName) {
        String[] headerValues = StringUtils.toStringArray(getRequest().getHeaders(headerName));
        if (ObjectUtils.isEmpty((Object[]) headerValues)) {
            return null;
        }
        return headerValues;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getHeaderNames() {
        return CollectionUtils.toIterator(getRequest().getHeaderNames());
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getParameter(String paramName) {
        return getRequest().getParameter(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getParameterValues(String paramName) {
        return getRequest().getParameterValues(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getParameterNames() {
        return CollectionUtils.toIterator(getRequest().getParameterNames());
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Map<String, String[]> getParameterMap() {
        return getRequest().getParameterMap();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Locale getLocale() {
        return getRequest().getLocale();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getContextPath() {
        return getRequest().getContextPath();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getRemoteUser() {
        return getRequest().getRemoteUser();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public Principal getUserPrincipal() {
        return getRequest().getUserPrincipal();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isUserInRole(String role) {
        return getRequest().isUserInRole(role);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isSecure() {
        return getRequest().isSecure();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return checkNotModified(null, lastModifiedTimestamp);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(String etag) {
        return checkNotModified(etag, -1L);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(@Nullable String eTag, long lastModifiedTimestamp) {
        HttpServletResponse response = getResponse();
        if (this.notModified || (response != null && HttpStatus.OK.value() != response.getStatus())) {
            return this.notModified;
        }
        if (validateIfMatch(eTag)) {
            updateResponseStateChanging(eTag, lastModifiedTimestamp);
            return this.notModified;
        }
        if (validateIfUnmodifiedSince(lastModifiedTimestamp)) {
            updateResponseStateChanging(eTag, lastModifiedTimestamp);
            return this.notModified;
        }
        if (!validateIfNoneMatch(eTag)) {
            validateIfModifiedSince(lastModifiedTimestamp);
        }
        updateResponseIdempotent(eTag, lastModifiedTimestamp);
        return this.notModified;
    }

    private boolean validateIfMatch(@Nullable String eTag) {
        if (SAFE_METHODS.contains(getRequest().getMethod())) {
            return false;
        }
        Enumeration<String> ifMatchHeaders = getRequest().getHeaders(HttpHeaders.IF_MATCH);
        if (!ifMatchHeaders.hasMoreElements()) {
            return false;
        }
        this.notModified = matchRequestedETags(ifMatchHeaders, eTag, false);
        return true;
    }

    private boolean validateIfNoneMatch(@Nullable String eTag) {
        Enumeration<String> ifNoneMatchHeaders = getRequest().getHeaders(HttpHeaders.IF_NONE_MATCH);
        if (!ifNoneMatchHeaders.hasMoreElements()) {
            return false;
        }
        this.notModified = !matchRequestedETags(ifNoneMatchHeaders, eTag, true);
        return true;
    }

    private boolean matchRequestedETags(Enumeration<String> requestedETags, @Nullable String eTag, boolean weakCompare) {
        String eTag2 = padEtagIfNecessary(eTag);
        while (requestedETags.hasMoreElements()) {
            Matcher eTagMatcher = ETAG_HEADER_VALUE_PATTERN.matcher(requestedETags.nextElement());
            while (eTagMatcher.find()) {
                if ("*".equals(eTagMatcher.group()) && StringUtils.hasLength(eTag2) && !SAFE_METHODS.contains(getRequest().getMethod())) {
                    return false;
                }
                if (weakCompare) {
                    if (eTagWeakMatch(eTag2, eTagMatcher.group(1))) {
                        return false;
                    }
                } else if (eTagStrongMatch(eTag2, eTagMatcher.group(1))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Nullable
    private String padEtagIfNecessary(@Nullable String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean eTagStrongMatch(@Nullable String first, @Nullable String second) {
        if (!StringUtils.hasLength(first) || first.startsWith("W/")) {
            return false;
        }
        return first.equals(second);
    }

    private boolean eTagWeakMatch(@Nullable String first, @Nullable String second) {
        if (!StringUtils.hasLength(first) || !StringUtils.hasLength(second)) {
            return false;
        }
        if (first.startsWith("W/")) {
            first = first.substring(2);
        }
        if (second.startsWith("W/")) {
            second = second.substring(2);
        }
        return first.equals(second);
    }

    private void updateResponseStateChanging(@Nullable String eTag, long lastModifiedTimestamp) {
        if (this.notModified && getResponse() != null) {
            getResponse().setStatus(HttpStatus.PRECONDITION_FAILED.value());
        } else {
            addCachingResponseHeaders(eTag, lastModifiedTimestamp);
        }
    }

    private boolean validateIfUnmodifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return false;
        }
        long ifUnmodifiedSince = parseDateHeader(HttpHeaders.IF_UNMODIFIED_SINCE);
        if (ifUnmodifiedSince == -1) {
            return false;
        }
        this.notModified = ifUnmodifiedSince < (lastModifiedTimestamp / 1000) * 1000;
        return true;
    }

    private void validateIfModifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return;
        }
        long ifModifiedSince = parseDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (ifModifiedSince != -1) {
            this.notModified = ifModifiedSince >= (lastModifiedTimestamp / 1000) * 1000;
        }
    }

    private void updateResponseIdempotent(@Nullable String eTag, long lastModifiedTimestamp) {
        if (getResponse() != null) {
            boolean isHttpGetOrHead = SAFE_METHODS.contains(getRequest().getMethod());
            if (this.notModified) {
                getResponse().setStatus(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED.value() : HttpStatus.PRECONDITION_FAILED.value());
            }
            addCachingResponseHeaders(eTag, lastModifiedTimestamp);
        }
    }

    private void addCachingResponseHeaders(@Nullable String eTag, long lastModifiedTimestamp) {
        if (SAFE_METHODS.contains(getRequest().getMethod())) {
            if (lastModifiedTimestamp > 0 && parseDateValue(getResponse().getHeader(HttpHeaders.LAST_MODIFIED)) == -1) {
                getResponse().setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedTimestamp);
            }
            if (StringUtils.hasLength(eTag) && getResponse().getHeader(HttpHeaders.ETAG) == null) {
                getResponse().setHeader(HttpHeaders.ETAG, padEtagIfNecessary(eTag));
            }
        }
    }

    public boolean isNotModified() {
        return this.notModified;
    }

    private long parseDateHeader(String headerName) {
        int separatorIndex;
        long dateValue = -1;
        try {
            dateValue = getRequest().getDateHeader(headerName);
        } catch (IllegalArgumentException e) {
            String headerValue = getHeader(headerName);
            if (headerValue != null && (separatorIndex = headerValue.indexOf(59)) != -1) {
                String datePart = headerValue.substring(0, separatorIndex);
                dateValue = parseDateValue(datePart);
            }
        }
        return dateValue;
    }

    private long parseDateValue(@Nullable String headerValue) {
        if (headerValue != null && headerValue.length() >= 3) {
            for (String dateFormat : DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                simpleDateFormat.setTimeZone(GMT);
                try {
                    return simpleDateFormat.parse(headerValue).getTime();
                } catch (ParseException e) {
                }
            }
            return -1L;
        }
        return -1L;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getDescription(boolean includeClientInfo) {
        HttpServletRequest request = getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("uri=").append(request.getRequestURI());
        if (includeClientInfo) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                sb.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                sb.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (StringUtils.hasLength(user)) {
                sb.append(";user=").append(user);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.web.context.request.ServletRequestAttributes
    public String toString() {
        return "ServletWebRequest: " + getDescription(true);
    }
}
