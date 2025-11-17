package org.springframework.http.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/CodecConfigurerFactory.class */
final class CodecConfigurerFactory {
    private static final String DEFAULT_CONFIGURERS_PATH = "CodecConfigurer.properties";
    private static final Map<Class<?>, Class<?>> defaultCodecConfigurers = new HashMap(4);

    static {
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(DEFAULT_CONFIGURERS_PATH, (Class<?>) CodecConfigurerFactory.class));
            for (String ifcName : props.stringPropertyNames()) {
                String implName = props.getProperty(ifcName);
                Class<?> ifc = ClassUtils.forName(ifcName, CodecConfigurerFactory.class.getClassLoader());
                Class<?> impl = ClassUtils.forName(implName, CodecConfigurerFactory.class.getClassLoader());
                defaultCodecConfigurers.put(ifc, impl);
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private CodecConfigurerFactory() {
    }

    public static <T extends CodecConfigurer> T create(Class<T> ifc) {
        Class<?> impl = defaultCodecConfigurers.get(ifc);
        if (impl == null) {
            throw new IllegalStateException("No default codec configurer found for " + ifc);
        }
        return (T) BeanUtils.instantiateClass(impl);
    }
}
