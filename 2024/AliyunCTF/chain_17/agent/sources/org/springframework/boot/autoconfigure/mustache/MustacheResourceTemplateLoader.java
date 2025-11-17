package org.springframework.boot.autoconfigure.mustache;

import cn.hutool.core.util.CharsetUtil;
import com.samskivert.mustache.Mustache;
import java.io.InputStreamReader;
import java.io.Reader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mustache/MustacheResourceTemplateLoader.class */
public class MustacheResourceTemplateLoader implements Mustache.TemplateLoader, ResourceLoaderAware {
    private String prefix;
    private String suffix;
    private String charSet;
    private ResourceLoader resourceLoader;

    public MustacheResourceTemplateLoader() {
        this.prefix = "";
        this.suffix = "";
        this.charSet = CharsetUtil.UTF_8;
        this.resourceLoader = new DefaultResourceLoader(null);
    }

    public MustacheResourceTemplateLoader(String prefix, String suffix) {
        this.prefix = "";
        this.suffix = "";
        this.charSet = CharsetUtil.UTF_8;
        this.resourceLoader = new DefaultResourceLoader(null);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void setCharset(String charSet) {
        this.charSet = charSet;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Reader getTemplate(String name) throws Exception {
        return new InputStreamReader(this.resourceLoader.getResource(this.prefix + name + this.suffix).getInputStream(), this.charSet);
    }
}
