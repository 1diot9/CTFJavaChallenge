package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataStdVar.class */
final class AggregateDataStdVar extends AggregateData {
    private final AggregateType aggregateType;
    private long count;
    private double m2;
    private double mean;

    /*  JADX ERROR: Failed to decode insn: 0x0014: MOVE_MULTI, method: org.h2.expression.aggregate.AggregateDataStdVar.add(org.h2.engine.SessionLocal, org.h2.value.Value):void
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[10]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    @Override // org.h2.expression.aggregate.AggregateData
    void add(org.h2.engine.SessionLocal r11, org.h2.value.Value r12) {
        /*
            r10 = this;
            r0 = r12
            org.h2.value.ValueNull r1 = org.h2.value.ValueNull.INSTANCE
            if (r0 != r1) goto L8
            return
            r0 = r12
            double r0 = r0.getDouble()
            r13 = r0
            r0 = r10
            r1 = r0
            long r1 = r1.count
            r2 = 1
            long r1 = r1 + r2
            // decode failed: arraycopy: source index -1 out of bounds for object array[10]
            r0.count = r1
            r0 = 1
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 != 0) goto L2a
            r-1 = r10
            r0 = r13
            r-1.mean = r0
            r-1 = r10
            r0 = 0
            r-1.m2 = r0
            goto L55
            r-1 = r13
            r0 = r10
            double r0 = r0.mean
            double r-1 = r-1 - r0
            r15 = r-1
            r-1 = r10
            r0 = r-1
            double r0 = r0.mean
            r1 = r15
            r2 = r10
            long r2 = r2.count
            double r2 = (double) r2
            double r1 = r1 / r2
            double r0 = r0 + r1
            r-1.mean = r0
            r-1 = r10
            r0 = r-1
            double r0 = r0.m2
            r1 = r15
            r2 = r13
            r3 = r10
            double r3 = r3.mean
            double r2 = r2 - r3
            double r1 = r1 * r2
            double r0 = r0 + r1
            r-1.m2 = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.aggregate.AggregateDataStdVar.add(org.h2.engine.SessionLocal, org.h2.value.Value):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataStdVar(AggregateType aggregateType) {
        this.aggregateType = aggregateType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        double d;
        switch (this.aggregateType) {
            case STDDEV_SAMP:
            case VAR_SAMP:
                if (this.count < 2) {
                    return ValueNull.INSTANCE;
                }
                d = this.m2 / (this.count - 1);
                if (this.aggregateType == AggregateType.STDDEV_SAMP) {
                    d = Math.sqrt(d);
                    break;
                }
                break;
            case STDDEV_POP:
            case VAR_POP:
                if (this.count < 1) {
                    return ValueNull.INSTANCE;
                }
                d = this.m2 / this.count;
                if (this.aggregateType == AggregateType.STDDEV_POP) {
                    d = Math.sqrt(d);
                    break;
                }
                break;
            case REGR_SXX:
            case REGR_SYY:
                if (this.count < 1) {
                    return ValueNull.INSTANCE;
                }
                d = this.m2;
                break;
            default:
                throw DbException.getInternalError("type=" + this.aggregateType);
        }
        return ValueDouble.get(d);
    }
}
