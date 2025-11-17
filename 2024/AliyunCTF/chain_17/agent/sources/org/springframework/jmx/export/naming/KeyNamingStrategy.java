package org.springframework.jmx.export.naming;

import java.io.IOException;
import java.util.Properties;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/naming/KeyNamingStrategy.class */
public class KeyNamingStrategy implements ObjectNamingStrategy, InitializingBean {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Properties mappings;

    @Nullable
    private Resource[] mappingLocations;

    @Nullable
    private Properties mergedMappings;

    public void setMappings(Properties mappings) {
        this.mappings = mappings;
    }

    public void setMappingLocation(Resource location) {
        this.mappingLocations = new Resource[]{location};
    }

    public void setMappingLocations(Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IOException {
        this.mergedMappings = new Properties();
        CollectionUtils.mergePropertiesIntoMap(this.mappings, this.mergedMappings);
        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loading JMX object name mappings file from " + location);
                }
                PropertiesLoaderUtils.fillProperties(this.mergedMappings, location);
            }
        }
    }

    @Override // org.springframework.jmx.export.naming.ObjectNamingStrategy
    public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
        Assert.notNull(beanKey, "KeyNamingStrategy requires bean key");
        String objectName = null;
        if (this.mergedMappings != null) {
            objectName = this.mergedMappings.getProperty(beanKey);
        }
        if (objectName == null) {
            objectName = beanKey;
        }
        return ObjectNameManager.getInstance(objectName);
    }
}
