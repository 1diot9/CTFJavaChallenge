package org.springframework.boot;

import java.io.PrintStream;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringBootBanner.class */
class SpringBootBanner implements Banner {
    private static final String[] BANNER = {"", "  .   ____          _            __ _ _", " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\", "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\", " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )", "  '  |____| .__|_| |_|_| |_\\__, | / / / /", " =========|_|==============|___/=/_/_/_/"};
    private static final String SPRING_BOOT = " :: Spring Boot :: ";
    private static final int STRAP_LINE_SIZE = 42;

    @Override // org.springframework.boot.Banner
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = SpringBootVersion.getVersion();
        String version2 = version != null ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < 42 - (version2.length() + SPRING_BOOT.length())) {
            padding.append(" ");
        }
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT, AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT, version2));
        printStream.println();
    }
}
