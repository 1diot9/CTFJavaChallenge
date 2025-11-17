package com.alibaba.com.caucho.hessian.io;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/StackTraceElementDeserializer.class */
public class StackTraceElementDeserializer extends JavaDeserializer {
    public StackTraceElementDeserializer() {
        super(StackTraceElement.class);
    }

    @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer
    protected Object instantiate() throws Exception {
        return new StackTraceElement("", "", "", 0);
    }
}
