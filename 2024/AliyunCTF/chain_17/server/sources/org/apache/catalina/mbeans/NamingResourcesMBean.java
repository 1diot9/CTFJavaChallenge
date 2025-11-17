package org.apache.catalina.mbeans;

import java.util.ArrayList;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/NamingResourcesMBean.class */
public class NamingResourcesMBean extends BaseModelMBean {
    private static final StringManager sm = StringManager.getManager((Class<?>) NamingResourcesMBean.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("NamingResources");

    public String[] getEnvironments() {
        ContextEnvironment[] envs = ((NamingResourcesImpl) this.resource).findEnvironments();
        List<String> results = new ArrayList<>();
        for (ContextEnvironment env : envs) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), env);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.environment", env), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getResources() {
        ContextResource[] resources = ((NamingResourcesImpl) this.resource).findResources();
        List<String> results = new ArrayList<>();
        for (ContextResource contextResource : resources) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), contextResource);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.resource", contextResource), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String[] getResourceLinks() {
        ContextResourceLink[] resourceLinks = ((NamingResourcesImpl) this.resource).findResourceLinks();
        List<String> results = new ArrayList<>();
        for (ContextResourceLink resourceLink : resourceLinks) {
            try {
                ObjectName oname = MBeanUtils.createObjectName(this.managed.getDomain(), resourceLink);
                results.add(oname.toString());
            } catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException(sm.getString("namingResourcesMBean.createObjectNameError.resourceLink", resourceLink), e);
            }
        }
        return (String[]) results.toArray(new String[0]);
    }

    public String addEnvironment(String envName, String type, String value) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return null;
        }
        if (nresources.findEnvironment(envName) != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.environment", envName));
        }
        ContextEnvironment env = new ContextEnvironment();
        env.setName(envName);
        env.setType(type);
        env.setValue(value);
        nresources.addEnvironment(env);
        ManagedBean managed = this.registry.findManagedBean("ContextEnvironment");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), env);
        return oname.toString();
    }

    public String addResource(String resourceName, String type) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return null;
        }
        if (nresources.findResource(resourceName) != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.resource", resourceName));
        }
        ContextResource resource = new ContextResource();
        resource.setName(resourceName);
        resource.setType(type);
        nresources.addResource(resource);
        ManagedBean managed = this.registry.findManagedBean("ContextResource");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), resource);
        return oname.toString();
    }

    public String addResourceLink(String resourceLinkName, String type) throws MalformedObjectNameException {
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return null;
        }
        if (nresources.findResourceLink(resourceLinkName) != null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.addAlreadyExists.resourceLink", resourceLinkName));
        }
        ContextResourceLink resourceLink = new ContextResourceLink();
        resourceLink.setName(resourceLinkName);
        resourceLink.setType(type);
        nresources.addResourceLink(resourceLink);
        ManagedBean managed = this.registry.findManagedBean("ContextResourceLink");
        ObjectName oname = MBeanUtils.createObjectName(managed.getDomain(), resourceLink);
        return oname.toString();
    }

    public void removeEnvironment(String envName) {
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return;
        }
        ContextEnvironment env = nresources.findEnvironment(envName);
        if (env == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.environment", envName));
        }
        nresources.removeEnvironment(envName);
    }

    public void removeResource(String resourceName) {
        String resourceName2 = ObjectName.unquote(resourceName);
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return;
        }
        ContextResource resource = nresources.findResource(resourceName2);
        if (resource == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.resource", resourceName2));
        }
        nresources.removeResource(resourceName2);
    }

    public void removeResourceLink(String resourceLinkName) {
        String resourceLinkName2 = ObjectName.unquote(resourceLinkName);
        NamingResourcesImpl nresources = (NamingResourcesImpl) this.resource;
        if (nresources == null) {
            return;
        }
        ContextResourceLink resourceLink = nresources.findResourceLink(resourceLinkName2);
        if (resourceLink == null) {
            throw new IllegalArgumentException(sm.getString("namingResourcesMBean.removeNotFound.resourceLink", resourceLinkName2));
        }
        nresources.removeResourceLink(resourceLinkName2);
    }
}
