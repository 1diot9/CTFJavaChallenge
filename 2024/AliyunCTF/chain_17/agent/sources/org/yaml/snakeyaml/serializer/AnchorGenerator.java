package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.nodes.Node;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/serializer/AnchorGenerator.class */
public interface AnchorGenerator {
    String nextAnchor(Node node);
}
