package ch.qos.logback.classic.util;

import ch.qos.logback.core.util.EnvUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/ClassicEnvUtil.class */
public class ClassicEnvUtil {
    public static boolean isGroovyAvailable() {
        return EnvUtil.isClassAvailable(ClassicEnvUtil.class, "groovy.lang.Binding");
    }

    public static <T> List<T> loadFromServiceLoader(Class<T> c, ClassLoader classLoader) {
        ServiceLoader<T> loader = ServiceLoader.load(c, classLoader);
        List<T> listOfT = new ArrayList<>();
        Iterator<T> it = loader.iterator();
        while (it.hasNext()) {
            T t = it.next();
            listOfT.add(t);
        }
        return listOfT;
    }
}
