package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.JavaScriptUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/EscapeBodyTag.class */
public class EscapeBodyTag extends HtmlEscapingAwareTag implements BodyTag {
    private boolean javaScriptEscape = false;

    @Nullable
    private BodyContent bodyContent;

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected int doStartTagInternal() {
        return 2;
    }

    public void doInitBody() {
    }

    public void setBodyContent(BodyContent bodyContent) {
        this.bodyContent = bodyContent;
    }

    public int doAfterBody() throws JspException {
        try {
            String content = htmlEscape(readBodyContent());
            writeBodyContent(this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(content) : content);
            return 0;
        } catch (IOException ex) {
            throw new JspException("Could not write escaped body", ex);
        }
    }

    protected String readBodyContent() throws IOException {
        Assert.state(this.bodyContent != null, "No BodyContent set");
        return this.bodyContent.getString();
    }

    protected void writeBodyContent(String content) throws IOException {
        Assert.state(this.bodyContent != null, "No BodyContent set");
        this.bodyContent.getEnclosingWriter().print(content);
    }
}
