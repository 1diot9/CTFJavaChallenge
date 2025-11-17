package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ServletDef.class */
public class ServletDef implements Serializable {
    private static final long serialVersionUID = 1;
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private String description = null;
    private String displayName = null;
    private String smallIcon = null;
    private String largeIcon = null;
    private String servletName = null;
    private String servletClass = null;
    private String jspFile = null;
    private final Map<String, String> parameters = new HashMap();
    private Integer loadOnStartup = null;
    private String runAs = null;
    private final Set<SecurityRoleRef> securityRoleRefs = new HashSet();
    private MultipartDef multipartDef = null;
    private Boolean asyncSupported = null;
    private Boolean enabled = null;
    private boolean overridable = false;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getServletName() {
        return this.servletName;
    }

    public void setServletName(String servletName) {
        if (servletName == null || servletName.equals("")) {
            throw new IllegalArgumentException(sm.getString("servletDef.invalidServletName", servletName));
        }
        this.servletName = servletName;
    }

    public String getServletClass() {
        return this.servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    public String getJspFile() {
        return this.jspFile;
    }

    public void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }

    public Map<String, String> getParameterMap() {
        return this.parameters;
    }

    public void addInitParameter(String name, String value) {
        if (this.parameters.containsKey(name)) {
            return;
        }
        this.parameters.put(name, value);
    }

    public Integer getLoadOnStartup() {
        return this.loadOnStartup;
    }

    public void setLoadOnStartup(String loadOnStartup) {
        this.loadOnStartup = Integer.valueOf(loadOnStartup);
    }

    public String getRunAs() {
        return this.runAs;
    }

    public void setRunAs(String runAs) {
        this.runAs = runAs;
    }

    public Set<SecurityRoleRef> getSecurityRoleRefs() {
        return this.securityRoleRefs;
    }

    public void addSecurityRoleRef(SecurityRoleRef securityRoleRef) {
        this.securityRoleRefs.add(securityRoleRef);
    }

    public MultipartDef getMultipartDef() {
        return this.multipartDef;
    }

    public void setMultipartDef(MultipartDef multipartDef) {
        this.multipartDef = multipartDef;
    }

    public Boolean getAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(String asyncSupported) {
        this.asyncSupported = Boolean.valueOf(asyncSupported);
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = Boolean.valueOf(enabled);
    }

    public boolean isOverridable() {
        return this.overridable;
    }

    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }
}
