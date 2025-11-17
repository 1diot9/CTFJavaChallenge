package org.springframework.boot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/DefaultApplicationArguments.class */
public class DefaultApplicationArguments implements ApplicationArguments {
    private final Source source;
    private final String[] args;

    public DefaultApplicationArguments(String... args) {
        Assert.notNull(args, "Args must not be null");
        this.source = new Source(args);
        this.args = args;
    }

    @Override // org.springframework.boot.ApplicationArguments
    public String[] getSourceArgs() {
        return this.args;
    }

    @Override // org.springframework.boot.ApplicationArguments
    public Set<String> getOptionNames() {
        String[] names = this.source.getPropertyNames();
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(names)));
    }

    @Override // org.springframework.boot.ApplicationArguments
    public boolean containsOption(String name) {
        return this.source.containsProperty(name);
    }

    @Override // org.springframework.boot.ApplicationArguments
    public List<String> getOptionValues(String name) {
        List<String> values = this.source.getOptionValues(name);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        return null;
    }

    @Override // org.springframework.boot.ApplicationArguments
    public List<String> getNonOptionArgs() {
        return this.source.getNonOptionArgs();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/DefaultApplicationArguments$Source.class */
    private static class Source extends SimpleCommandLinePropertySource {
        Source(String[] args) {
            super(args);
        }

        @Override // org.springframework.core.env.SimpleCommandLinePropertySource, org.springframework.core.env.CommandLinePropertySource
        public List<String> getNonOptionArgs() {
            return super.getNonOptionArgs();
        }

        @Override // org.springframework.core.env.SimpleCommandLinePropertySource, org.springframework.core.env.CommandLinePropertySource
        public List<String> getOptionValues(String name) {
            return super.getOptionValues(name);
        }
    }
}
