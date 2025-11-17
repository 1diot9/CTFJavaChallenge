package org.jooq.conf;

import java.util.regex.Pattern;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/MappedSchemaObject.class */
public interface MappedSchemaObject {
    String getInput();

    Pattern getInputExpression();

    String getOutput();
}
