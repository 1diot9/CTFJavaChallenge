package org.springframework.boot.jarmode.layertools;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.boot.jarmode.layertools.Command;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/HelpCommand.class */
class HelpCommand extends Command {
    private final Context context;
    private final List<Command> commands;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HelpCommand(Context context, List<Command> commands) {
        super("help", "Help about any command", Command.Options.none(), Command.Parameters.of("[<command]"));
        this.context = context;
        this.commands = commands;
    }

    @Override // org.springframework.boot.jarmode.layertools.Command
    protected void run(Map<Command.Option, String> options, List<String> parameters) {
        run(System.out, parameters);
    }

    void run(PrintStream out, List<String> parameters) {
        Command command = !parameters.isEmpty() ? Command.find(this.commands, parameters.get(0)) : null;
        if (command != null) {
            printCommandHelp(out, command);
        } else {
            printUsageAndCommands(out);
        }
    }

    private void printCommandHelp(PrintStream out, Command command) {
        out.println(command.getDescription());
        out.println();
        out.println("Usage:");
        out.println("  " + getJavaCommand() + " " + getUsage(command));
        if (!command.getOptions().isEmpty()) {
            out.println();
            out.println("Options:");
            int maxNameLength = getMaxLength(0, command.getOptions().stream().map((v0) -> {
                return v0.getNameAndValueDescription();
            }));
            command.getOptions().stream().forEach(option -> {
                printOptionSummary(out, option, maxNameLength);
            });
        }
    }

    private void printOptionSummary(PrintStream out, Command.Option option, int padding) {
        out.printf("  --%-" + padding + "s  %s%n", option.getNameAndValueDescription(), option.getDescription());
    }

    private String getUsage(Command command) {
        StringBuilder usage = new StringBuilder();
        usage.append(command.getName());
        if (!command.getOptions().isEmpty()) {
            usage.append(" [options]");
        }
        command.getParameters().getDescriptions().forEach(param -> {
            usage.append(" ").append(param);
        });
        return usage.toString();
    }

    private void printUsageAndCommands(PrintStream out) {
        out.println("Usage:");
        out.println("  " + getJavaCommand());
        out.println();
        out.println("Available commands:");
        int maxNameLength = getMaxLength(getName().length(), this.commands.stream().map((v0) -> {
            return v0.getName();
        }));
        this.commands.forEach(command -> {
            printCommandSummary(out, command, maxNameLength);
        });
        printCommandSummary(out, this, maxNameLength);
    }

    private int getMaxLength(int minimum, Stream<String> strings) {
        return Math.max(minimum, strings.mapToInt((v0) -> {
            return v0.length();
        }).max().orElse(0));
    }

    private void printCommandSummary(PrintStream out, Command command, int padding) {
        out.printf("  %-" + padding + "s  %s%n", command.getName(), command.getDescription());
    }

    private String getJavaCommand() {
        return "java -Djarmode=layertools -jar " + this.context.getArchiveFile().getName();
    }
}
