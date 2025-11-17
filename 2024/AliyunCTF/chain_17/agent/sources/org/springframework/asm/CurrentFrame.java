package org.springframework.asm;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/CurrentFrame.class */
final class CurrentFrame extends Frame {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentFrame(final Label owner) {
        super(owner);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.asm.Frame
    public void execute(final int opcode, final int arg, final Symbol symbolArg, final SymbolTable symbolTable) {
        super.execute(opcode, arg, symbolArg, symbolTable);
        Frame successor = new Frame(null);
        merge(symbolTable, successor, 0);
        copyFrom(successor);
    }
}
