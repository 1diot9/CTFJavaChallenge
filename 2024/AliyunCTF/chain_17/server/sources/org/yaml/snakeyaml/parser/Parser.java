package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/parser/Parser.class */
public interface Parser {
    boolean checkEvent(Event.ID id);

    Event peekEvent();

    Event getEvent();
}
