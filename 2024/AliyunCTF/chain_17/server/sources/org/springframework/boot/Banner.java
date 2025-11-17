package org.springframework.boot;

import java.io.PrintStream;
import org.springframework.core.env.Environment;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/Banner.class */
public interface Banner {

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/Banner$Mode.class */
    public enum Mode {
        OFF,
        CONSOLE,
        LOG
    }

    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);
}
