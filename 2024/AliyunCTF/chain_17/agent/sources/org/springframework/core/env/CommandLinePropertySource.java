package org.springframework.core.env;

import java.util.Collection;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/CommandLinePropertySource.class */
public abstract class CommandLinePropertySource<T> extends EnumerablePropertySource<T> {
    public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";
    public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";
    private String nonOptionArgsPropertyName;

    protected abstract boolean containsOption(String name);

    @Nullable
    protected abstract List<String> getOptionValues(String name);

    protected abstract List<String> getNonOptionArgs();

    public CommandLinePropertySource(T source) {
        super(COMMAND_LINE_PROPERTY_SOURCE_NAME, source);
        this.nonOptionArgsPropertyName = DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;
    }

    public CommandLinePropertySource(String name, T source) {
        super(name, source);
        this.nonOptionArgsPropertyName = DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;
    }

    public void setNonOptionArgsPropertyName(String nonOptionArgsPropertyName) {
        this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
    }

    @Override // org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public final boolean containsProperty(String name) {
        if (this.nonOptionArgsPropertyName.equals(name)) {
            return !getNonOptionArgs().isEmpty();
        }
        return containsOption(name);
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public final String getProperty(String name) {
        if (this.nonOptionArgsPropertyName.equals(name)) {
            Collection<String> nonOptionArguments = getNonOptionArgs();
            if (nonOptionArguments.isEmpty()) {
                return null;
            }
            return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
        }
        Collection<String> optionValues = getOptionValues(name);
        if (optionValues == null) {
            return null;
        }
        return StringUtils.collectionToCommaDelimitedString(optionValues);
    }
}
