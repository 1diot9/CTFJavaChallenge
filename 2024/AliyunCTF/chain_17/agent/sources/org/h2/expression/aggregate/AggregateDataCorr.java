package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataCorr.class */
final class AggregateDataCorr extends AggregateDataBinarySet {
    private final AggregateType aggregateType;
    private long count;
    private double sumY;
    private double sumX;
    private double sumYX;
    private double m2y;
    private double meanY;
    private double m2x;
    private double meanX;

    /*  JADX ERROR: Failed to decode insn: 0x0037: MOVE_MULTI, method: org.h2.expression.aggregate.AggregateDataCorr.add(org.h2.engine.SessionLocal, org.h2.value.Value, org.h2.value.Value):void
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
    /*  JADX ERROR: Failed to decode insn: 0x004F: MOVE_MULTI, method: org.h2.expression.aggregate.AggregateDataCorr.add(org.h2.engine.SessionLocal, org.h2.value.Value, org.h2.value.Value):void
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
    @Override // org.h2.expression.aggregate.AggregateDataBinarySet
    void add(org.h2.engine.SessionLocal r11, org.h2.value.Value r12, org.h2.value.Value r13) {
        /*
            r10 = this;
            r0 = r12
            double r0 = r0.getDouble()
            r14 = r0
            r0 = r13
            double r0 = r0.getDouble()
            r16 = r0
            r0 = r10
            r1 = r0
            double r1 = r1.sumY
            r2 = r14
            double r1 = r1 + r2
            r0.sumY = r1
            r0 = r10
            r1 = r0
            double r1 = r1.sumX
            r2 = r16
            double r1 = r1 + r2
            r0.sumX = r1
            r0 = r10
            r1 = r0
            double r1 = r1.sumYX
            r2 = r14
            r3 = r16
            double r2 = r2 * r3
            double r1 = r1 + r2
            r0.sumYX = r1
            r0 = r10
            r1 = r0
            long r1 = r1.count
            r2 = 1
            long r1 = r1 + r2
            // decode failed: arraycopy: source index -1 out of bounds for object array[10]
            r0.count = r1
            r0 = 1
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 != 0) goto L59
            r-1 = r10
            r0 = r14
            r-1.meanY = r0
            r-1 = r10
            r0 = r16
            r-1.meanX = r0
            r-1 = r10
            r0 = r10
            r1 = 0
            // decode failed: arraycopy: source index -1 out of bounds for object array[10]
            r0.m2y = r1
            r-2.m2x = r-1
            goto Lb3
            r-1 = r14
            r0 = r10
            double r0 = r0.meanY
            double r-1 = r-1 - r0
            r18 = r-1
            r-1 = r10
            r0 = r-1
            double r0 = r0.meanY
            r1 = r18
            r2 = r10
            long r2 = r2.count
            double r2 = (double) r2
            double r1 = r1 / r2
            double r0 = r0 + r1
            r-1.meanY = r0
            r-1 = r10
            r0 = r-1
            double r0 = r0.m2y
            r1 = r18
            r2 = r14
            r3 = r10
            double r3 = r3.meanY
            double r2 = r2 - r3
            double r1 = r1 * r2
            double r0 = r0 + r1
            r-1.m2y = r0
            r-1 = r16
            r0 = r10
            double r0 = r0.meanX
            double r-1 = r-1 - r0
            r18 = r-1
            r-1 = r10
            r0 = r-1
            double r0 = r0.meanX
            r1 = r18
            r2 = r10
            long r2 = r2.count
            double r2 = (double) r2
            double r1 = r1 / r2
            double r0 = r0 + r1
            r-1.meanX = r0
            r-1 = r10
            r0 = r-1
            double r0 = r0.m2x
            r1 = r18
            r2 = r16
            r3 = r10
            double r3 = r3.meanX
            double r2 = r2 - r3
            double r1 = r1 * r2
            double r0 = r0 + r1
            r-1.m2x = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.aggregate.AggregateDataCorr.add(org.h2.engine.SessionLocal, org.h2.value.Value, org.h2.value.Value):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateDataCorr(AggregateType aggregateType) {
        this.aggregateType = aggregateType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        double d;
        if (this.count < 1) {
            return ValueNull.INSTANCE;
        }
        switch (this.aggregateType) {
            case CORR:
                if (this.m2y == 0.0d || this.m2x == 0.0d) {
                    return ValueNull.INSTANCE;
                }
                d = (this.sumYX - ((this.sumX * this.sumY) / this.count)) / Math.sqrt(this.m2y * this.m2x);
                break;
                break;
            case REGR_SLOPE:
                if (this.m2x == 0.0d) {
                    return ValueNull.INSTANCE;
                }
                d = (this.sumYX - ((this.sumX * this.sumY) / this.count)) / this.m2x;
                break;
            case REGR_INTERCEPT:
                if (this.m2x == 0.0d) {
                    return ValueNull.INSTANCE;
                }
                d = this.meanY - (((this.sumYX - ((this.sumX * this.sumY) / this.count)) / this.m2x) * this.meanX);
                break;
            case REGR_R2:
                if (this.m2x != 0.0d) {
                    if (this.m2y == 0.0d) {
                        return ValueDouble.ONE;
                    }
                    double d2 = this.sumYX - ((this.sumX * this.sumY) / this.count);
                    d = (d2 * d2) / (this.m2y * this.m2x);
                    break;
                } else {
                    return ValueNull.INSTANCE;
                }
            default:
                throw DbException.getInternalError("type=" + this.aggregateType);
        }
        return ValueDouble.get(d);
    }
}
