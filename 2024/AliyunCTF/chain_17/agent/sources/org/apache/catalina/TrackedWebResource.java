package org.apache.catalina;

import java.io.Closeable;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/TrackedWebResource.class */
public interface TrackedWebResource extends Closeable {
    Exception getCreatedBy();

    String getName();
}
