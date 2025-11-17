package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyContainer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ClassBodyEvaluator;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/conditional/PropertyEvalScriptBuilder.class */
public class PropertyEvalScriptBuilder extends ContextAwareBase {
    private static String SCRIPT_PREFIX = "public boolean evaluate() { return ";
    private static String SCRIPT_SUFFIX = "; }";
    final PropertyContainer localPropContainer;
    Map<String, String> map = new HashMap();

    public PropertyEvalScriptBuilder(PropertyContainer localPropContainer) {
        this.localPropContainer = localPropContainer;
    }

    public Condition build(String script) throws IllegalAccessException, CompileException, InstantiationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        ClassBodyEvaluator cbe = new ClassBodyEvaluator();
        cbe.setImplementedInterfaces(new Class[]{Condition.class});
        cbe.setExtendedClass(PropertyWrapperForScripts.class);
        cbe.setParentClassLoader(ClassBodyEvaluator.class.getClassLoader());
        cbe.cook(SCRIPT_PREFIX + script + SCRIPT_SUFFIX);
        Class<?> clazz = cbe.getClazz();
        Condition instance = (Condition) clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        Method setMapMethod = clazz.getMethod("setPropertyContainers", PropertyContainer.class, PropertyContainer.class);
        setMapMethod.invoke(instance, this.localPropContainer, this.context);
        return instance;
    }
}
