package org.springframework.boot.jarmode.layertools;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/MissingValueException.class */
class MissingValueException extends RuntimeException {
    private final String optionName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MissingValueException(String optionName) {
        this.optionName = optionName;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "--" + this.optionName;
    }
}
