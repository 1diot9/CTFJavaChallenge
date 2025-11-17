package org.springframework.boot.context.properties.bind;

import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/MissingParametersCompilerArgumentException.class */
class MissingParametersCompilerArgumentException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MissingParametersCompilerArgumentException(Set<Class<?>> faultyClasses) {
        super(message(faultyClasses));
    }

    private static String message(Set<Class<?>> faultyClasses) {
        StringBuilder message = new StringBuilder(String.format("Constructor binding in a native image requires compilation with -parameters but the following classes were compiled without it:%n", new Object[0]));
        for (Class<?> faultyClass : faultyClasses) {
            message.append(String.format("\t%s%n", faultyClass.getName()));
        }
        return message.toString();
    }
}
