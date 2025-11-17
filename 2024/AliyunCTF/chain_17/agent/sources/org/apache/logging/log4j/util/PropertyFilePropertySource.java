package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertyFilePropertySource.class */
public class PropertyFilePropertySource extends PropertiesPropertySource {
    public PropertyFilePropertySource(final String fileName) {
        this(fileName, true);
    }

    public PropertyFilePropertySource(final String fileName, final boolean useTccl) {
        super(loadPropertiesFile(fileName, useTccl));
    }

    private static Properties loadPropertiesFile(final String fileName, final boolean useTccl) {
        Properties props = new Properties();
        for (URL url : LoaderUtil.findResources(fileName, useTccl)) {
            try {
                InputStream in = url.openStream();
                try {
                    props.load(in);
                    if (in != null) {
                        in.close();
                    }
                } catch (Throwable th) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                    break;
                }
            } catch (IOException e) {
                LowLevelLogUtil.logException("Unable to read " + url, e);
            }
        }
        return props;
    }
}
