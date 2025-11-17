package org.springframework.web.servlet.view.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerConfigurer.class */
public class FreeMarkerConfigurer extends FreeMarkerConfigurationFactory implements FreeMarkerConfig, InitializingBean, ResourceLoaderAware {

    @Nullable
    private Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IOException, TemplateException {
        if (this.configuration == null) {
            this.configuration = createConfiguration();
        }
    }

    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
    }

    @Override // org.springframework.web.servlet.view.freemarker.FreeMarkerConfig
    public Configuration getConfiguration() {
        Assert.state(this.configuration != null, "No Configuration available");
        return this.configuration;
    }
}
