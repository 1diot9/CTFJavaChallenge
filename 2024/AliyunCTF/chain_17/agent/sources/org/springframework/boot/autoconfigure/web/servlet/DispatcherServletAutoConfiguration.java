package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

@AutoConfiguration(after = {ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnClass({DispatcherServlet.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletAutoConfiguration.class */
public class DispatcherServletAutoConfiguration {
    public static final String DEFAULT_DISPATCHER_SERVLET_BEAN_NAME = "dispatcherServlet";
    public static final String DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME = "dispatcherServletRegistration";

    @EnableConfigurationProperties({WebMvcProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ServletRegistration.class})
    @Conditional({DefaultDispatcherServletCondition.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletAutoConfiguration$DispatcherServletConfiguration.class */
    protected static class DispatcherServletConfiguration {
        protected DispatcherServletConfiguration() {
        }

        @Bean(name = {"dispatcherServlet"})
        public DispatcherServlet dispatcherServlet(WebMvcProperties webMvcProperties) {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setDispatchOptionsRequest(webMvcProperties.isDispatchOptionsRequest());
            dispatcherServlet.setDispatchTraceRequest(webMvcProperties.isDispatchTraceRequest());
            configureThrowExceptionIfNoHandlerFound(webMvcProperties, dispatcherServlet);
            dispatcherServlet.setPublishEvents(webMvcProperties.isPublishRequestHandledEvents());
            dispatcherServlet.setEnableLoggingRequestDetails(webMvcProperties.isLogRequestDetails());
            return dispatcherServlet;
        }

        private void configureThrowExceptionIfNoHandlerFound(WebMvcProperties webMvcProperties, DispatcherServlet dispatcherServlet) {
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(webMvcProperties.isThrowExceptionIfNoHandlerFound());
        }

        @ConditionalOnMissingBean(name = {DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME})
        @ConditionalOnBean({MultipartResolver.class})
        @Bean
        public MultipartResolver multipartResolver(MultipartResolver resolver) {
            return resolver;
        }
    }

    @EnableConfigurationProperties({WebMvcProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ServletRegistration.class})
    @Conditional({DispatcherServletRegistrationCondition.class})
    @Import({DispatcherServletConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletAutoConfiguration$DispatcherServletRegistrationConfiguration.class */
    protected static class DispatcherServletRegistrationConfiguration {
        protected DispatcherServletRegistrationConfiguration() {
        }

        @ConditionalOnBean(value = {DispatcherServlet.class}, name = {"dispatcherServlet"})
        @Bean(name = {DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME})
        public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet, WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
            DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet, webMvcProperties.getServlet().getPath());
            registration.setName("dispatcherServlet");
            registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
            Objects.requireNonNull(registration);
            multipartConfig.ifAvailable(registration::setMultipartConfig);
            return registration;
        }
    }

    @Order(2147483637)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletAutoConfiguration$DefaultDispatcherServletCondition.class */
    private static final class DefaultDispatcherServletCondition extends SpringBootCondition {
        private DefaultDispatcherServletCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("Default DispatcherServlet", new Object[0]);
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            List<String> dispatchServletBeans = Arrays.asList(beanFactory.getBeanNamesForType(DispatcherServlet.class, false, false));
            if (dispatchServletBeans.contains("dispatcherServlet")) {
                return ConditionOutcome.noMatch(message.found("dispatcher servlet bean").items("dispatcherServlet"));
            }
            if (beanFactory.containsBean("dispatcherServlet")) {
                return ConditionOutcome.noMatch(message.found("non dispatcher servlet bean").items("dispatcherServlet"));
            }
            if (dispatchServletBeans.isEmpty()) {
                return ConditionOutcome.match(message.didNotFind("dispatcher servlet beans").atAll());
            }
            return ConditionOutcome.match(message.found("dispatcher servlet bean", "dispatcher servlet beans").items(ConditionMessage.Style.QUOTE, dispatchServletBeans).append("and none is named dispatcherServlet"));
        }
    }

    @Order(2147483637)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletAutoConfiguration$DispatcherServletRegistrationCondition.class */
    private static final class DispatcherServletRegistrationCondition extends SpringBootCondition {
        private DispatcherServletRegistrationCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            ConditionOutcome outcome = checkDefaultDispatcherName(beanFactory);
            if (!outcome.isMatch()) {
                return outcome;
            }
            return checkServletRegistration(beanFactory);
        }

        private ConditionOutcome checkDefaultDispatcherName(ConfigurableListableBeanFactory beanFactory) {
            boolean containsDispatcherBean = beanFactory.containsBean("dispatcherServlet");
            if (!containsDispatcherBean) {
                return ConditionOutcome.match();
            }
            List<String> servlets = Arrays.asList(beanFactory.getBeanNamesForType(DispatcherServlet.class, false, false));
            if (!servlets.contains("dispatcherServlet")) {
                return ConditionOutcome.noMatch(startMessage().found("non dispatcher servlet").items("dispatcherServlet"));
            }
            return ConditionOutcome.match();
        }

        private ConditionOutcome checkServletRegistration(ConfigurableListableBeanFactory beanFactory) {
            ConditionMessage.Builder message = startMessage();
            List<String> registrations = Arrays.asList(beanFactory.getBeanNamesForType(ServletRegistrationBean.class, false, false));
            boolean containsDispatcherRegistrationBean = beanFactory.containsBean(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
            if (registrations.isEmpty()) {
                if (containsDispatcherRegistrationBean) {
                    return ConditionOutcome.noMatch(message.found("non servlet registration bean").items(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME));
                }
                return ConditionOutcome.match(message.didNotFind("servlet registration bean").atAll());
            }
            if (registrations.contains(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)) {
                return ConditionOutcome.noMatch(message.found("servlet registration bean").items(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME));
            }
            if (containsDispatcherRegistrationBean) {
                return ConditionOutcome.noMatch(message.found("non servlet registration bean").items(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME));
            }
            return ConditionOutcome.match(message.found("servlet registration beans").items(ConditionMessage.Style.QUOTE, registrations).append("and none is named dispatcherServletRegistration"));
        }

        private ConditionMessage.Builder startMessage() {
            return ConditionMessage.forCondition("DispatcherServlet Registration", new Object[0]);
        }
    }
}
