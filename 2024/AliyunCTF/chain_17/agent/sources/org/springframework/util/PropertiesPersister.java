package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/PropertiesPersister.class */
public interface PropertiesPersister {
    void load(Properties props, InputStream is) throws IOException;

    void load(Properties props, Reader reader) throws IOException;

    void store(Properties props, OutputStream os, String header) throws IOException;

    void store(Properties props, Writer writer, String header) throws IOException;

    void loadFromXml(Properties props, InputStream is) throws IOException;

    void storeToXml(Properties props, OutputStream os, String header) throws IOException;

    void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException;
}
