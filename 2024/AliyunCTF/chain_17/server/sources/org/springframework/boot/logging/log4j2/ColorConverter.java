package org.springframework.boot.logging.log4j2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;

@ConverterKeys({"clr", "color"})
@Plugin(name = "color", category = "Converter")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/log4j2/ColorConverter.class */
public final class ColorConverter extends LogEventPatternConverter {
    private static final Map<String, AnsiElement> ELEMENTS;
    private static final Map<Integer, AnsiElement> LEVELS;
    private final List<PatternFormatter> formatters;
    private final AnsiElement styling;

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
        ansiLevels.put(Integer.valueOf(Level.FATAL.intLevel()), AnsiColor.RED);
        ansiLevels.put(Integer.valueOf(Level.ERROR.intLevel()), AnsiColor.RED);
        ansiLevels.put(Integer.valueOf(Level.WARN.intLevel()), AnsiColor.YELLOW);
        LEVELS = Collections.unmodifiableMap(ansiLevels);
    }

    private ColorConverter(List<PatternFormatter> formatters, AnsiElement styling) {
        super(AbstractHtmlElementTag.STYLE_ATTRIBUTE, AbstractHtmlElementTag.STYLE_ATTRIBUTE);
        this.formatters = formatters;
        this.styling = styling;
    }

    public static ColorConverter newInstance(Configuration config, String[] options) {
        if (options.length < 1) {
            LOGGER.error("Incorrect number of options on style. Expected at least 1, received {}", Integer.valueOf(options.length));
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on style");
            return null;
        }
        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        AnsiElement element = options.length != 1 ? ELEMENTS.get(options[1]) : null;
        return new ColorConverter(formatters, element);
    }

    public boolean handlesThrowable() {
        for (PatternFormatter formatter : this.formatters) {
            if (formatter.handlesThrowable()) {
                return true;
            }
        }
        return super.handlesThrowable();
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        StringBuilder buf = new StringBuilder();
        for (PatternFormatter formatter : this.formatters) {
            formatter.format(event, buf);
        }
        if (!buf.isEmpty()) {
            AnsiElement element = this.styling;
            if (element == null) {
                AnsiElement element2 = LEVELS.get(Integer.valueOf(event.getLevel().intLevel()));
                element = element2 != null ? element2 : AnsiColor.GREEN;
            }
            appendAnsiString(toAppendTo, buf.toString(), element);
        }
    }

    protected void appendAnsiString(StringBuilder toAppendTo, String in, AnsiElement element) {
        toAppendTo.append(AnsiOutput.toString(element, in));
    }
}
