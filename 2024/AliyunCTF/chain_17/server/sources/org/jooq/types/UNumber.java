package org.jooq.types;

import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/types/UNumber.class */
public abstract class UNumber extends Number {
    private static final long serialVersionUID = -7666221938815339843L;

    public BigInteger toBigInteger() {
        return new BigInteger(toString());
    }
}
