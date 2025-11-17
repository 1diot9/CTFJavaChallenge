package org.apache.el.lang;

import jakarta.el.FunctionMapper;
import java.lang.reflect.Method;
import org.apache.el.util.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/FunctionMapperFactory.class */
public class FunctionMapperFactory extends FunctionMapper {
    protected FunctionMapperImpl memento = null;
    protected final FunctionMapper target;

    public FunctionMapperFactory(FunctionMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException(MessageFactory.get("error.noFunctionMapperTarget"));
        }
        this.target = mapper;
    }

    @Override // jakarta.el.FunctionMapper
    public Method resolveFunction(String prefix, String localName) {
        if (this.memento == null) {
            this.memento = new FunctionMapperImpl();
        }
        Method m = this.target.resolveFunction(prefix, localName);
        if (m != null) {
            this.memento.mapFunction(prefix, localName, m);
        }
        return m;
    }

    @Override // jakarta.el.FunctionMapper
    public void mapFunction(String prefix, String localName, Method method) {
        if (this.memento == null) {
            this.memento = new FunctionMapperImpl();
        }
        this.memento.mapFunction(prefix, localName, method);
    }

    public FunctionMapper create() {
        return this.memento;
    }
}
