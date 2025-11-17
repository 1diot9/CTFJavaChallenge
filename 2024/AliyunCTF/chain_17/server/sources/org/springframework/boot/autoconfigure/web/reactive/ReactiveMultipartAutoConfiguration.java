package org.springframework.boot.autoconfigure.web.reactive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.PartEventHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableConfigurationProperties({ReactiveMultipartProperties.class})
@AutoConfiguration
@ConditionalOnClass({DefaultPartHttpMessageReader.class, WebFluxConfigurer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveMultipartAutoConfiguration.class */
public class ReactiveMultipartAutoConfiguration {
    @Bean
    @Order(0)
    CodecCustomizer defaultPartHttpMessageReaderCustomizer(ReactiveMultipartProperties multipartProperties) {
        return configurer -> {
            configurer.defaultCodecs().configureDefaultCodec(codec -> {
                if (codec instanceof DefaultPartHttpMessageReader) {
                    DefaultPartHttpMessageReader defaultPartHttpMessageReader = (DefaultPartHttpMessageReader) codec;
                    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source<Integer> asInt = map.from(multipartProperties::getMaxInMemorySize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(defaultPartHttpMessageReader);
                    asInt.to((v1) -> {
                        r1.setMaxInMemorySize(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source<Integer> asInt2 = map.from(multipartProperties::getMaxHeadersSize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(defaultPartHttpMessageReader);
                    asInt2.to((v1) -> {
                        r1.setMaxHeadersSize(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source as = map.from(multipartProperties::getMaxDiskUsagePerPart).as((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(defaultPartHttpMessageReader);
                    as.to((v1) -> {
                        r1.setMaxDiskUsagePerPart(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source from = map.from(multipartProperties::getMaxParts);
                    Objects.requireNonNull(defaultPartHttpMessageReader);
                    from.to((v1) -> {
                        r1.setMaxParts(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    map.from(multipartProperties::getFileStorageDirectory).as(x$0 -> {
                        return Paths.get(x$0, new String[0]);
                    }).to(dir -> {
                        configureFileStorageDirectory(defaultPartHttpMessageReader, dir);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source from2 = map.from(multipartProperties::getHeadersCharset);
                    Objects.requireNonNull(defaultPartHttpMessageReader);
                    from2.to(defaultPartHttpMessageReader::setHeadersCharset);
                    return;
                }
                if (codec instanceof PartEventHttpMessageReader) {
                    PartEventHttpMessageReader partEventHttpMessageReader = (PartEventHttpMessageReader) codec;
                    PropertyMapper map2 = PropertyMapper.get().alwaysApplyingWhenNonNull();
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source<Integer> asInt3 = map2.from(multipartProperties::getMaxInMemorySize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(partEventHttpMessageReader);
                    asInt3.to((v1) -> {
                        r1.setMaxInMemorySize(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source<Integer> asInt4 = map2.from(multipartProperties::getMaxHeadersSize).asInt((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(partEventHttpMessageReader);
                    asInt4.to((v1) -> {
                        r1.setMaxHeadersSize(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source as2 = map2.from(multipartProperties::getMaxDiskUsagePerPart).as((v0) -> {
                        return v0.toBytes();
                    });
                    Objects.requireNonNull(partEventHttpMessageReader);
                    as2.to((v1) -> {
                        r1.setMaxPartSize(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source from3 = map2.from(multipartProperties::getMaxParts);
                    Objects.requireNonNull(partEventHttpMessageReader);
                    from3.to((v1) -> {
                        r1.setMaxParts(v1);
                    });
                    Objects.requireNonNull(multipartProperties);
                    PropertyMapper.Source from4 = map2.from(multipartProperties::getHeadersCharset);
                    Objects.requireNonNull(partEventHttpMessageReader);
                    from4.to(partEventHttpMessageReader::setHeadersCharset);
                }
            });
        };
    }

    private void configureFileStorageDirectory(DefaultPartHttpMessageReader defaultPartHttpMessageReader, Path fileStorageDirectory) {
        try {
            defaultPartHttpMessageReader.setFileStorageDirectory(fileStorageDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to configure multipart file storage directory", ex);
        }
    }
}
