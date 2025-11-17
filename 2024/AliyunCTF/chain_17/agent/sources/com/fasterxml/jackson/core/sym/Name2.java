package com.fasterxml.jackson.core.sym;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/Name2.class */
public final class Name2 extends Name {
    private final int q1;
    private final int q2;

    Name2(String name, int hash, int quad1, int quad2) {
        super(name, hash);
        this.q1 = quad1;
        this.q2 = quad2;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2) {
        return quad1 == this.q1 && quad2 == this.q2;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2, int q3) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] quads, int qlen) {
        return qlen == 2 && quads[0] == this.q1 && quads[1] == this.q2;
    }
}
