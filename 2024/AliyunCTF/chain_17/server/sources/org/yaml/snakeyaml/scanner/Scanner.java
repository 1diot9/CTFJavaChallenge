package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.Token;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/scanner/Scanner.class */
public interface Scanner {
    boolean checkToken(Token.ID... idArr);

    Token peekToken();

    Token getToken();

    void resetDocumentIndex();
}
