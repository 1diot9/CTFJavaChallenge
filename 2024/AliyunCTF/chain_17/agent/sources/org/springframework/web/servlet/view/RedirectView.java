package org.springframework.web.servlet.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/RedirectView.class */
public class RedirectView extends AbstractUrlBasedView implements SmartView {
    private static final Pattern URI_TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    private boolean contextRelative;
    private boolean http10Compatible;
    private boolean exposeModelAttributes;

    @Nullable
    private String encodingScheme;

    @Nullable
    private HttpStatusCode statusCode;
    private boolean expandUriTemplateVariables;
    private boolean propagateQueryParams;

    @Nullable
    private String[] hosts;

    public RedirectView() {
        this.contextRelative = false;
        this.http10Compatible = true;
        this.exposeModelAttributes = true;
        this.expandUriTemplateVariables = true;
        this.propagateQueryParams = false;
        setExposePathVariables(false);
    }

    public RedirectView(String url) {
        super(url);
        this.contextRelative = false;
        this.http10Compatible = true;
        this.exposeModelAttributes = true;
        this.expandUriTemplateVariables = true;
        this.propagateQueryParams = false;
        setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative) {
        super(url);
        this.contextRelative = false;
        this.http10Compatible = true;
        this.exposeModelAttributes = true;
        this.expandUriTemplateVariables = true;
        this.propagateQueryParams = false;
        this.contextRelative = contextRelative;
        setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible) {
        super(url);
        this.contextRelative = false;
        this.http10Compatible = true;
        this.exposeModelAttributes = true;
        this.expandUriTemplateVariables = true;
        this.propagateQueryParams = false;
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
        setExposePathVariables(false);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible, boolean exposeModelAttributes) {
        super(url);
        this.contextRelative = false;
        this.http10Compatible = true;
        this.exposeModelAttributes = true;
        this.expandUriTemplateVariables = true;
        this.propagateQueryParams = false;
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
        this.exposeModelAttributes = exposeModelAttributes;
        setExposePathVariables(false);
    }

    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    public void setHttp10Compatible(boolean http10Compatible) {
        this.http10Compatible = http10Compatible;
    }

    public void setExposeModelAttributes(final boolean exposeModelAttributes) {
        this.exposeModelAttributes = exposeModelAttributes;
    }

    public void setEncodingScheme(String encodingScheme) {
        this.encodingScheme = encodingScheme;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setExpandUriTemplateVariables(boolean expandUriTemplateVariables) {
        this.expandUriTemplateVariables = expandUriTemplateVariables;
    }

    public void setPropagateQueryParams(boolean propagateQueryParams) {
        this.propagateQueryParams = propagateQueryParams;
    }

    public boolean isPropagateQueryProperties() {
        return this.propagateQueryParams;
    }

    public void setHosts(@Nullable String... hosts) {
        this.hosts = hosts;
    }

    @Nullable
    public String[] getHosts() {
        return this.hosts;
    }

    @Override // org.springframework.web.servlet.SmartView
    public boolean isRedirectView() {
        return true;
    }

    @Override // org.springframework.web.context.support.WebApplicationObjectSupport, org.springframework.context.support.ApplicationObjectSupport
    protected boolean isContextRequired() {
        return false;
    }

    @Override // org.springframework.web.servlet.view.AbstractView
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String targetUrl = updateTargetUrl(createTargetUrl(model, request), model, request, response);
        RequestContextUtils.saveOutputFlashMap(targetUrl, request, response);
        sendRedirect(request, response, targetUrl, this.http10Compatible);
    }

    protected final String createTargetUrl(Map<String, Object> model, HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder targetUrl = new StringBuilder();
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        if (this.contextRelative && getUrl().startsWith("/")) {
            targetUrl.append(getContextPath(request));
        }
        targetUrl.append(getUrl());
        String enc = this.encodingScheme;
        if (enc == null) {
            enc = request.getCharacterEncoding();
        }
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        if (this.expandUriTemplateVariables && StringUtils.hasText(targetUrl)) {
            Map<String, String> variables = getCurrentRequestUriVariables(request);
            targetUrl = replaceUriTemplateVariables(targetUrl.toString(), model, variables, enc);
        }
        if (isPropagateQueryProperties()) {
            appendCurrentQueryParams(targetUrl, request);
        }
        if (this.exposeModelAttributes) {
            appendQueryProperties(targetUrl, model, enc);
        }
        return targetUrl.toString();
    }

