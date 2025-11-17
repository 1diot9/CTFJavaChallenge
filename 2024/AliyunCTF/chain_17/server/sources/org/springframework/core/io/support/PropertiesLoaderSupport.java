package org.springframework.core.io.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PropertiesLoaderSupport.class */
public abstract class PropertiesLoaderSupport {

    @Nullable
    protected Properties[] localProperties;

    @Nullable
    private Resource[] locations;

    @Nullable
    private String fileEncoding;
    protected final Log logger = LogFactory.getLog(getClass());
    protected boolean localOverride = false;
    private boolean ignoreResourceNotFound = false;
    private PropertiesPersister propertiesPersister = DefaultPropertiesPersister.INSTANCE;

    public void setProperties(Properties properties) {
        this.localProperties = new Properties[]{properties};
    }

    public void setPropertiesArray(Properties... propertiesArray) {
        this.localProperties = propertiesArray;
    }

    public void setLocation(Resource location) {
        this.locations = new Resource[]{location};
    }

    public void setLocations(Resource... locations) {
        this.locations = locations;
    }

    public void setLocalOverride(boolean localOverride) {
        this.localOverride = localOverride;
    }

    public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
        this.ignoreResourceNotFound = ignoreResourceNotFound;
    }

    public void setFileEncoding(String encoding) {
        this.fileEncoding = encoding;
    }

    public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister != null ? propertiesPersister : DefaultPropertiesPersister.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Properties mergeProperties() throws IOException {
        Properties result = new Properties();
        if (this.localOverride) {
            loadProperties(result);
        }
        if (this.localProperties != null) {
            for (Properties localProp : this.localProperties) {
                CollectionUtils.mergePropertiesIntoMap(localProp, result);
            }
        }
        if (!this.localOverride) {
            loadProperties(result);
        }
        return result;
    }

    protected void loadProperties(Properties props) throws IOException {
        if (this.locations != null) {
            for (Resource location : this.locations) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Loading properties file from " + location);
                }
                try {
                    PropertiesLoaderUtils.fillProperties(props, new EncodedResource(location, this.fileEncoding), this.propertiesPersister);
                } catch (FileNotFoundException | SocketException | UnknownHostException ex) {
                    if (this.ignoreResourceNotFound) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Properties resource not found: " + ex.getMessage());
                        }
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }
}
