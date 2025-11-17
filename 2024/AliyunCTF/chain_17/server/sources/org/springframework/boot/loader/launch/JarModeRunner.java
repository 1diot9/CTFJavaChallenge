package org.springframework.boot.loader.launch;

import java.util.List;
import org.springframework.boot.loader.jarmode.JarMode;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:org/springframework/boot/loader/launch/JarModeRunner.class */
final class JarModeRunner {
    static final String DISABLE_SYSTEM_EXIT = JarModeRunner.class.getName() + ".DISABLE_SYSTEM_EXIT";

    private JarModeRunner() {
    }

    static void main(String[] args) {
        String mode = System.getProperty("jarmode");
        List<JarMode> candidates = SpringFactoriesLoader.loadFactories(JarMode.class, ClassUtils.getDefaultClassLoader());
        for (JarMode candidate : candidates) {
            if (candidate.accepts(mode)) {
                candidate.run(mode, args);
                return;
            }
        }
        System.err.println("Unsupported jarmode '" + mode + "'");
        if (!Boolean.getBoolean(DISABLE_SYSTEM_EXIT)) {
            System.exit(1);
        }
    }
}
