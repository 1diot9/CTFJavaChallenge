package com.fasterxml.jackson.core.sym;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/Name1.class */
public final class Name1 extends Name {
    private static final Name1 EMPTY = new Name1("", 0, 0);
    private final int q;

    Name1(String name, int hash, int quad) {
        super(name, hash);
        this.q = quad;
    }

    public static Name1 getEmptyName() {
        return EMPTY;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad) {
        return quad == this.q;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2) {
        return quad1 == this.q && quad2 == 0;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int q1, int q2, int q3) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] quads, int qlen) {
        return qlen == 1 && quads[0] == this.q;
    }
}
