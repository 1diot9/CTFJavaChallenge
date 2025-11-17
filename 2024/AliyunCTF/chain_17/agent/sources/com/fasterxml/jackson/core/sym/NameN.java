package com.fasterxml.jackson.core.sym;

import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/sym/NameN.class */
public final class NameN extends Name {
    private final int q1;
    private final int q2;
    private final int q3;
    private final int q4;
    private final int qlen;
    private final int[] q;

    NameN(String name, int hash, int q1, int q2, int q3, int q4, int[] quads, int quadLen) {
        super(name, hash);
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q = quads;
        this.qlen = quadLen;
    }

    public static NameN construct(String name, int hash, int[] q, int qlen) {
        int[] buf;
        if (qlen < 4) {
            throw new IllegalArgumentException();
        }
        int q1 = q[0];
        int q2 = q[1];
        int q3 = q[2];
        int q4 = q[3];
        int rem = qlen - 4;
        if (rem > 0) {
            buf = Arrays.copyOfRange(q, 4, qlen);
        } else {
            buf = null;
        }
        return new NameN(name, hash, q1, q2, q3, q4, buf, qlen);
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
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:18:0x003b. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:26:0x007e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x008c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x009a A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    @Override // com.fasterxml.jackson.core.sym.Name
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(int[] r5, int r6) {
        /*
            r4 = this;
            r0 = r6
            r1 = r4
            int r1 = r1.qlen
            if (r0 == r1) goto La
            r0 = 0
            return r0
        La:
            r0 = r5
            r1 = 0
            r0 = r0[r1]
            r1 = r4
            int r1 = r1.q1
            if (r0 == r1) goto L16
            r0 = 0
            return r0
        L16:
            r0 = r5
            r1 = 1
            r0 = r0[r1]
            r1 = r4
            int r1 = r1.q2
            if (r0 == r1) goto L22
            r0 = 0
            return r0
        L22:
            r0 = r5
            r1 = 2
            r0 = r0[r1]
            r1 = r4
            int r1 = r1.q3
            if (r0 == r1) goto L2e
            r0 = 0
            return r0
        L2e:
            r0 = r5
            r1 = 3
            r0 = r0[r1]
            r1 = r4
            int r1 = r1.q4
            if (r0 == r1) goto L3a
            r0 = 0
            return r0
        L3a:
            r0 = r6
            switch(r0) {
                case 4: goto L9c;
                case 5: goto L8e;
                case 6: goto L80;
                case 7: goto L71;
                case 8: goto L62;
                default: goto L5c;
            }
        L5c:
            r0 = r4
            r1 = r5
            boolean r0 = r0._equals2(r1)
            return r0
        L62:
            r0 = r5
            r1 = 7
            r0 = r0[r1]
            r1 = r4
            int[] r1 = r1.q
            r2 = 3
            r1 = r1[r2]
            if (r0 == r1) goto L71
            r0 = 0
            return r0
        L71:
            r0 = r5
            r1 = 6
            r0 = r0[r1]
            r1 = r4
            int[] r1 = r1.q
            r2 = 2
            r1 = r1[r2]
            if (r0 == r1) goto L80
            r0 = 0
            return r0
        L80:
            r0 = r5
            r1 = 5
            r0 = r0[r1]
            r1 = r4
            int[] r1 = r1.q
            r2 = 1
            r1 = r1[r2]
            if (r0 == r1) goto L8e
            r0 = 0
            return r0
        L8e:
            r0 = r5
            r1 = 4
            r0 = r0[r1]
            r1 = r4
            int[] r1 = r1.q
            r2 = 0
            r1 = r1[r2]
            if (r0 == r1) goto L9c
            r0 = 0
            return r0
        L9c:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.sym.NameN.equals(int[], int):boolean");
    }

    private final boolean _equals2(int[] quads) {
        int end = this.qlen - 4;
        for (int i = 0; i < end; i++) {
            if (quads[i + 4] != this.q[i]) {
                return false;
            }
        }
        return true;
    }
}
