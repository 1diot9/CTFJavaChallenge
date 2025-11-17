package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.el.VariableResolver;
import java.io.IOException;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/EvalTag.class */
public class EvalTag extends HtmlEscapingAwareTag {
    private static final String EVALUATION_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.EVALUATION_CONTEXT";

    @Nullable
    private Expression expression;

    @Nullable
    private String var;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private int scope = 1;
    private boolean javaScriptEscape = false;

    public void setExpression(String expression) {
        this.expression = this.expressionParser.parseExpression(expression);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = TagUtils.getScope(scope);
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override // org.springframework.web.servlet.tags.RequestContextAwareTag
    public int doStartTagInternal() throws JspException {
        return 1;
    }

    public int doEndTag() throws JspException {
        EvaluationContext evaluationContext = (EvaluationContext) this.pageContext.getAttribute(EVALUATION_CONTEXT_PAGE_ATTRIBUTE);
        if (evaluationContext == null) {
            evaluationContext = createEvaluationContext(this.pageContext);
            this.pageContext.setAttribute(EVALUATION_CONTEXT_PAGE_ATTRIBUTE, evaluationContext);
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, this.expression != null ? this.expression.getValue(evaluationContext) : null, this.scope);
            return 6;
        }
        try {
            String result = htmlEscape(ObjectUtils.getDisplayString(this.expression != null ? (String) this.expression.getValue(evaluationContext, String.class) : null));
            this.pageContext.getOut().print(this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(result) : result);
            return 6;
        } catch (IOException ex) {
            throw new JspException(ex);
        }
    }

    private EvaluationContext createEvaluationContext(PageContext pageContext) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new JspPropertyAccessor(pageContext));
        context.addPropertyAccessor(new MapAccessor());
        context.addPropertyAccessor(new EnvironmentAccessor());
        context.setBeanResolver(new BeanFactoryResolver(getRequestContext().getWebApplicationContext()));
        ConversionService conversionService = getConversionService(pageContext);
        if (conversionService != null) {
            context.setTypeConverter(new StandardTypeConverter(conversionService));
        }
        return context;
    }

    @Nullable
    private ConversionService getConversionService(PageContext pageContext) {
        return (ConversionService) pageContext.getRequest().getAttribute(ConversionService.class.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/EvalTag$JspPropertyAccessor.class */
    public static class JspPropertyAccessor implements PropertyAccessor {
        private final PageContext pageContext;

        @Nullable
        private final VariableResolver variableResolver;

        public JspPropertyAccessor(PageContext pageContext) {
            this.pageContext = pageContext;
            this.variableResolver = pageContext.getVariableResolver();
        }

        @Override // org.springframework.expression.PropertyAccessor
        @Nullable
        public Class<?>[] getSpecificTargetClasses() {
            return null;
        }

        @Override // org.springframework.expression.PropertyAccessor
        public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            return target == null && !(resolveImplicitVariable(name) == null && this.pageContext.findAttribute(name) == null);
        }

        @Override // org.springframework.expression.PropertyAccessor
        public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            Object implicitVar = resolveImplicitVariable(name);
            if (implicitVar != null) {
                return new TypedValue(implicitVar);
            }
            return new TypedValue(this.pageContext.findAttribute(name));
        }

        @Override // org.springframework.expression.PropertyAccessor
        public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
            return false;
        }

        @Override // org.springframework.expression.PropertyAccessor
        public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        private Object resolveImplicitVariable(String name) throws AccessException {
            if (this.variableResolver == null) {
                return null;
            }
            try {
                return this.variableResolver.resolveVariable(name);
            } catch (Exception ex) {
                throw new AccessException("Unexpected exception occurred accessing '" + name + "' as an implicit variable", ex);
            }
        }
    }
}
