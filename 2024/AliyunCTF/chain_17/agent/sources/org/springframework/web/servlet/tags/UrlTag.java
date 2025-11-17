package org.springframework.web.servlet.tags;

import ch.qos.logback.core.CoreConstants;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;
import org.springframework.web.util.UriUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/UrlTag.class */
public class UrlTag extends HtmlEscapingAwareTag implements ParamAware {
    private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";
    private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";
    private static final String URL_TYPE_ABSOLUTE = "://";

    @Nullable
    private UrlType type;

    @Nullable
    private String value;

    @Nullable
    private String context;

    @Nullable
    private String var;
    private List<Param> params = Collections.emptyList();
    private Set<String> templateParams = Collections.emptySet();
    private int scope = 1;
    private boolean javaScriptEscape = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/UrlTag$UrlType.class */
    public enum UrlType {
        CONTEXT_RELATIVE,
        RELATIVE,
        ABSOLUTE
    }

    public void setValue(String value) {
        if (value.contains(URL_TYPE_ABSOLUTE)) {
            this.type = UrlType.ABSOLUTE;
            this.value = value;
        } else if (value.startsWith("/")) {
            this.type = UrlType.CONTEXT_RELATIVE;
            this.value = value;
        } else {
            this.type = UrlType.RELATIVE;
            this.value = value;
        }
    }

    public void setContext(String context) {
        if (context.startsWith("/")) {
            this.context = context;
        } else {
            this.context = "/" + context;
        }
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = TagUtils.getScope(scope);
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override // org.springframework.web.servlet.tags.ParamAware
    public void addParam(Param param) {
        this.params.add(param);
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    public int doStartTagInternal() throws JspException {
        this.params = new ArrayList();
        this.templateParams = new HashSet();
        return 1;
    }

    public int doEndTag() throws JspException {
        String url = createUrl();
        RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = this.pageContext.getRequest();
        if (processor != null && (request instanceof HttpServletRequest)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            url = processor.processUrl(httpServletRequest, url);
        }
        if (this.var == null) {
            try {
                this.pageContext.getOut().print(url);
                return 6;
            } catch (IOException ex) {
                throw new JspException(ex);
            }
        }
        this.pageContext.setAttribute(this.var, url, this.scope);
        return 6;
    }

    String createUrl() throws JspException {
        Assert.state(this.value != null, "No value set");
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
        StringBuilder url = new StringBuilder();
        if (this.type == UrlType.CONTEXT_RELATIVE) {
            if (this.context == null) {
                url.append(request.getContextPath());
            } else if (this.context.endsWith("/")) {
                url.append((CharSequence) this.context, 0, this.context.length() - 1);
            } else {
                url.append(this.context);
            }
        }
        if (this.type != UrlType.RELATIVE && this.type != UrlType.ABSOLUTE && !this.value.startsWith("/")) {
            url.append('/');
        }
        url.append(replaceUriTemplateParams(this.value, this.params, this.templateParams));
        url.append(createQueryString(this.params, this.templateParams, url.indexOf(CoreConstants.NA) == -1));
        String urlStr = url.toString();
        if (this.type != UrlType.ABSOLUTE) {
            urlStr = response.encodeURL(urlStr);
        }
        String urlStr2 = htmlEscape(urlStr);
        return this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr2) : urlStr2;
    }

    protected String createQueryString(List<Param> params, Set<String> usedParams, boolean includeQueryStringDelimiter) throws JspException {
        String encoding = this.pageContext.getResponse().getCharacterEncoding();
        StringBuilder qs = new StringBuilder();
        for (Param param : params) {
            if (!usedParams.contains(param.getName()) && StringUtils.hasLength(param.getName())) {
                if (includeQueryStringDelimiter && qs.length() == 0) {
                    qs.append('?');
                } else {
                    qs.append('&');
                }
                try {
                    qs.append(UriUtils.encodeQueryParam(param.getName(), encoding));
                    if (param.getValue() != null) {
                        qs.append('=');
                        qs.append(UriUtils.encodeQueryParam(param.getValue(), encoding));
                    }
                } catch (UnsupportedCharsetException ex) {
                    throw new JspException(ex);
                }
            }
        }
        return qs.toString();
    }

    protected String replaceUriTemplateParams(String uri, List<Param> params, Set<String> usedParams) throws JspException {
        String encodePath;
        String encodePathSegment;
        String encoding = this.pageContext.getResponse().getCharacterEncoding();
        for (Param param : params) {
            String template = "{" + param.getName() + "}";
            if (uri.contains(template)) {
                usedParams.add(param.getName());
                String value = param.getValue();
                String str = uri;
                if (value != null) {
                    try {
                        encodePath = UriUtils.encodePath(value, encoding);
                    } catch (UnsupportedCharsetException ex) {
                        throw new JspException(ex);
                    }
                } else {
                    encodePath = "";
                }
                uri = StringUtils.replace(str, template, encodePath);
            } else {
                String template2 = "{/" + param.getName() + "}";
                if (uri.contains(template2)) {
                    usedParams.add(param.getName());
                    String value2 = param.getValue();
                    String str2 = uri;
                    if (value2 != null) {
                        try {
                            encodePathSegment = UriUtils.encodePathSegment(value2, encoding);
                        } catch (UnsupportedCharsetException ex2) {
                            throw new JspException(ex2);
                        }
                    } else {
                        encodePathSegment = "";
                    }
                    uri = StringUtils.replace(str2, template2, encodePathSegment);
                } else {
                    continue;
                }
            }
        }
        return uri;
    }
}
