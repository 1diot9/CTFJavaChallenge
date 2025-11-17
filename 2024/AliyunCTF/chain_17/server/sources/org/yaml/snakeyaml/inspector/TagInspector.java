package org.yaml.snakeyaml.inspector;

import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/inspector/TagInspector.class */
public interface TagInspector {
    boolean isGlobalTagAllowed(Tag tag);
}
