package org.jooq.tools.reflect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Processor;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/CompileOptions.class */
public final class CompileOptions {
    final List<? extends Processor> processors;
    final List<String> options;
    final ClassLoader classLoader;

    public CompileOptions() {
        this(Collections.emptyList(), Collections.emptyList(), null);
    }

    private CompileOptions(List<? extends Processor> processors, List<String> options, ClassLoader classLoader) {
        this.processors = processors;
        this.options = options;
        this.classLoader = classLoader;
    }

    public final CompileOptions processors(Processor... newProcessors) {
        return processors(Arrays.asList(newProcessors));
    }

    public final CompileOptions processors(List<? extends Processor> newProcessors) {
        return new CompileOptions(newProcessors, this.options, this.classLoader);
    }

    public final CompileOptions options(String... newOptions) {
        return options(Arrays.asList(newOptions));
    }

    public final CompileOptions options(List<String> newOptions) {
        return new CompileOptions(this.processors, newOptions, this.classLoader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasOption(String opt) {
        for (String option : this.options) {
            if (option.equalsIgnoreCase(opt)) {
                return true;
            }
        }
        return false;
    }

    public final CompileOptions classLoader(ClassLoader newClassLoader) {
        return new CompileOptions(this.processors, this.options, newClassLoader);
    }
}
