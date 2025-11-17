package org.springframework.boot.web.servlet.view;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractTemplateView;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/view/MustacheView.class */
public class MustacheView extends AbstractTemplateView {
    private Mustache.Compiler compiler;
    private String charset;

    public void setCompiler(Mustache.Compiler compiler) {
        this.compiler = compiler;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override // org.springframework.web.servlet.view.AbstractUrlBasedView
    public boolean checkResource(Locale locale) throws Exception {
        Resource resource = getApplicationContext().getResource(getUrl());
        return resource != null && resource.exists();
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateView
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Template template = createTemplate(getApplicationContext().getResource(getUrl()));
        if (template != null) {
            template.execute(model, response.getWriter());
        }
    }

    private Template createTemplate(Resource resource) throws IOException {
        Reader reader = getReader(resource);
        try {
            Template compile = this.compiler.compile(reader);
            if (reader != null) {
                reader.close();
            }
            return compile;
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private Reader getReader(Resource resource) throws IOException {
        if (this.charset != null) {
            return new InputStreamReader(resource.getInputStream(), this.charset);
        }
        return new InputStreamReader(resource.getInputStream());
    }
}
