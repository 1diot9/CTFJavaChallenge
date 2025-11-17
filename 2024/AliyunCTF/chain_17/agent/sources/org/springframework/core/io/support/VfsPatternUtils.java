package org.springframework.core.io.support;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import org.springframework.core.io.VfsUtils;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/VfsPatternUtils.class */
abstract class VfsPatternUtils extends VfsUtils {
    VfsPatternUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Object getVisitorAttributes() {
        return doGetVisitorAttributes();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getPath(Object resource) {
        String path = doGetPath(resource);
        return path != null ? path : "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object findRoot(URL url) throws IOException {
        return getRoot(url);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(VIRTUAL_FILE_VISITOR_INTERFACE.getClassLoader(), new Class[]{VIRTUAL_FILE_VISITOR_INTERFACE}, visitor);
        invokeVfsMethod(VIRTUAL_FILE_METHOD_VISIT, resource, visitorProxy);
    }
}
