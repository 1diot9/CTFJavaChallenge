package org.springframework.boot.ansi;

import ch.qos.logback.core.pattern.color.ANSIConstants;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiColor.class */
public enum AnsiColor implements AnsiElement {
    DEFAULT(ANSIConstants.DEFAULT_FG),
    BLACK(ANSIConstants.BLACK_FG),
    RED(ANSIConstants.RED_FG),
    GREEN(ANSIConstants.GREEN_FG),
    YELLOW(ANSIConstants.YELLOW_FG),
    BLUE(ANSIConstants.BLUE_FG),
    MAGENTA(ANSIConstants.MAGENTA_FG),
    CYAN(ANSIConstants.CYAN_FG),
    WHITE(ANSIConstants.WHITE_FG),
    BRIGHT_BLACK("90"),
    BRIGHT_RED("91"),
    BRIGHT_GREEN("92"),
    BRIGHT_YELLOW("93"),
    BRIGHT_BLUE("94"),
    BRIGHT_MAGENTA("95"),
    BRIGHT_CYAN("96"),
    BRIGHT_WHITE("97");

    private final String code;

    AnsiColor(String code) {
        this.code = code;
    }

    @Override // java.lang.Enum, org.springframework.boot.ansi.AnsiElement
    public String toString() {
        return this.code;
    }
}
