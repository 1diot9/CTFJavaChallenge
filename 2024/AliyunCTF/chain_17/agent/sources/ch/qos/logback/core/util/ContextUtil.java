package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/ContextUtil.class */
public class ContextUtil extends ContextAwareBase {
    static final String GROOVY_RUNTIME_PACKAGE = "org.codehaus.groovy.runtime";

    public ContextUtil(Context context) {
        setContext(context);
    }

    public void addProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            String key = (String) e.getKey();
            this.context.putProperty(key, (String) e.getValue());
        }
    }

    public static Map<String, String> getFilenameCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map<String, String> map = (Map) context.getObject(CoreConstants.FA_FILENAME_COLLISION_MAP);
        return map;
    }

    public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map<String, FileNamePattern> map = (Map) context.getObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP);
        return map;
    }

    public void addGroovyPackages(List<String> frameworkPackages) {
        addFrameworkPackage(frameworkPackages, GROOVY_RUNTIME_PACKAGE);
    }

    public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
        if (!frameworkPackages.contains(packageName)) {
            frameworkPackages.add(packageName);
        }
    }
}