    private String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        while (true) {
            String contextPath2 = contextPath;
            if (contextPath2.startsWith("//")) {
                contextPath = contextPath2.substring(1);
            } else {
                return contextPath2;
            }
        }
    }

    protected StringBuilder replaceUriTemplateVariables(String targetUrl, Map<String, Object> model, Map<String, String> currentUriVariables, String encodingScheme) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        Matcher matcher = URI_TEMPLATE_VARIABLE_PATTERN.matcher(targetUrl);
        int i = 0;
        while (true) {
            int endLastMatch = i;
            if (matcher.find()) {
                String name = matcher.group(1);
                Object value = model.containsKey(name) ? model.remove(name) : currentUriVariables.get(name);
                if (value == null) {
                    throw new IllegalArgumentException("Model has no value for key '" + name + "'");
                }
                result.append((CharSequence) targetUrl, endLastMatch, matcher.start());
                result.append(UriUtils.encodePathSegment(value.toString(), encodingScheme));
                i = matcher.end();
            } else {
                result.append(targetUrl.substring(endLastMatch));
                return result;
            }
        }
    }

    private Map<String, String> getCurrentRequestUriVariables(HttpServletRequest request) {
        String name = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map<String, String> uriVars = (Map) request.getAttribute(name);
        return uriVars != null ? uriVars : Collections.emptyMap();
    }

    protected void appendCurrentQueryParams(StringBuilder targetUrl, HttpServletRequest request) {
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            String fragment = null;
            int anchorIndex = targetUrl.indexOf("#");
            if (anchorIndex > -1) {
                fragment = targetUrl.substring(anchorIndex);
                targetUrl.delete(anchorIndex, targetUrl.length());
            }
            if (targetUrl.toString().indexOf(63) < 0) {
                targetUrl.append('?').append(query);
            } else {
                targetUrl.append('&').append(query);
            }
            if (fragment != null) {
                targetUrl.append(fragment);
            }
        }
    }

    protected void appendQueryProperties(StringBuilder targetUrl, Map<String, Object> model, String encodingScheme) throws UnsupportedEncodingException {
        Collection<?> values;
        String fragment = null;
        int anchorIndex = targetUrl.indexOf("#");
        if (anchorIndex > -1) {
            fragment = targetUrl.substring(anchorIndex);
            targetUrl.delete(anchorIndex, targetUrl.length());
        }
        boolean first = targetUrl.toString().indexOf(63) < 0;
        for (Map.Entry<String, Object> entry : queryProperties(model).entrySet()) {
            Object rawValue = entry.getValue();
            if (rawValue != null && rawValue.getClass().isArray()) {
                values = CollectionUtils.arrayToList(rawValue);
            } else if (rawValue instanceof Collection) {
                Collection<?> collection = (Collection) rawValue;
                values = collection;
            } else {
                values = Collections.singleton(rawValue);
            }
            Iterator<?> it = values.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                if (first) {
                    targetUrl.append('?');
                    first = false;
                } else {
                    targetUrl.append('&');
                }
                String encodedKey = urlEncode(entry.getKey(), encodingScheme);
                String encodedValue = value != null ? urlEncode(value.toString(), encodingScheme) : "";
                targetUrl.append(encodedKey).append('=').append(encodedValue);
            }
        }
        if (fragment != null) {
            targetUrl.append(fragment);
        }
    }

    protected Map<String, Object> queryProperties(Map<String, Object> model) {
        Map<String, Object> result = new LinkedHashMap<>();
        model.forEach((name, value) -> {
            if (isEligibleProperty(name, value)) {
                result.put(name, value);
            }
        });
        return result;
    }

    protected boolean isEligibleProperty(String key, @Nullable Object value) {
        if (value == null) {
            return false;
        }
        if (isEligibleValue(value)) {
            return true;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length == 0) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                if (!isEligibleValue(element)) {
                    return false;
                }
            }
            return true;
        }
        if (value instanceof Collection) {
            Collection<?> coll = (Collection) value;
            if (coll.isEmpty()) {
                return false;
            }
            for (Object element2 : coll) {
                if (!isEligibleValue(element2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean isEligibleValue(@Nullable Object value) {
        return value != null && BeanUtils.isSimpleValueType(value.getClass());
    }

    protected String urlEncode(String input, String encodingScheme) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, encodingScheme);
    }

    protected String updateTargetUrl(String targetUrl, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        WebApplicationContext wac = getWebApplicationContext();
        if (wac == null) {
            wac = RequestContextUtils.findWebApplicationContext(request, getServletContext());
        }
        if (wac != null && wac.containsBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
            RequestDataValueProcessor processor = (RequestDataValueProcessor) wac.getBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, RequestDataValueProcessor.class);
            return processor.processUrl(request, targetUrl);
        }
        return targetUrl;
    }

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible) throws IOException {
        String encodedURL = isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl);
        if (http10Compatible) {
            HttpStatusCode attributeStatusCode = (HttpStatusCode) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
            if (this.statusCode != null) {
                response.setStatus(this.statusCode.value());
                response.setHeader("Location", encodedURL);
                return;
            } else if (attributeStatusCode != null) {
                response.setStatus(attributeStatusCode.value());
                response.setHeader("Location", encodedURL);
                return;
            } else {
                response.sendRedirect(encodedURL);
                return;
            }
        }
        HttpStatusCode statusCode = getHttp11StatusCode(request, response, targetUrl);
        response.setStatus(statusCode.value());
        response.setHeader("Location", encodedURL);
    }

    protected boolean isRemoteHost(String targetUrl) {
        if (ObjectUtils.isEmpty((Object[]) getHosts())) {
            return false;
        }
        String targetHost = UriComponentsBuilder.fromUriString(targetUrl).build().getHost();
        if (!StringUtils.hasLength(targetHost)) {
            return false;
        }
        for (String host : getHosts()) {
            if (targetHost.equals(host)) {
                return false;
            }
        }
        return true;
    }

    protected HttpStatusCode getHttp11StatusCode(HttpServletRequest request, HttpServletResponse response, String targetUrl) {
        if (this.statusCode != null) {
            return this.statusCode;
        }
        HttpStatusCode attributeStatusCode = (HttpStatusCode) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
        if (attributeStatusCode != null) {
            return attributeStatusCode;
        }
        return HttpStatus.SEE_OTHER;
    }
}
