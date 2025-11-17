package org.apache.tomcat.util.descriptor.web;

import jakarta.servlet.Filter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/FilterDef.class */
public class FilterDef implements Serializable {
    private static final long serialVersionUID = 1;
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private String description = null;
    private String displayName = null;
    private transient Filter filter = null;
    private String filterClass = null;
    private String filterName = null;
    private String largeIcon = null;
    private final Map<String, String> parameters = new HashMap();
    private String smallIcon = null;
    private String asyncSupported = null;

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

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getFilterClass() {
        return this.filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public String getFilterName() {
        return this.filterName;
    }

    public void setFilterName(String filterName) {
        if (filterName == null || filterName.equals("")) {
            throw new IllegalArgumentException(sm.getString("filterDef.invalidFilterName", filterName));
        }
        this.filterName = filterName;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public Map<String, String> getParameterMap() {
        return this.parameters;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(String asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public void addInitParameter(String name, String value) {
        if (this.parameters.containsKey(name)) {
            return;
        }
        this.parameters.put(name, value);
    }

    public String toString() {
        return "FilterDef[filterName=" + this.filterName + ", filterClass=" + this.filterClass + ']';
    }
}
