package org.yaml.snakeyaml.tokens;

import ch.qos.logback.core.CoreConstants;
import org.springframework.beans.PropertyAccessor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/tokens/Token.class */
public abstract class Token {
    private final Mark startMark;
    private final Mark endMark;

    public abstract ID getTokenId();

    /* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/tokens/Token$ID.class */
    public enum ID {
        Alias("<alias>"),
        Anchor("<anchor>"),
        BlockEnd("<block end>"),
        BlockEntry("-"),
        BlockMappingStart("<block mapping start>"),
        BlockSequenceStart("<block sequence start>"),
        Directive("<directive>"),
        DocumentEnd("<document end>"),
        DocumentStart("<document start>"),
        FlowEntry(","),
        FlowMappingEnd("}"),
        FlowMappingStart("{"),
        FlowSequenceEnd("]"),
        FlowSequenceStart(PropertyAccessor.PROPERTY_KEY_PREFIX),
        Key(CoreConstants.NA),
        Scalar("<scalar>"),
        StreamEnd("<stream end>"),
        StreamStart("<stream start>"),
        Tag("<tag>"),
        Value(":"),
        Whitespace("<whitespace>"),
        Comment("#"),
        Error("<error>");

        private final String description;

        ID(String s) {
            this.description = s;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }
    }

    public Token(Mark startMark, Mark endMark) {
        if (startMark == null || endMark == null) {
            throw new YAMLException("Token requires marks.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public Mark getStartMark() {
        return this.startMark;
    }

    public Mark getEndMark() {
        return this.endMark;
    }
}
