package org.apache.catalina.mbeans;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/ClassNameMBean.class */
public class ClassNameMBean<T> extends BaseCatalinaMBean<T> {
    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public String getClassName() {
        return this.resource.getClass().getName();
    }
}
