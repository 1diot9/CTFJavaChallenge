package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ResourceBase.class */
public class ResourceBase implements Serializable, Injectable {
    private static final long serialVersionUID = 1;
    private String description = null;
    private String name = null;
    private String type = null;
    private String lookupName = null;
    private final Map<String, Object> properties = new HashMap();
    private final List<InjectionTarget> injectionTargets = new ArrayList();
    private NamingResources resources = null;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // org.apache.tomcat.util.descriptor.web.Injectable
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLookupName() {
        return this.lookupName;
    }

    public void setLookupName(String lookupName) {
        if (lookupName == null || lookupName.length() == 0) {
            this.lookupName = null;
        } else {
            this.lookupName = lookupName;
        }
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public void removeProperty(String name) {
        this.properties.remove(name);
    }

    public Iterator<String> listProperties() {
        return this.properties.keySet().iterator();
    }

    @Override // org.apache.tomcat.util.descriptor.web.Injectable
    public void addInjectionTarget(String injectionTargetName, String jndiName) {
        InjectionTarget target = new InjectionTarget(injectionTargetName, jndiName);
        this.injectionTargets.add(target);
    }

    @Override // org.apache.tomcat.util.descriptor.web.Injectable
    public List<InjectionTarget> getInjectionTargets() {
        return this.injectionTargets;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.description == null ? 0 : this.description.hashCode());
        return (31 * ((31 * ((31 * ((31 * ((31 * result) + (this.injectionTargets == null ? 0 : this.injectionTargets.hashCode()))) + (this.name == null ? 0 : this.name.hashCode()))) + (this.properties == null ? 0 : this.properties.hashCode()))) + (this.type == null ? 0 : this.type.hashCode()))) + (this.lookupName == null ? 0 : this.lookupName.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ResourceBase other = (ResourceBase) obj;
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.injectionTargets == null) {
            if (other.injectionTargets != null) {
                return false;
            }
        } else if (!this.injectionTargets.equals(other.injectionTargets)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!this.properties.equals(other.properties)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.lookupName == null) {
            if (other.lookupName != null) {
                return false;
            }
            return true;
        }
        if (!this.lookupName.equals(other.lookupName)) {
            return false;
        }
        return true;
    }

    public NamingResources getNamingResources() {
        return this.resources;
    }

    public void setNamingResources(NamingResources resources) {
        this.resources = resources;
    }
}
