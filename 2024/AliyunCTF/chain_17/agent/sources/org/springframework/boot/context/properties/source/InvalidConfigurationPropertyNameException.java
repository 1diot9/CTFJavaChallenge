package org.springframework.boot.context.properties.source;

import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/InvalidConfigurationPropertyNameException.class */
public class InvalidConfigurationPropertyNameException extends RuntimeException {
    private final CharSequence name;
    private final List<Character> invalidCharacters;

    public InvalidConfigurationPropertyNameException(CharSequence name, List<Character> invalidCharacters) {
        super("Configuration property name '" + name + "' is not valid");
        this.name = name;
        this.invalidCharacters = invalidCharacters;
    }

    public List<Character> getInvalidCharacters() {
        return this.invalidCharacters;
    }

    public CharSequence getName() {
        return this.name;
    }

    public static void throwIfHasInvalidChars(CharSequence name, List<Character> invalidCharacters) {
        if (!invalidCharacters.isEmpty()) {
            throw new InvalidConfigurationPropertyNameException(name, invalidCharacters);
        }
    }
}
