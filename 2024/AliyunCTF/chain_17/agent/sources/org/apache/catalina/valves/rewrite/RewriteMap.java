package org.apache.catalina.valves.rewrite;

import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RewriteMap.class */
public interface RewriteMap {
    String setParameters(String str);

    String lookup(String str);

    default void setParameters(String... params) {
        if (params == null) {
            return;
        }
        if (params.length > 1) {
            throw new IllegalArgumentException(StringManager.getManager((Class<?>) RewriteMap.class).getString("rewriteMap.tooManyParameters"));
        }
        setParameters(params[0]);
    }
}
