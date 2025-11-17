package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/MessageTag.class */
public class MessageTag extends HtmlEscapingAwareTag implements ArgumentAware {
    public static final String DEFAULT_ARGUMENT_SEPARATOR = ",";

    @Nullable
    private MessageSourceResolvable message;

    @Nullable
    private String code;

    @Nullable
    private Object arguments;

    @Nullable
    private String text;

    @Nullable
    private String var;
    private String argumentSeparator = ",";
    private List<Object> nestedArguments = Collections.emptyList();
    private String scope = TagUtils.SCOPE_PAGE;
    private boolean javaScriptEscape = false;

    public void setMessage(MessageSourceResolvable message) {
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }

    public void setArgumentSeparator(String argumentSeparator) {
        this.argumentSeparator = argumentSeparator;
    }

    @Override // org.springframework.web.servlet.tags.ArgumentAware
    public void addArgument(@Nullable Object argument) throws JspTagException {
        this.nestedArguments.add(argument);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    protected final int doStartTagInternal() throws JspException, IOException {
        this.nestedArguments = new ArrayList();
        return 1;
    }

    public int doEndTag() throws JspException {
        try {
            String msg = htmlEscape(resolveMessage());
            String msg2 = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(msg) : msg;
            if (this.var != null) {
                this.pageContext.setAttribute(this.var, msg2, TagUtils.getScope(this.scope));
                return 6;
            }
            writeMessage(msg2);
            return 6;
        } catch (IOException ex) {
            throw new JspTagException(ex.getMessage(), ex);
        } catch (NoSuchMessageException ex2) {
            throw new JspTagException(getNoSuchMessageExceptionDescription(ex2));
        }
    }

    public void release() {
        super.release();
        this.arguments = null;
    }

    protected String resolveMessage() throws JspException, NoSuchMessageException {
        MessageSource messageSource = getMessageSource();
        if (this.message != null) {
            return messageSource.getMessage(this.message, getRequestContext().getLocale());
        }
        if (this.code != null || this.text != null) {
            Object[] argumentsArray = resolveArguments(this.arguments);
            if (!this.nestedArguments.isEmpty()) {
                argumentsArray = appendArguments(argumentsArray, this.nestedArguments.toArray());
            }
            if (this.text != null) {
                String msg = messageSource.getMessage(this.code, argumentsArray, this.text, getRequestContext().getLocale());
                return msg != null ? msg : "";
            }
            return messageSource.getMessage(this.code, argumentsArray, getRequestContext().getLocale());
        }
        throw new JspTagException("No resolvable message");
    }

    private Object[] appendArguments(@Nullable Object[] sourceArguments, Object[] additionalArguments) {
        if (ObjectUtils.isEmpty(sourceArguments)) {
            return additionalArguments;
        }
        Object[] arguments = new Object[sourceArguments.length + additionalArguments.length];
        System.arraycopy(sourceArguments, 0, arguments, 0, sourceArguments.length);
        System.arraycopy(additionalArguments, 0, arguments, sourceArguments.length, additionalArguments.length);
        return arguments;
    }

    @Nullable
    protected Object[] resolveArguments(@Nullable Object arguments) throws JspException {
        if (arguments instanceof String) {
            String string = (String) arguments;
            return StringUtils.delimitedListToStringArray(string, this.argumentSeparator);
        }
        if (arguments instanceof Object[]) {
            Object[] array = (Object[]) arguments;
            return array;
        }
        if (arguments instanceof Collection) {
            Collection<?> collection = (Collection) arguments;
            return collection.toArray();
        }
        if (arguments != null) {
            return new Object[]{arguments};
        }
        return null;
    }

    protected void writeMessage(String msg) throws IOException {
        this.pageContext.getOut().write(msg);
    }

    protected MessageSource getMessageSource() {
        return getRequestContext().getMessageSource();
    }

    protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
        return ex.getMessage();
    }
}
