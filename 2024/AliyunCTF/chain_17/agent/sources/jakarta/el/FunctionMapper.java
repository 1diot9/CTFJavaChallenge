package jakarta.el;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/FunctionMapper.class */
public abstract class FunctionMapper {
    public abstract Method resolveFunction(String str, String str2);

    public void mapFunction(String prefix, String localName, Method method) {
    }
}
