package org.yaml.snakeyaml.tokens;

import java.util.Objects;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/tokens/CommentToken.class */
public final class CommentToken extends Token {
    private final CommentType type;
    private final String value;

    public CommentToken(CommentType type, String value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(type);
        this.type = type;
        Objects.requireNonNull(value);
        this.value = value;
    }

    public CommentType getCommentType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override // org.yaml.snakeyaml.tokens.Token
    public Token.ID getTokenId() {
        return Token.ID.Comment;
    }
}
