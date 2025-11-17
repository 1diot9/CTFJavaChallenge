package org.springframework.boot.autoconfigure.web.servlet.error;

import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.tags.BindTag;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.util.HtmlUtils;

@EnableConfigurationProperties({ServerProperties.class, WebMvcProperties.class})
@AutoConfiguration(before = {WebMvcAutoConfiguration.class})
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration.class */
public class ErrorMvcAutoConfiguration {
    private final ServerProperties serverProperties;

    public ErrorMvcAutoConfiguration(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @ConditionalOnMissingBean(value = {ErrorAttributes.class}, search = SearchStrategy.CURRENT)
    @Bean
    public DefaultErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }

    @ConditionalOnMissingBean(value = {ErrorController.class}, search = SearchStrategy.CURRENT)
    @Bean
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new BasicErrorController(errorAttributes, this.serverProperties.getError(), errorViewResolvers.orderedStream().toList());
    }

    @Bean
    public ErrorPageCustomizer errorPageCustomizer(DispatcherServletPath dispatcherServletPath) {
        return new ErrorPageCustomizer(this.serverProperties, dispatcherServletPath);
    }

    @Bean
    public static PreserveErrorControllerTargetClassPostProcessor preserveErrorControllerTargetClassPostProcessor() {
        return new PreserveErrorControllerTargetClassPostProcessor();
    }

    @EnableConfigurationProperties({WebProperties.class, WebMvcProperties.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$DefaultErrorViewResolverConfiguration.class */
    static class DefaultErrorViewResolverConfiguration {
        private final ApplicationContext applicationContext;
        private final WebProperties.Resources resources;

        DefaultErrorViewResolverConfiguration(ApplicationContext applicationContext, WebProperties webProperties) {
            this.applicationContext = applicationContext;
            this.resources = webProperties.getResources();
        }

        @ConditionalOnMissingBean({ErrorViewResolver.class})
        @ConditionalOnBean({DispatcherServlet.class})
        @Bean
        DefaultErrorViewResolver conventionErrorViewResolver() {
            return new DefaultErrorViewResolver(this.applicationContext, this.resources);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "server.error.whitelabel", name = {"enabled"}, matchIfMissing = true)
    @Conditional({ErrorTemplateMissingCondition.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$WhitelabelErrorViewConfiguration.class */
    protected static class WhitelabelErrorViewConfiguration {
        private final StaticView defaultErrorView = new StaticView();

        protected WhitelabelErrorViewConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"error"})
        @Bean(name = {"error"})
        public View defaultErrorView() {
            return this.defaultErrorView;
        }

        @ConditionalOnMissingBean
        @Bean
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(2147483637);
            return resolver;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$ErrorTemplateMissingCondition.class */
    private static final class ErrorTemplateMissingCondition extends SpringBootCondition {
        private ErrorTemplateMissingCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("ErrorTemplate Missing", new Object[0]);
            TemplateAvailabilityProviders providers = new TemplateAvailabilityProviders(context.getClassLoader());
            TemplateAvailabilityProvider provider = providers.getProvider("error", context.getEnvironment(), context.getClassLoader(), context.getResourceLoader());
            if (provider != null) {
                return ConditionOutcome.noMatch(message.foundExactly("template from " + provider));
            }
            return ConditionOutcome.match(message.didNotFind("error template view").atAll());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$StaticView.class */
    private static final class StaticView implements View {
        private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);
        private static final Log logger = LogFactory.getLog((Class<?>) StaticView.class);

        private StaticView() {
        }

        @Override // org.springframework.web.servlet.View
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (response.isCommitted()) {
                logger.error(getMessage(model));
                return;
            }
            response.setContentType(TEXT_HTML_UTF8.toString());
            StringBuilder builder = new StringBuilder();
            Object timestamp = model.get(JsonEncoder.TIMESTAMP_ATTR_NAME);
            Object message = model.get(JsonEncoder.MESSAGE_ATTR_NAME);
            Object trace = model.get("trace");
            if (response.getContentType() == null) {
                response.setContentType(getContentType());
            }
            builder.append("<html><body><h1>Whitelabel Error Page</h1>").append("<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>").append("<div id='created'>").append(timestamp).append("</div>").append("<div>There was an unexpected error (type=").append(htmlEscape(model.get("error"))).append(", status=").append(htmlEscape(model.get(BindTag.STATUS_VARIABLE_NAME))).append(").</div>");
            if (message != null) {
                builder.append("<div>").append(htmlEscape(message)).append("</div>");
            }
            if (trace != null) {
                builder.append("<div style='white-space:pre-wrap;'>").append(htmlEscape(trace)).append("</div>");
            }
            builder.append("</body></html>");
            response.getWriter().append((CharSequence) builder.toString());
        }

        private String htmlEscape(Object input) {
            if (input != null) {
                return HtmlUtils.htmlEscape(input.toString());
            }
            return null;
        }

        private String getMessage(Map<String, ?> model) {
            Object path = model.get("path");
            String message = "Cannot render error page for request [" + path + "]";
            if (model.get(JsonEncoder.MESSAGE_ATTR_NAME) != null) {
                message = message + " and exception [" + model.get(JsonEncoder.MESSAGE_ATTR_NAME) + "]";
            }
            return (message + " as the response has already been committed.") + " As a result, the response may have the wrong status code.";
        }

        @Override // org.springframework.web.servlet.View
        public String getContentType() {
            return "text/html";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$ErrorPageCustomizer.class */
    static class ErrorPageCustomizer implements ErrorPageRegistrar, Ordered {
        private final ServerProperties properties;
        private final DispatcherServletPath dispatcherServletPath;

        protected ErrorPageCustomizer(ServerProperties properties, DispatcherServletPath dispatcherServletPath) {
            this.properties = properties;
            this.dispatcherServletPath = dispatcherServletPath;
        }

        @Override // org.springframework.boot.web.server.ErrorPageRegistrar
        public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
            ErrorPage errorPage = new ErrorPage(this.dispatcherServletPath.getRelativePath(this.properties.getError().getPath()));
            errorPageRegistry.addErrorPages(errorPage);
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/error/ErrorMvcAutoConfiguration$PreserveErrorControllerTargetClassPostProcessor.class */
    static class PreserveErrorControllerTargetClassPostProcessor implements BeanFactoryPostProcessor {
        PreserveErrorControllerTargetClassPostProcessor() {
        }

        @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            String[] errorControllerBeans = beanFactory.getBeanNamesForType(ErrorController.class, false, false);
            for (String errorControllerBean : errorControllerBeans) {
                try {
                    beanFactory.getBeanDefinition(errorControllerBean).setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
                } catch (Throwable th) {
                }
            }
        }
    }
}
