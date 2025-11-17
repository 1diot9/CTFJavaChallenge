package org.apache.tomcat.util.modeler;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import javax.management.MBeanAttributeInfo;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/AttributeInfo.class */
public class AttributeInfo extends FeatureInfo {
    private static final long serialVersionUID = -2511626862303972143L;
    protected String displayName = null;
    protected String getMethod = null;
    protected String setMethod = null;
    protected boolean readable = true;
    protected boolean writeable = true;
    protected boolean is = false;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGetMethod() {
        if (this.getMethod == null) {
            this.getMethod = getMethodName(getName(), true, isIs());
        }
        return this.getMethod;
    }

    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    public boolean isIs() {
        return this.is;
    }

    public void setIs(boolean is) {
        this.is = is;
    }

    public boolean isReadable() {
        return this.readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public String getSetMethod() {
        if (this.setMethod == null) {
            this.setMethod = getMethodName(getName(), false, false);
        }
        return this.setMethod;
    }

    public void setSetMethod(String setMethod) {
        this.setMethod = setMethod;
    }

    public boolean isWriteable() {
        return this.writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MBeanAttributeInfo createAttributeInfo() {
        if (this.info == null) {
            this.info = new MBeanAttributeInfo(getName(), getType(), getDescription(), isReadable(), isWriteable(), false);
        }
        return this.info;
    }

    private String getMethodName(String name, boolean getter, boolean is) {
        StringBuilder sb = new StringBuilder();
        if (getter) {
            if (is) {
                sb.append(BeanUtil.PREFIX_GETTER_IS);
            } else {
                sb.append(BeanUtil.PREFIX_GETTER_GET);
            }
        } else {
            sb.append("set");
        }
        sb.append(Character.toUpperCase(name.charAt(0)));
        sb.append(name.substring(1));
        return sb.toString();
    }
}
