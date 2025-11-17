package org.springframework.boot;

import java.util.List;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ApplicationArguments.class */
public interface ApplicationArguments {
    String[] getSourceArgs();

    Set<String> getOptionNames();

    boolean containsOption(String name);

    List<String> getOptionValues(String name);

    List<String> getNonOptionArgs();
}
