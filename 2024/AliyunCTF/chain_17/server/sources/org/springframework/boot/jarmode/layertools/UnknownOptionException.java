package org.springframework.boot.jarmode.layertools;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/UnknownOptionException.class */
class UnknownOptionException extends RuntimeException {
    private final String optionName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnknownOptionException(String optionName) {
        this.optionName = optionName;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "--" + this.optionName;
    }
}
