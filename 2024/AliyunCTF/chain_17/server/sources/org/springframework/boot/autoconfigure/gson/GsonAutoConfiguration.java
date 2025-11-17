package org.springframework.boot.autoconfigure.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@EnableConfigurationProperties({GsonProperties.class})
@AutoConfiguration
@ConditionalOnClass({Gson.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration.class */
public class GsonAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) {
        GsonBuilder builder = new GsonBuilder();
        customizers.forEach(c -> {
            c.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean
    @Bean
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
    }

    @Bean
    public StandardGsonBuilderCustomizer standardGsonBuilderCustomizer(GsonProperties gsonProperties) {
        return new StandardGsonBuilderCustomizer(gsonProperties);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration$StandardGsonBuilderCustomizer.class */
    static final class StandardGsonBuilderCustomizer implements GsonBuilderCustomizer, Ordered {
        private final GsonProperties properties;

        StandardGsonBuilderCustomizer(GsonProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }

        @Override // org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer
        public void customize(GsonBuilder builder) {
            GsonProperties properties = this.properties;
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(properties);
            PropertyMapper.Source from = map.from(properties::getGenerateNonExecutableJson);
            Objects.requireNonNull(builder);
            from.toCall(builder::generateNonExecutableJson);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from2 = map.from(properties::getExcludeFieldsWithoutExposeAnnotation);
            Objects.requireNonNull(builder);
            from2.toCall(builder::excludeFieldsWithoutExposeAnnotation);
            Objects.requireNonNull(properties);
            PropertyMapper.Source whenTrue = map.from(properties::getSerializeNulls).whenTrue();
            Objects.requireNonNull(builder);
            whenTrue.toCall(builder::serializeNulls);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from3 = map.from(properties::getEnableComplexMapKeySerialization);
            Objects.requireNonNull(builder);
            from3.toCall(builder::enableComplexMapKeySerialization);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from4 = map.from(properties::getDisableInnerClassSerialization);
            Objects.requireNonNull(builder);
            from4.toCall(builder::disableInnerClassSerialization);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from5 = map.from(properties::getLongSerializationPolicy);
            Objects.requireNonNull(builder);
            from5.to(builder::setLongSerializationPolicy);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from6 = map.from(properties::getFieldNamingPolicy);
            Objects.requireNonNull(builder);
            from6.to(builder::setFieldNamingPolicy);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from7 = map.from(properties::getPrettyPrinting);
            Objects.requireNonNull(builder);
            from7.toCall(builder::setPrettyPrinting);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from8 = map.from(properties::getLenient);
            Objects.requireNonNull(builder);
            from8.toCall(builder::setLenient);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from9 = map.from(properties::getDisableHtmlEscaping);
            Objects.requireNonNull(builder);
            from9.toCall(builder::disableHtmlEscaping);
            Objects.requireNonNull(properties);
            PropertyMapper.Source from10 = map.from(properties::getDateFormat);
            Objects.requireNonNull(builder);
            from10.to(builder::setDateFormat);
        }
    }
}
