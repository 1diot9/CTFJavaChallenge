package org.apache.tomcat.util.modeler;

import javax.management.MBeanParameterInfo;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/ParameterInfo.class */
public class ParameterInfo extends FeatureInfo {
    private static final long serialVersionUID = 2222796006787664020L;

    public MBeanParameterInfo createParameterInfo() {
        if (this.info == null) {
            this.info = new MBeanParameterInfo(getName(), getType(), getDescription());
        }
        return this.info;
    }
}
