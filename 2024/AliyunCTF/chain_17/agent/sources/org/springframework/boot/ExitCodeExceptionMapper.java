package org.springframework.boot;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ExitCodeExceptionMapper.class */
public interface ExitCodeExceptionMapper {
    int getExitCode(Throwable exception);
}
