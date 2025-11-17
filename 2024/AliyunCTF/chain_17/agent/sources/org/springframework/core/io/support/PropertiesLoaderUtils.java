package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.ResourceUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PropertiesLoaderUtils.class */
public abstract class PropertiesLoaderUtils {
    private static final String XML_FILE_EXTENSION = ".xml";

    public static Properties loadProperties(EncodedResource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    public static void fillProperties(Properties props, EncodedResource resource) throws IOException {
        fillProperties(props, resource, DefaultPropertiesPersister.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void fillProperties(Properties props, EncodedResource resource, PropertiesPersister persister) throws IOException {
        InputStream stream = null;
        Reader reader = null;
        try {
            String filename = resource.getResource().getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                stream = resource.getInputStream();
                persister.loadFromXml(props, stream);
            } else if (resource.requiresReader()) {
                reader = resource.getReader();
                persister.load(props, reader);
            } else {
                stream = resource.getInputStream();
                persister.load(props, stream);
            }
            if (stream != null) {
                stream.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (0 != 0) {
                stream.close();
            }
            if (0 != 0) {
                reader.close();
            }
            throw th;
        }
    }

    public static Properties loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }

    public static void fillProperties(Properties props, Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        try {
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
            if (is != null) {
                is.close();
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Properties loadAllProperties(String resourceName) throws IOException {
        return loadAllProperties(resourceName, null);
    }

    public static Properties loadAllProperties(String resourceName, @Nullable ClassLoader classLoader) throws IOException {
        Assert.notNull(resourceName, "Resource name must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = ClassUtils.getDefaultClassLoader();
        }
        Enumeration<URL> urls = classLoaderToUse != null ? classLoaderToUse.getResources(resourceName) : ClassLoader.getSystemResources(resourceName);
        Properties props = new Properties();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            URLConnection con = url.openConnection();
            ResourceUtils.useCachesIfNecessary(con);
            InputStream is = con.getInputStream();
            try {
                if (resourceName.endsWith(".xml")) {
                    props.loadFromXML(is);
                } else {
                    props.load(is);
                }
                if (is != null) {
                    is.close();
                }
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        return props;
    }
}
