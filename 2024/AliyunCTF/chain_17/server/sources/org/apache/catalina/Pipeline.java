package org.apache.catalina;

import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Pipeline.class */
public interface Pipeline extends Contained {
    Valve getBasic();

    void setBasic(Valve valve);

    void addValve(Valve valve);

    Valve[] getValves();

    void removeValve(Valve valve);

    Valve getFirst();

    boolean isAsyncSupported();

    void findNonAsyncValves(Set<String> set);
}
