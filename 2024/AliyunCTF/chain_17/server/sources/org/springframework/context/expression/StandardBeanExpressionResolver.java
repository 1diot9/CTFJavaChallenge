package org.springframework.context.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/StandardBeanExpressionResolver.class */
public class StandardBeanExpressionResolver implements BeanExpressionResolver {
    public static final String MAX_SPEL_EXPRESSION_LENGTH_PROPERTY_NAME = "spring.context.expression.maxLength";
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";
    private String expressionPrefix;
    private String expressionSuffix;
    private ExpressionParser expressionParser;
    private final Map<String, Expression> expressionCache;
    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache;
    private final ParserContext beanExpressionParserContext;

    public StandardBeanExpressionResolver() {
        this(null);
    }

    public StandardBeanExpressionResolver(@Nullable ClassLoader beanClassLoader) {
        this.expressionPrefix = DEFAULT_EXPRESSION_PREFIX;
        this.expressionSuffix = "}";
        this.expressionCache = new ConcurrentHashMap(256);
        this.evaluationCache = new ConcurrentHashMap(8);
        this.beanExpressionParserContext = new ParserContext() { // from class: org.springframework.context.expression.StandardBeanExpressionResolver.1
            @Override // org.springframework.expression.ParserContext
            public boolean isTemplate() {
                return true;
            }

            @Override // org.springframework.expression.ParserContext
            public String getExpressionPrefix() {
                return StandardBeanExpressionResolver.this.expressionPrefix;
            }

            @Override // org.springframework.expression.ParserContext
            public String getExpressionSuffix() {
                return StandardBeanExpressionResolver.this.expressionSuffix;
            }
        };
        SpelParserConfiguration parserConfig = new SpelParserConfiguration(null, beanClassLoader, false, false, Integer.MAX_VALUE, retrieveMaxExpressionLength());
        this.expressionParser = new SpelExpressionParser(parserConfig);
    }

    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }

    @Override // org.springframework.beans.factory.config.BeanExpressionResolver
    @Nullable
    public Object evaluate(@Nullable String value, BeanExpressionContext beanExpressionContext) throws BeansException {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
        try {
            Expression expr = this.expressionCache.get(value);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                this.expressionCache.put(value, expr);
            }
            StandardEvaluationContext sec = this.evaluationCache.get(beanExpressionContext);
            if (sec == null) {
                sec = new StandardEvaluationContext(beanExpressionContext);
                sec.addPropertyAccessor(new BeanExpressionContextAccessor());
                sec.addPropertyAccessor(new BeanFactoryAccessor());
                sec.addPropertyAccessor(new MapAccessor());
                sec.addPropertyAccessor(new EnvironmentAccessor());
                sec.setBeanResolver(new BeanFactoryResolver(beanExpressionContext.getBeanFactory()));
                sec.setTypeLocator(new StandardTypeLocator(beanExpressionContext.getBeanFactory().getBeanClassLoader()));
                sec.setTypeConverter(new StandardTypeConverter((Supplier<ConversionService>) () -> {
                    ConversionService cs = beanExpressionContext.getBeanFactory().getConversionService();
                    return cs != null ? cs : DefaultConversionService.getSharedInstance();
                }));
                customizeEvaluationContext(sec);
                this.evaluationCache.put(beanExpressionContext, sec);
            }
            return expr.getValue((EvaluationContext) sec);
        } catch (Throwable ex) {
            throw new BeanExpressionException("Expression parsing failed", ex);
        }
    }

    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
    }

    private static int retrieveMaxExpressionLength() {
        String value = SpringProperties.getProperty(MAX_SPEL_EXPRESSION_LENGTH_PROPERTY_NAME);
        if (!StringUtils.hasText(value)) {
            return 10000;
        }
        try {
            int maxLength = Integer.parseInt(value.trim());
            Assert.isTrue(maxLength > 0, (Supplier<String>) () -> {
                return "Value [" + maxLength + "] for system property [spring.context.expression.maxLength] must be positive";
            });
            return maxLength;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Failed to parse value for system property [spring.context.expression.maxLength]: " + ex.getMessage(), ex);
        }
    }
}
