package org.springframework.web.servlet.view.groovy;

import groovy.text.Template;
import groovy.text.markup.MarkupTemplateEngine;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractTemplateView;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/groovy/GroovyMarkupView.class */
public class GroovyMarkupView extends AbstractTemplateView {

    @Nullable
    private MarkupTemplateEngine engine;

    public void setTemplateEngine(MarkupTemplateEngine engine) {
        this.engine = engine;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.WebApplicationObjectSupport, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext();
        if (this.engine == null) {
            setTemplateEngine(autodetectMarkupTemplateEngine());
        }
    }

    protected MarkupTemplateEngine autodetectMarkupTemplateEngine() throws BeansException {
        try {
            return ((GroovyMarkupConfig) BeanFactoryUtils.beanOfTypeIncludingAncestors(obtainApplicationContext(), GroovyMarkupConfig.class, true, false)).getTemplateEngine();
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Expected a single GroovyMarkupConfig bean in the current Servlet web application context or the parent root context: GroovyMarkupConfigurer is the usual implementation. This bean may have any name.", ex);
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractUrlBasedView
    public boolean checkResource(Locale locale) throws Exception {
        Assert.state(this.engine != null, "No MarkupTemplateEngine set");
        try {
            this.engine.resolveTemplate(getUrl());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateView
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = getUrl();
        Assert.state(url != null, "'url' not set");
        Template template = getTemplate(url);
        template.make(model).writeTo(new BufferedWriter(response.getWriter()));
    }

    protected Template getTemplate(String viewUrl) throws Exception {
        Assert.state(this.engine != null, "No MarkupTemplateEngine set");
        try {
            return this.engine.createTemplateByPath(viewUrl);
        } catch (ClassNotFoundException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            throw new ServletException("Could not find class while rendering Groovy Markup view with name '" + getUrl() + "': " + ex.getMessage() + "'", cause);
        }
    }
}
