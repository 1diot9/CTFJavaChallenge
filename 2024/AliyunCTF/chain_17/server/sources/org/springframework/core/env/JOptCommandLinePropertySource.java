package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Deprecated(since = "6.1")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/JOptCommandLinePropertySource.class */
public class JOptCommandLinePropertySource extends CommandLinePropertySource<OptionSet> {
    public JOptCommandLinePropertySource(OptionSet options) {
        super(options);
    }

    public JOptCommandLinePropertySource(String name, OptionSet options) {
        super(name, options);
    }

    @Override // org.springframework.core.env.CommandLinePropertySource
    protected boolean containsOption(String name) {
        return ((OptionSet) this.source).has(name);
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        List<String> names = new ArrayList<>();
        for (OptionSpec<?> spec : ((OptionSet) this.source).specs()) {
            String lastOption = (String) CollectionUtils.lastElement(spec.options());
            if (lastOption != null) {
                names.add(lastOption);
            }
        }
        return StringUtils.toStringArray(names);
    }

    @Override // org.springframework.core.env.CommandLinePropertySource
    @Nullable
    public List<String> getOptionValues(String name) {
        List<?> argValues = ((OptionSet) this.source).valuesOf(name);
        List<String> stringArgValues = new ArrayList<>();
        for (Object argValue : argValues) {
            stringArgValues.add(argValue.toString());
        }
        if (stringArgValues.isEmpty()) {
            if (((OptionSet) this.source).has(name)) {
                return Collections.emptyList();
            }
            return null;
        }
        return Collections.unmodifiableList(stringArgValues);
    }

    @Override // org.springframework.core.env.CommandLinePropertySource
    protected List<String> getNonOptionArgs() {
        List<?> argValues = ((OptionSet) this.source).nonOptionArguments();
        List<String> stringArgValues = new ArrayList<>();
        for (Object argValue : argValues) {
            stringArgValues.add(argValue.toString());
        }
        return stringArgValues.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(stringArgValues);
    }
}
