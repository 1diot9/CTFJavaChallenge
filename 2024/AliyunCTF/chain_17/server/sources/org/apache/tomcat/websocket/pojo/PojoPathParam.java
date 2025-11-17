package org.apache.tomcat.websocket.pojo;

import jakarta.websocket.DeploymentException;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Util;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/pojo/PojoPathParam.class */
public class PojoPathParam {
    private static final StringManager sm = StringManager.getManager((Class<?>) PojoPathParam.class);
    private final Class<?> type;
    private final String name;

    public PojoPathParam(Class<?> type, String name) throws DeploymentException {
        if (name != null) {
            validateType(type);
        }
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    private static void validateType(Class<?> type) throws DeploymentException {
        if (String.class == type || Util.isPrimitive(type)) {
        } else {
            throw new DeploymentException(sm.getString("pojoPathParam.wrongType", type.getName()));
        }
    }
}
