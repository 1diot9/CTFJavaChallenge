package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/tokens/TagToken.class */
public final class TagToken extends Token {
    private final TagTuple value;

    public TagToken(TagTuple value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }

    public TagTuple getValue() {
        return this.value;
    }

    @Override // org.yaml.snakeyaml.tokens.Token
    public Token.ID getTokenId() {
        return Token.ID.Tag;
    }
}
