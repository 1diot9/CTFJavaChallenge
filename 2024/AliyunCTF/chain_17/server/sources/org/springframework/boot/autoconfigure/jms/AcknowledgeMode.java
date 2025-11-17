package org.springframework.boot.autoconfigure.jms;

import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/AcknowledgeMode.class */
public final class AcknowledgeMode {
    private static final Map<String, AcknowledgeMode> knownModes = new HashMap(3);
    public static final AcknowledgeMode AUTO = new AcknowledgeMode(1);
    public static final AcknowledgeMode CLIENT = new AcknowledgeMode(2);
    public static final AcknowledgeMode DUPS_OK = new AcknowledgeMode(3);
    private final int mode;

    static {
        knownModes.put("auto", AUTO);
        knownModes.put("client", CLIENT);
        knownModes.put("dupsok", DUPS_OK);
    }

    private AcknowledgeMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    public static AcknowledgeMode of(String mode) {
        String canonicalMode = canonicalize(mode);
        AcknowledgeMode knownMode = knownModes.get(canonicalMode);
        if (knownMode != null) {
            return knownMode;
        }
        try {
            return new AcknowledgeMode(Integer.parseInt(canonicalMode));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + mode + "' is neither a known acknowledge mode (auto, client, or dups_ok) nor an integer value");
        }
    }

    private static String canonicalize(String input) {
        StringBuilder canonicalName = new StringBuilder(input.length());
        input.chars().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach(c -> {
            canonicalName.append((char) c);
        });
        return canonicalName.toString();
    }
}
