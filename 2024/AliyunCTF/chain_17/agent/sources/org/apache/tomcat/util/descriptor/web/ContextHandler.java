package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ContextHandler.class */
public class ContextHandler extends ResourceBase {
    private static final long serialVersionUID = 1;
    private String handlerclass = null;
    private final Map<String, String> soapHeaders = new HashMap();
    private final List<String> soapRoles = new ArrayList();
    private final List<String> portNames = new ArrayList();

    public String getHandlerclass() {
        return this.handlerclass;
    }

    public void setHandlerclass(String handlerclass) {
        this.handlerclass = handlerclass;
    }

    public Iterator<String> getLocalparts() {
        return this.soapHeaders.keySet().iterator();
    }

    public String getNamespaceuri(String localpart) {
        return this.soapHeaders.get(localpart);
    }

    public void addSoapHeaders(String localpart, String namespaceuri) {
        this.soapHeaders.put(localpart, namespaceuri);
    }

    public void setProperty(String name, String value) {
        setProperty(name, (Object) value);
    }

    public String getSoapRole(int i) {
        return this.soapRoles.get(i);
    }

    public int getSoapRolesSize() {
        return this.soapRoles.size();
    }

    public void addSoapRole(String soapRole) {
        this.soapRoles.add(soapRole);
    }

    public String getPortName(int i) {
        return this.portNames.get(i);
    }

    public int getPortNamesSize() {
        return this.portNames.size();
    }

    public void addPortName(String portName) {
        this.portNames.add(portName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextHandler[");
        sb.append("name=");
        sb.append(getName());
        if (this.handlerclass != null) {
            sb.append(", class=");
            sb.append(this.handlerclass);
        }
        if (this.soapHeaders != null) {
            sb.append(", soap-headers=");
            sb.append(this.soapHeaders);
        }
        if (getSoapRolesSize() > 0) {
            sb.append(", soap-roles=");
            sb.append(this.soapRoles);
        }
        if (getPortNamesSize() > 0) {
            sb.append(", port-name=");
            sb.append(this.portNames);
        }
        if (listProperties() != null) {
            sb.append(", init-param=");
            sb.append(listProperties());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * ((31 * result) + (this.handlerclass == null ? 0 : this.handlerclass.hashCode()))) + (this.portNames == null ? 0 : this.portNames.hashCode()))) + (this.soapHeaders == null ? 0 : this.soapHeaders.hashCode()))) + (this.soapRoles == null ? 0 : this.soapRoles.hashCode());
    }

    @Override // org.apache.tomcat.util.descriptor.web.ResourceBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ContextHandler other = (ContextHandler) obj;
        if (this.handlerclass == null) {
            if (other.handlerclass != null) {
                return false;
            }
        } else if (!this.handlerclass.equals(other.handlerclass)) {
            return false;
        }
        if (this.portNames == null) {
            if (other.portNames != null) {
                return false;
            }
        } else if (!this.portNames.equals(other.portNames)) {
            return false;
        }
        if (this.soapHeaders == null) {
            if (other.soapHeaders != null) {
                return false;
            }
        } else if (!this.soapHeaders.equals(other.soapHeaders)) {
            return false;
        }
        if (this.soapRoles == null) {
            if (other.soapRoles != null) {
                return false;
            }
            return true;
        }
        if (!this.soapRoles.equals(other.soapRoles)) {
            return false;
        }
        return true;
    }
}
