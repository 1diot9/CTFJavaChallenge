package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.nodes.Node;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/serializer/AnchorGenerator.class */
public interface AnchorGenerator {
    String nextAnchor(Node node);
}
