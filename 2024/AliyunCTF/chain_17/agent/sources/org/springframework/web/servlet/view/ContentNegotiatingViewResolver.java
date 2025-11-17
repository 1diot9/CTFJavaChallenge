package org.springframework.web.servlet.view;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/ContentNegotiatingViewResolver.class */
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered, InitializingBean {

    @Nullable
    private ContentNegotiationManager contentNegotiationManager;

    @Nullable
    private List<View> defaultViews;

    @Nullable
    private List<ViewResolver> viewResolvers;
    private static final View NOT_ACCEPTABLE_VIEW = new View() { // from class: org.springframework.web.servlet.view.ContentNegotiatingViewResolver.1
        @Override // org.springframework.web.servlet.View
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override // org.springframework.web.servlet.View
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(406);
        }
    };
    private final ContentNegotiationManagerFactoryBean cnmFactoryBean = new ContentNegotiationManagerFactoryBean();
    private boolean useNotAcceptableStatusCode = false;
    private int order = Integer.MIN_VALUE;

    public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable
    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setUseNotAcceptableStatusCode(boolean useNotAcceptableStatusCode) {
        this.useNotAcceptableStatusCode = useNotAcceptableStatusCode;
    }

    public boolean isUseNotAcceptableStatusCode() {
        return this.useNotAcceptableStatusCode;
    }

    public void setDefaultViews(List<View> defaultViews) {
        this.defaultViews = defaultViews;
    }

    public List<View> getDefaultViews() {
        return this.defaultViews != null ? Collections.unmodifiableList(this.defaultViews) : Collections.emptyList();
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    public List<ViewResolver> getViewResolvers() {
        return this.viewResolvers != null ? Collections.unmodifiableList(this.viewResolvers) : Collections.emptyList();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.WebApplicationObjectSupport
    public void initServletContext(ServletContext servletContext) {
        Collection<ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(obtainApplicationContext(), ViewResolver.class).values();
        if (this.viewResolvers == null) {
            this.viewResolvers = new ArrayList(matchingBeans.size());
            for (ViewResolver viewResolver : matchingBeans) {
                if (this != viewResolver) {
                    this.viewResolvers.add(viewResolver);
                }
            }
        } else {
            for (int i = 0; i < this.viewResolvers.size(); i++) {
                ViewResolver vr = this.viewResolvers.get(i);
                if (!matchingBeans.contains(vr)) {
                    String name = vr.getClass().getName() + i;
                    obtainApplicationContext().getAutowireCapableBeanFactory().initializeBean(vr, name);
                }
            }
        }
        AnnotationAwareOrderComparator.sort(this.viewResolvers);
        this.cnmFactoryBean.setServletContext(servletContext);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.contentNegotiationManager == null) {
            this.contentNegotiationManager = this.cnmFactoryBean.build();
        }
        if (this.viewResolvers == null || this.viewResolvers.isEmpty()) {
            this.logger.warn("No ViewResolvers configured");
        }
    }

    @Override // org.springframework.web.servlet.ViewResolver
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
        if (requestedMediaTypes != null) {
            List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
            View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);
            if (bestView != null) {
                return bestView;
            }
        }
        String mediaTypeInfo = (!this.logger.isDebugEnabled() || requestedMediaTypes == null) ? "" : " given " + requestedMediaTypes.toString();
        if (this.useNotAcceptableStatusCode) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
            }
            return NOT_ACCEPTABLE_VIEW;
        }
        this.logger.debug("View remains unresolved" + mediaTypeInfo);
        return null;
    }

    @Nullable
    protected List<MediaType> getMediaTypes(HttpServletRequest request) {
        Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
        try {
            ServletWebRequest webRequest = new ServletWebRequest(request);
            List<MediaType> acceptableMediaTypes = this.contentNegotiationManager.resolveMediaTypes(webRequest);
            List<MediaType> producibleMediaTypes = getProducibleMediaTypes(request);
            Set<MediaType> compatibleMediaTypes = new LinkedHashSet<>();
            for (MediaType acceptable : acceptableMediaTypes) {
                for (MediaType producible : producibleMediaTypes) {
                    if (acceptable.isCompatibleWith(producible)) {
                        compatibleMediaTypes.add(getMostSpecificMediaType(acceptable, producible));
                    }
                }
            }
            List<MediaType> selectedMediaTypes = new ArrayList<>(compatibleMediaTypes);
            MimeTypeUtils.sortBySpecificity(selectedMediaTypes);
            return selectedMediaTypes;
        } catch (HttpMediaTypeNotAcceptableException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(ex.getMessage());
                return null;
            }
            return null;
        }
    }

    private List<MediaType> getProducibleMediaTypes(HttpServletRequest request) {
        Set<MediaType> mediaTypes = (Set) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList(mediaTypes);
        }
        return Collections.singletonList(MediaType.ALL);
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceType2 = produceType.copyQualityValue(acceptType);
        if (acceptType.isLessSpecific(produceType2)) {
            return produceType2;
        }
        return acceptType;
    }

    private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes) throws Exception {
        List<View> candidateViews = new ArrayList<>();
        if (this.viewResolvers != null) {
            Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    candidateViews.add(view);
                }
                for (MediaType requestedMediaType : requestedMediaTypes) {
                    List<String> extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
                    for (String extension : extensions) {
                        String viewNameWithExtension = viewName + "." + extension;
                        View view2 = viewResolver.resolveViewName(viewNameWithExtension, locale);
                        if (view2 != null) {
                            candidateViews.add(view2);
                        }
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(this.defaultViews)) {
            candidateViews.addAll(this.defaultViews);
        }
        return candidateViews;
    }

    @Nullable
    private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes, RequestAttributes attrs) {
        for (View candidateView : candidateViews) {
            if (candidateView instanceof SmartView) {
                SmartView smartView = (SmartView) candidateView;
                if (smartView.isRedirectView()) {
                    return candidateView;
                }
            }
        }
        for (MediaType mediaType : requestedMediaTypes) {
            for (View candidateView2 : candidateViews) {
                if (StringUtils.hasText(candidateView2.getContentType())) {
                    MediaType candidateContentType = MediaType.parseMediaType(candidateView2.getContentType());
                    if (mediaType.isCompatibleWith(candidateContentType)) {
                        MediaType mediaType2 = mediaType.removeQualityValue();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Selected '" + mediaType2 + "' given " + requestedMediaTypes);
                        }
                        attrs.setAttribute(View.SELECTED_CONTENT_TYPE, mediaType2, 0);
                        return candidateView2;
                    }
                }
            }
        }
        return null;
    }
}
