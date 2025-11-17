package org.springframework.boot.autoconfigure.info;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

@EnableConfigurationProperties({ProjectInfoProperties.class})
@AutoConfiguration
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/info/ProjectInfoAutoConfiguration.class */
public class ProjectInfoAutoConfiguration {
    private final ProjectInfoProperties properties;

    public ProjectInfoAutoConfiguration(ProjectInfoProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Conditional({GitResourceAvailableCondition.class})
    @Bean
    public GitProperties gitProperties() throws Exception {
        return new GitProperties(loadFrom(this.properties.getGit().getLocation(), "git", this.properties.getGit().getEncoding()));
    }

    @ConditionalOnMissingBean
    @ConditionalOnResource(resources = {"${spring.info.build.location:classpath:META-INF/build-info.properties}"})
    @Bean
    public BuildProperties buildProperties() throws Exception {
        return new BuildProperties(loadFrom(this.properties.getBuild().getLocation(), JsonPOJOBuilder.DEFAULT_BUILD_METHOD, this.properties.getBuild().getEncoding()));
    }

    protected Properties loadFrom(Resource location, String prefix, Charset encoding) throws IOException {
        String prefix2 = prefix.endsWith(".") ? prefix : prefix + ".";
        Properties source = loadSource(location, encoding);
        Properties target = new Properties();
        for (String key : source.stringPropertyNames()) {
            if (key.startsWith(prefix2)) {
                target.put(key.substring(prefix2.length()), source.get(key));
            }
        }
        return target;
    }

    private Properties loadSource(Resource location, Charset encoding) throws IOException {
        if (encoding != null) {
            return PropertiesLoaderUtils.loadProperties(new EncodedResource(location, encoding));
        }
        return PropertiesLoaderUtils.loadProperties(location);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/info/ProjectInfoAutoConfiguration$GitResourceAvailableCondition.class */
    static class GitResourceAvailableCondition extends SpringBootCondition {
        GitResourceAvailableCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ResourceLoader loader = context.getResourceLoader();
            Environment environment = context.getEnvironment();
            String location = environment.getProperty("spring.info.git.location");
            if (location == null) {
                location = "classpath:git.properties";
            }
            ConditionMessage.Builder message = ConditionMessage.forCondition("GitResource", new Object[0]);
            return loader.getResource(location).exists() ? ConditionOutcome.match(message.found("git info at").items(location)) : ConditionOutcome.noMatch(message.didNotFind("git info at").items(location));
        }
    }
}
