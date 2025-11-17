package org.springframework.web.servlet.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/UrlBasedViewResolver.class */
public class UrlBasedViewResolver extends AbstractCachingViewResolver implements Ordered {
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    public static final String FORWARD_URL_PREFIX = "forward:";

    @Nullable
    private Class<?> viewClass;

    @Nullable
    private String contentType;

    @Nullable
    private String[] redirectHosts;

    @Nullable
    private String requestContextAttribute;

    @Nullable
    private Boolean exposePathVariables;

    @Nullable
    private Boolean exposeContextBeansAsAttributes;

    @Nullable
    private String[] exposedContextBeanNames;

    @Nullable
    private String[] viewNames;
    private String prefix = "";
    private String suffix = "";
    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;
    private final Map<String, Object> staticAttributes = new HashMap();
    private int order = Integer.MAX_VALUE;

    public void setViewClass(@Nullable Class<?> viewClass) {
        if (viewClass != null && !requiredViewClass().isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException("Given view class [" + viewClass.getName() + "] is not of type [" + requiredViewClass().getName() + "]");
        }
        this.viewClass = viewClass;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Class<?> getViewClass() {
        return this.viewClass;
    }

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    protected String getSuffix() {
        return this.suffix;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    protected String getContentType() {
        return this.contentType;
    }

    public void setRedirectContextRelative(boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    protected boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    protected boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }

    public void setRedirectHosts(@Nullable String... redirectHosts) {
        this.redirectHosts = redirectHosts;
    }

    @Nullable
    public String[] getRedirectHosts() {
        return this.redirectHosts;
    }

    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    @Nullable
    protected String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setAttributes(Properties props) {
        CollectionUtils.mergePropertiesIntoMap(props, this.staticAttributes);
    }

    public void setAttributesMap(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            this.staticAttributes.putAll(attributes);
        }
    }

    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    public void setExposePathVariables(@Nullable Boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    @Nullable
    protected Boolean getExposePathVariables() {
        return this.exposePathVariables;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = Boolean.valueOf(exposeContextBeansAsAttributes);
    }

    @Nullable
    protected Boolean getExposeContextBeansAsAttributes() {
        return this.exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(@Nullable String... exposedContextBeanNames) {
        this.exposedContextBeanNames = exposedContextBeanNames;
    }

    @Nullable
    protected String[] getExposedContextBeanNames() {
        return this.exposedContextBeanNames;
    }

    public void setViewNames(@Nullable String... viewNames) {
        this.viewNames = viewNames;
    }

    @Nullable
    protected String[] getViewNames() {
        return this.viewNames;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.support.ApplicationObjectSupport
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (getViewClass() == null) {
            throw new IllegalArgumentException("Property 'viewClass' is required");
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    public View createView(String viewName, Locale locale) throws Exception {
        if (!canHandle(viewName, locale)) {
            return null;
        }
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
            String[] hosts = getRedirectHosts();
            if (hosts != null) {
                view.setHosts(hosts);
            }
            return applyLifecycleMethods(REDIRECT_URL_PREFIX, view);
        }
        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
            return applyLifecycleMethods(FORWARD_URL_PREFIX, new InternalResourceView(forwardUrl));
        }
        return super.createView(viewName, locale);
    }

    protected boolean canHandle(String viewName, Locale locale) {
        String[] viewNames = getViewNames();
        return viewNames == null || PatternMatchUtils.simpleMatch(viewNames, viewName);
    }

    protected Class<?> requiredViewClass() {
        return AbstractUrlBasedView.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractUrlBasedView instantiateView() {
        Class<?> viewClass = getViewClass();
        Assert.state(viewClass != null, "No view class");
        return (AbstractUrlBasedView) BeanUtils.instantiateClass(viewClass);
    }

    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected View loadView(String viewName, Locale locale) throws Exception {
        AbstractUrlBasedView view = buildView(viewName);
        View result = applyLifecycleMethods(viewName, view);
        if (view.checkResource(locale)) {
            return result;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractUrlBasedView view = instantiateView();
        view.setUrl(getPrefix() + viewName + getSuffix());
        view.setAttributesMap(getAttributesMap());
        String contentType = getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }
        String requestContextAttribute = getRequestContextAttribute();
        if (requestContextAttribute != null) {
            view.setRequestContextAttribute(requestContextAttribute);
        }
        Boolean exposePathVariables = getExposePathVariables();
        if (exposePathVariables != null) {
            view.setExposePathVariables(exposePathVariables.booleanValue());
        }
        Boolean exposeContextBeansAsAttributes = getExposeContextBeansAsAttributes();
        if (exposeContextBeansAsAttributes != null) {
            view.setExposeContextBeansAsAttributes(exposeContextBeansAsAttributes.booleanValue());
        }
        String[] exposedContextBeanNames = getExposedContextBeanNames();
        if (exposedContextBeanNames != null) {
            view.setExposedContextBeanNames(exposedContextBeanNames);
        }
        return view;
    }

    protected View applyLifecycleMethods(String viewName, AbstractUrlBasedView view) {
        ApplicationContext context = getApplicationContext();
        if (context != null) {
            Object initialized = context.getAutowireCapableBeanFactory().initializeBean(view, viewName);
            if (initialized instanceof View) {
                View initializedView = (View) initialized;
                return initializedView;
            }
        }
        return view;
    }
}
