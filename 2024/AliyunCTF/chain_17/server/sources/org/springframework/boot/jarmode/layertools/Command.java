package org.springframework.boot.jarmode.layertools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/Command.class */
abstract class Command {
    private final String name;
    private final String description;
    private final Options options;
    private final Parameters parameters;

    protected abstract void run(Map<Option, String> options, List<String> parameters);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Command(String name, String description, Options options, Parameters parameters) {
        this.name = name;
        this.description = description;
        this.options = options;
        this.parameters = parameters;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getName() {
        return this.name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDescription() {
        return this.description;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Options getOptions() {
        return this.options;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Parameters getParameters() {
        return this.parameters;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void run(Deque<String> args) {
        List<String> parameters = new ArrayList<>();
        Map<Option, String> options = new HashMap<>();
        while (!args.isEmpty()) {
            String arg = args.removeFirst();
            Option option = this.options.find(arg);
            if (option != null) {
                options.put(option, option.claimArg(args));
            } else {
                parameters.add(arg);
            }
        }
        run(options, parameters);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Command find(Collection<? extends Command> commands, String name) {
        for (Command command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/Command$Parameters.class */
    protected static final class Parameters {
        private final List<String> descriptions;

        private Parameters(String[] descriptions) {
            this.descriptions = Collections.unmodifiableList(Arrays.asList(descriptions));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public List<String> getDescriptions() {
            return this.descriptions;
        }

        public String toString() {
            return this.descriptions.toString();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Parameters none() {
            return of(new String[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Parameters of(String... descriptions) {
            return new Parameters(descriptions);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/Command$Options.class */
    protected static final class Options {
        private final Option[] values;

        private Options(Option[] values) {
            this.values = values;
        }

        private Option find(String arg) {
            if (arg.startsWith("--")) {
                String name = arg.substring(2);
                for (Option candidate : this.values) {
                    if (candidate.getName().equals(name)) {
                        return candidate;
                    }
                }
                throw new UnknownOptionException(name);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isEmpty() {
            return this.values.length == 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Stream<Option> stream() {
            return Arrays.stream(this.values);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Options none() {
            return of(new Option[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Options of(Option... values) {
            return new Options(values);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/Command$Option.class */
    public static final class Option {
        private final String name;
        private final String valueDescription;
        private final String description;

        private Option(String name, String valueDescription, String description) {
            this.name = name;
            this.description = description;
            this.valueDescription = valueDescription;
        }

        String getName() {
            return this.name;
        }

        String getValueDescription() {
            return this.valueDescription;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getNameAndValueDescription() {
            return this.name + (this.valueDescription != null ? " " + this.valueDescription : "");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getDescription() {
            return this.description;
        }

        private String claimArg(Deque<String> args) {
            if (this.valueDescription != null) {
                if (args.isEmpty()) {
                    throw new MissingValueException(this.name);
                }
                return args.removeFirst();
            }
            return null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.name.equals(((Option) obj).name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }

        protected static Option flag(String name, String description) {
            return new Option(name, null, description);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public static Option of(String name, String valueDescription, String description) {
            return new Option(name, valueDescription, description);
        }
    }
}
