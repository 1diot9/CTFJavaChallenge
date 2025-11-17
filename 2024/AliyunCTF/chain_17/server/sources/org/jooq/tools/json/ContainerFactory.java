package org.jooq.tools.json;

import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/ContainerFactory.class */
public interface ContainerFactory {
    Map createObjectContainer();

    List createArrayContainer();
}
