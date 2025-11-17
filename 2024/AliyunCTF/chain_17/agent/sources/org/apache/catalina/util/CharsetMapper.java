package org.apache.catalina.util;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.compat.JreCompat;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/CharsetMapper.class */
public class CharsetMapper {
    public static final String DEFAULT_RESOURCE = "/org/apache/catalina/util/CharsetMapperDefault.properties";
    private Properties map;

    public CharsetMapper() {
        this(DEFAULT_RESOURCE);
    }

    public CharsetMapper(String name) {
        this.map = new Properties();
        if (JreCompat.isGraalAvailable()) {
            this.map.put("en", "ISO-8859-1");
            return;
        }
        try {
            InputStream stream = getClass().getResourceAsStream(name);
            try {
                this.map.load(stream);
                if (stream != null) {
                    stream.close();
                }
            } finally {
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            throw new IllegalArgumentException(t);
        }
    }

    public String getCharset(Locale locale) {
        String charset = this.map.getProperty(locale.toString());
        if (charset == null) {
            charset = this.map.getProperty(locale.getLanguage() + "_" + locale.getCountry());
            if (charset == null) {
                charset = this.map.getProperty(locale.getLanguage());
            }
        }
        return charset;
    }

    public void addCharsetMappingFromDeploymentDescriptor(String locale, String charset) {
        this.map.put(locale, charset);
    }
}
