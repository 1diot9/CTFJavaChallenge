package org.apache.tomcat.util.modeler.modules;

import java.util.List;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/modules/ModelerSource.class */
public abstract class ModelerSource {
    protected static final StringManager sm = StringManager.getManager((Class<?>) Registry.class);
    protected Object source;

    public abstract List<ObjectName> loadDescriptors(Registry registry, String str, Object obj) throws Exception;
}
