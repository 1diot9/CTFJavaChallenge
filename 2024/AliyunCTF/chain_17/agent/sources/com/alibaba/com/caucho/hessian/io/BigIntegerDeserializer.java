package com.alibaba.com.caucho.hessian.io;

import java.math.BigInteger;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BigIntegerDeserializer.class */
public class BigIntegerDeserializer extends JavaDeserializer {
    public BigIntegerDeserializer() {
        super(BigInteger.class);
    }

    @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer
    protected Object instantiate() throws Exception {
        return new BigInteger(CustomBooleanEditor.VALUE_0);
    }
}
