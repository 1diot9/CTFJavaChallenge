package com.fasterxml.jackson.core.sym;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/Name3.class */
public final class Name3 extends Name {
    private final int q1;
    private final int q2;
    private final int q3;

    Name3(String name, int hash, int i1, int i2, int i3) {
        super(name, hash);
        this.q1 = i1;
        this.q2 = i2;
        this.q3 = i3;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2, int quad3) {
        return this.q1 == quad1 && this.q2 == quad2 && this.q3 == quad3;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] quads, int qlen) {
        return qlen == 3 && quads[0] == this.q1 && quads[1] == this.q2 && quads[2] == this.q3;
    }
}
