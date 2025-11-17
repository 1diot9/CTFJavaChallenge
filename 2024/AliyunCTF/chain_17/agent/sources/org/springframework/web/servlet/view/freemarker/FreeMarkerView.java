package org.springframework.web.servlet.view.freemarker;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerView.class */
public class FreeMarkerView extends AbstractTemplateView {

    @Nullable
    private String encoding;

    @Nullable
    private Configuration configuration;

    public void setEncoding(@Nullable String encoding) {
        this.encoding = encoding;
    }

    @Nullable
    protected String getEncoding() {
        return this.encoding;
    }

    public void setConfiguration(@Nullable Configuration configuration) {
        this.configuration = configuration;
    }

    @Nullable
    protected Configuration getConfiguration() {
        return this.configuration;
    }

    protected Configuration obtainConfiguration() {
        Configuration configuration = getConfiguration();
        Assert.state(configuration != null, "No Configuration set");
        return configuration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.WebApplicationObjectSupport
    public void initServletContext(ServletContext servletContext) throws BeansException {
        if (getConfiguration() == null) {
            FreeMarkerConfig config = autodetectConfiguration();
            setConfiguration(config.getConfiguration());
        }
    }

    protected FreeMarkerConfig autodetectConfiguration() throws BeansException {
        try {
            return (FreeMarkerConfig) BeanFactoryUtils.beanOfTypeIncludingAncestors(obtainApplicationContext(), FreeMarkerConfig.class, true, false);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Must define a single FreeMarkerConfig bean in this web application context (may be inherited): FreeMarkerConfigurer is the usual implementation. This bean may be given any name.", ex);
        }
    }

    protected ObjectWrapper getObjectWrapper() {
        ObjectWrapper ow = obtainConfiguration().getObjectWrapper();
        return ow != null ? ow : new DefaultObjectWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
    }

    @Override // org.springframework.web.servlet.view.AbstractUrlBasedView
    public boolean checkResource(Locale locale) throws Exception {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        try {
            getTemplate(url, locale);
            return true;
        } catch (ParseException ex) {
            throw new ApplicationContextException("Failed to parse [" + url + "]", ex);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException ex2) {
            throw new ApplicationContextException("Failed to load [" + url + "]", ex2);
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateView
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposeHelpers(model, request);
        doRender(model, request, response);
    }

    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
    }

    protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposeModelAsRequestAttributes(model, request);
        SimpleHash fmModel = buildTemplateModel(model, request, response);
        Locale locale = RequestContextUtils.getLocale(request);
        processTemplate(getTemplate(locale), fmModel, response);
    }

    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        SimpleHash fmModel = new RequestHashModel(getObjectWrapper(), request);
        fmModel.putAll(model);
        return fmModel;
    }

    protected Template getTemplate(Locale locale) throws IOException {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        return getTemplate(url, locale);
    }

    protected Template getTemplate(String name, Locale locale) throws IOException {
        if (getEncoding() != null) {
            return obtainConfiguration().getTemplate(name, locale, getEncoding());
        }
        return obtainConfiguration().getTemplate(name, locale);
    }

    protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) throws IOException, TemplateException {
        template.process(model, response.getWriter());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerView$RequestHashModel.class */
    public static class RequestHashModel extends SimpleHash {
        private final HttpServletRequest request;

        public RequestHashModel(ObjectWrapper wrapper, HttpServletRequest request) {
            super(wrapper);
            this.request = request;
        }

        public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel model = super.get(key);
            if (model != null) {
                return model;
            }
            Object obj = this.request.getAttribute(key);
            if (obj != null) {
                return wrap(obj);
            }
            return wrap(null);
        }
    }
}
