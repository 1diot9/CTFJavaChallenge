package org.apache.tomcat.util.digester;

import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.security.PermissionCheck;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/digester/ServiceBindingPropertySource.class */
public class ServiceBindingPropertySource implements IntrospectionUtils.SecurePropertySource {
    private static final String SERVICE_BINDING_ROOT_ENV_VAR = "SERVICE_BINDING_ROOT";

    @Override // org.apache.tomcat.util.IntrospectionUtils.PropertySource
    public String getProperty(String key) {
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tomcat.util.IntrospectionUtils.SecurePropertySource
    public String getProperty(String key, ClassLoader classLoader) {
        byte c;
        if (classLoader instanceof PermissionCheck) {
            Permission p = new RuntimePermission("getenv.SERVICE_BINDING_ROOT", null);
            if (!((PermissionCheck) classLoader).check(p)) {
                return null;
            }
        }
        String serviceBindingRoot = System.getenv(SERVICE_BINDING_ROOT_ENV_VAR);
        if (serviceBindingRoot == null) {
            return null;
        }
        boolean chomp = false;
        if (key.startsWith("chomp:")) {
            chomp = true;
            key = key.substring(6);
        }
        String[] parts = key.split("\\.");
        if (parts.length != 2) {
            return null;
        }
        Path path = Paths.get(serviceBindingRoot, parts[0], parts[1]);
        if (!path.toFile().exists()) {
            return null;
        }
        try {
            if (classLoader instanceof PermissionCheck) {
                Permission p2 = new FilePermission(path.toString(), "read");
                if (!((PermissionCheck) classLoader).check(p2)) {
                    return null;
                }
            }
            byte[] bytes = Files.readAllBytes(path);
            int length = bytes.length;
            if (chomp) {
                if (length > 1 && bytes[length - 2] == 13 && bytes[length - 2] == 10) {
                    length -= 2;
                } else if (length > 0 && ((c = bytes[length - 1]) == 13 || c == 10)) {
                    length--;
                }
            }
            return new String(bytes, 0, length);
        } catch (IOException e) {
            return null;
        }
    }
}
