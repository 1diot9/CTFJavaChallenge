package org.springframework.boot.context.config;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/UnsupportedConfigDataLocationException.class */
public class UnsupportedConfigDataLocationException extends ConfigDataException {
    private final ConfigDataLocation location;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UnsupportedConfigDataLocationException(ConfigDataLocation location) {
        super("Unsupported config data location '" + location + "'", null);
        this.location = location;
    }

    public ConfigDataLocation getLocation() {
        return this.location;
    }
}
