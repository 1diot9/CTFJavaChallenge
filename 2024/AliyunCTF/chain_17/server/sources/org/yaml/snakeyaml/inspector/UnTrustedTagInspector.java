package org.yaml.snakeyaml.inspector;

import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/inspector/UnTrustedTagInspector.class */
public final class UnTrustedTagInspector implements TagInspector {
    @Override // org.yaml.snakeyaml.inspector.TagInspector
    public boolean isGlobalTagAllowed(Tag tag) {
        return false;
    }
}
