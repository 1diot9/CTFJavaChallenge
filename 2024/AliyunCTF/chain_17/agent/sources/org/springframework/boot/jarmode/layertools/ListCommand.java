package org.springframework.boot.jarmode.layertools;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.jarmode.layertools.Command;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-3.2.2.jar:org/springframework/boot/jarmode/layertools/ListCommand.class */
class ListCommand extends Command {
    private final Context context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListCommand(Context context) {
        super(BeanDefinitionParserDelegate.LIST_ELEMENT, "List layers from the jar that can be extracted", Command.Options.none(), Command.Parameters.none());
        this.context = context;
    }

    @Override // org.springframework.boot.jarmode.layertools.Command
    protected void run(Map<Command.Option, String> options, List<String> parameters) {
        printLayers(Layers.get(this.context), System.out);
    }

    void printLayers(Layers layers, PrintStream out) {
        Objects.requireNonNull(out);
        layers.forEach(out::println);
    }
}
