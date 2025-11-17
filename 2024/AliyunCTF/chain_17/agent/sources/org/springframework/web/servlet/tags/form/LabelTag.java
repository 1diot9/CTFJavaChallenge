package org.springframework.web.servlet.tags.form;

import jakarta.servlet.jsp.JspException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/LabelTag.class */
public class LabelTag extends AbstractHtmlElementTag {
    private static final String LABEL_TAG = "label";
    private static final String FOR_ATTRIBUTE = "for";

    @Nullable
    private TagWriter tagWriter;

    @Nullable
    private String forId;

    public void setFor(String forId) {
        this.forId = forId;
    }

    @Nullable
    protected String getFor() {
        return this.forId;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractFormTag
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        tagWriter.startTag(LABEL_TAG);
        tagWriter.writeAttribute(FOR_ATTRIBUTE, resolveFor());
        writeDefaultAttributes(tagWriter);
        tagWriter.forceBlock();
        this.tagWriter = tagWriter;
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag
    @Nullable
    public String getName() throws JspException {
        return null;
    }

    protected String resolveFor() throws JspException {
        if (StringUtils.hasText(this.forId)) {
            return getDisplayString(evaluate(FOR_ATTRIBUTE, this.forId));
        }
        return autogenerateFor();
    }

    protected String autogenerateFor() throws JspException {
        return StringUtils.deleteAny(getPropertyPath(), ClassUtils.ARRAY_SUFFIX);
    }

    public int doEndTag() throws JspException {
        Assert.state(this.tagWriter != null, "No TagWriter set");
        this.tagWriter.endTag();
        return 6;
    }

    @Override // org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag, org.springframework.web.servlet.tags.RequestContextAwareTag
    public void doFinally() {
        super.doFinally();
        this.tagWriter = null;
    }
}
