package org.yaml.snakeyaml.parser;

import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/parser/VersionTagsTuple.class */
public class VersionTagsTuple {
    private final DumperOptions.Version version;
    private final Map<String, String> tags;

    public VersionTagsTuple(DumperOptions.Version version, Map<String, String> tags) {
        this.version = version;
        this.tags = tags;
    }

    public DumperOptions.Version getVersion() {
        return this.version;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public String toString() {
        return String.format("VersionTagsTuple<%s, %s>", this.version, this.tags);
    }
}
