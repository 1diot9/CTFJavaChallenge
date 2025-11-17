package org.springframework.boot.autoconfigure.h2;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.log.LogMessage;

@EnableConfigurationProperties({H2ConsoleProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@ConditionalOnClass({JakartaWebServlet.class})
@ConditionalOnProperty(prefix = "spring.h2.console", name = {"enabled"}, havingValue = "true")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/h2/H2ConsoleAutoConfiguration.class */
public class H2ConsoleAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) H2ConsoleAutoConfiguration.class);

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2Console(H2ConsoleProperties properties, ObjectProvider<DataSource> dataSource) {
        String path = properties.getPath();
        String urlMapping = path + (path.endsWith("/") ? "*" : "/*");
        ServletRegistrationBean<JakartaWebServlet> registration = new ServletRegistrationBean<>(new JakartaWebServlet(), urlMapping);
        configureH2ConsoleSettings(registration, properties.getSettings());
        if (logger.isInfoEnabled()) {
            withThreadContextClassLoader(getClass().getClassLoader(), () -> {
                logDataSources(dataSource, path);
            });
        }
        return registration;
    }

    private void withThreadContextClassLoader(ClassLoader classLoader, Runnable action) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            action.run();
            Thread.currentThread().setContextClassLoader(previous);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(previous);
            throw th;
        }
    }

    private void logDataSources(ObjectProvider<DataSource> dataSource, String path) {
        List<String> urls = dataSource.orderedStream().map(this::getConnectionUrl).filter((v0) -> {
            return Objects.nonNull(v0);
        }).toList();
        if (!urls.isEmpty()) {
            logger.info(LogMessage.format("H2 console available at '%s'. %s available at %s", path, urls.size() > 1 ? "Databases" : "Database", String.join(", ", urls)));
        }
    }

    private String getConnectionUrl(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                String str = "'" + connection.getMetaData().getURL() + "'";
                if (connection != null) {
                    connection.close();
                }
                return str;
            } finally {
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void configureH2ConsoleSettings(ServletRegistrationBean<JakartaWebServlet> registration, H2ConsoleProperties.Settings settings) {
        if (settings.isTrace()) {
            registration.addInitParameter("trace", "");
        }
        if (settings.isWebAllowOthers()) {
            registration.addInitParameter("webAllowOthers", "");
        }
        if (settings.getWebAdminPassword() != null) {
            registration.addInitParameter("webAdminPassword", settings.getWebAdminPassword());
        }
    }
}
