package org.yaml.snakeyaml.tokens;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/tokens/TagTuple.class */
public final class TagTuple {
    private final String handle;
    private final String suffix;

    public TagTuple(String handle, String suffix) {
        if (suffix == null) {
            throw new NullPointerException("Suffix must be provided.");
        }
        this.handle = handle;
        this.suffix = suffix;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getSuffix() {
        return this.suffix;
    }
}
