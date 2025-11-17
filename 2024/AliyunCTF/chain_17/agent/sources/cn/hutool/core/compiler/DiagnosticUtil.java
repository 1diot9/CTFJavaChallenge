package cn.hutool.core.compiler;

import java.util.List;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compiler/DiagnosticUtil.class */
public class DiagnosticUtil {
    public static String getMessages(DiagnosticCollector<?> collector) {
        List<?> diagnostics = collector.getDiagnostics();
        return (String) diagnostics.stream().map(String::valueOf).collect(Collectors.joining(System.lineSeparator()));
    }
}
