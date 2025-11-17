package ch.qos.logback.core.util;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/DefaultInvocationGate.class */
public class DefaultInvocationGate implements InvocationGate {
    static final int MASK_DECREASE_RIGHT_SHIFT_COUNT = 2;
    private static final int MAX_MASK = 65535;
    static final int DEFAULT_MASK = 15;
    private volatile long mask;
    private long invocationCounter;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final long MASK_DECREASE_THRESHOLD = 800;
    private long minDelayThreshold;
    private long maxDelayThreshold;
    long lowerLimitForMaskMatch;
    long upperLimitForNoMaskMatch;

    /*  JADX ERROR: Failed to decode insn: 0x0005: MOVE_MULTI, method: ch.qos.logback.core.util.DefaultInvocationGate.isTooSoon(long):boolean
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[8]
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
    @Override // ch.qos.logback.core.util.InvocationGate
    public final boolean isTooSoon(long r9) {
        /*
            r8 = this;
            r0 = r8
            r1 = r0
            long r1 = r1.invocationCounter
            // decode failed: arraycopy: source index -1 out of bounds for object array[8]
            r2 = 1
            long r1 = r1 + r2
            r0.invocationCounter = r1
            r0 = r8
            long r0 = r0.mask
            long r-1 = r-1 & r0
            r0 = r8
            long r0 = r0.mask
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 != 0) goto L1c
            r-1 = 1
            goto L1d
            r-1 = 0
            r11 = r-1
            r-1 = r11
            if (r-1 == 0) goto L37
            r-1 = r9
            r0 = r8
            long r0 = r0.lowerLimitForMaskMatch
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 >= 0) goto L2f
            r-1 = r8
            r-1.increaseMask()
            r-1 = r8
            r0 = r9
            r-1.updateLimits(r0)
            goto L4b
            r-1 = r9
            r0 = r8
            long r0 = r0.upperLimitForNoMaskMatch
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 <= 0) goto L4b
            r-1 = r8
            r-1.decreaseMask()
            r-1 = r8
            r0 = r9
            r-1.updateLimits(r0)
            r-1 = 0
            return r-1
            r-1 = r11
            if (r-1 != 0) goto L53
            r-1 = 1
            goto L54
            r-1 = 0
            return r-1
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.core.util.DefaultInvocationGate.isTooSoon(long):boolean");
    }

    public DefaultInvocationGate() {
        this(100L, MASK_DECREASE_THRESHOLD, System.currentTimeMillis());
    }

    public DefaultInvocationGate(long minDelayThreshold, long maxDelayThreshold, long currentTime) {
        this.mask = 15L;
        this.invocationCounter = 0L;
        this.minDelayThreshold = minDelayThreshold;
        this.maxDelayThreshold = maxDelayThreshold;
        this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
    }

    private void updateLimits(long currentTime) {
        this.lowerLimitForMaskMatch = currentTime + this.minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + this.maxDelayThreshold;
    }

    long getMask() {
        return this.mask;
    }

    private void increaseMask() {
        if (this.mask >= 65535) {
            return;
        }
        this.mask = (this.mask << 1) | 1;
    }

    private void decreaseMask() {
        this.mask >>>= 2;
    }

    public long getInvocationCounter() {
        return this.invocationCounter;
    }
}
