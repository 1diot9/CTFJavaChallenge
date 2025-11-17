package jakarta.el;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/VariableMapper.class */
public abstract class VariableMapper {
    public abstract ValueExpression resolveVariable(String str);

    public abstract ValueExpression setVariable(String str, ValueExpression valueExpression);
}
