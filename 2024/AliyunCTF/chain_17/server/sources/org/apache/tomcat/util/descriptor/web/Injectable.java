package org.apache.tomcat.util.descriptor.web;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/Injectable.class */
public interface Injectable {
    String getName();

    void addInjectionTarget(String str, String str2);

    List<InjectionTarget> getInjectionTargets();
}
