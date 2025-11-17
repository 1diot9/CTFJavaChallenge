package ch.qos.logback.classic.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/ConfiguratorRank.class */
public @interface ConfiguratorRank {
    public static final int FALLBACK = -10;
    public static final int NOMINAL = 0;
    public static final int SERIALIZED_MODEL = 10;
    public static final int DEFAULT = 20;
    public static final int CUSTOM_LOW_PRIORITY = 20;
    public static final int CUSTOM_NORMAL_PRIORITY = 30;
    public static final int CUSTOM_HIGH_PRIORITY = 40;
    public static final int CUSTOM_TOP_PRIORITY = 50;

    int value() default 20;
}
