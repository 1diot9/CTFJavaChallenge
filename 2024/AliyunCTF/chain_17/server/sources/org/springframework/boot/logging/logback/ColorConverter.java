package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/ColorConverter.class */
public class ColorConverter extends CompositeConverter<ILoggingEvent> {
    private static final Map<String, AnsiElement> ELEMENTS;
    private static final Map<Integer, AnsiElement> LEVELS;

    static {
        Map<String, AnsiElement> ansiElements = new HashMap<>();
        ansiElements.put("black", AnsiColor.BLACK);
        ansiElements.put("white", AnsiColor.WHITE);
        ansiElements.put("faint", AnsiStyle.FAINT);
        ansiElements.put("red", AnsiColor.RED);
        ansiElements.put("green", AnsiColor.GREEN);
        ansiElements.put("yellow", AnsiColor.YELLOW);
        ansiElements.put("blue", AnsiColor.BLUE);
        ansiElements.put("magenta", AnsiColor.MAGENTA);
        ansiElements.put("cyan", AnsiColor.CYAN);
        ansiElements.put("bright_black", AnsiColor.BRIGHT_BLACK);
        ansiElements.put("bright_white", AnsiColor.BRIGHT_WHITE);
        ansiElements.put("bright_red", AnsiColor.BRIGHT_RED);
        ansiElements.put("bright_green", AnsiColor.BRIGHT_GREEN);
        ansiElements.put("bright_yellow", AnsiColor.BRIGHT_YELLOW);
        ansiElements.put("bright_blue", AnsiColor.BRIGHT_BLUE);
        ansiElements.put("bright_magenta", AnsiColor.BRIGHT_MAGENTA);
        ansiElements.put("bright_cyan", AnsiColor.BRIGHT_CYAN);
        ELEMENTS = Collections.unmodifiableMap(ansiElements);
        Map<Integer, AnsiElement> ansiLevels = new HashMap<>();
        ansiLevels.put(Level.ERROR_INTEGER, AnsiColor.RED);
        ansiLevels.put(Level.WARN_INTEGER, AnsiColor.YELLOW);
        LEVELS = Collections.unmodifiableMap(ansiLevels);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.pattern.CompositeConverter
    public String transform(ILoggingEvent event, String in) {
        AnsiElement element = ELEMENTS.get(getFirstOption());
        if (element == null) {
            AnsiElement element2 = LEVELS.get(event.getLevel().toInteger());
            element = element2 != null ? element2 : AnsiColor.GREEN;
        }
        return toAnsiString(in, element);
    }

    protected String toAnsiString(String in, AnsiElement element) {
        return AnsiOutput.toString(element, in);
    }
}
