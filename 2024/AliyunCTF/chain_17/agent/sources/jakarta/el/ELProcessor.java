package jakarta.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELProcessor.class */
public class ELProcessor {
    private static final Set<String> PRIMITIVES = new HashSet();
    private static final String[] EMPTY_STRING_ARRAY;
    private final ELManager manager = new ELManager();
    private final ELContext context = this.manager.getELContext();
    private final ExpressionFactory factory = ELManager.getExpressionFactory();

    static {
        PRIMITIVES.add("boolean");
        PRIMITIVES.add("byte");
        PRIMITIVES.add("char");
        PRIMITIVES.add("double");
        PRIMITIVES.add("float");
        PRIMITIVES.add("int");
        PRIMITIVES.add("long");
        PRIMITIVES.add("short");
        EMPTY_STRING_ARRAY = new String[0];
    }

    public ELManager getELManager() {
        return this.manager;
    }

    public <T> T eval(String str) {
        return (T) getValue(str, Object.class);
    }

    public <T> T getValue(String str, Class<T> cls) {
        return (T) this.factory.createValueExpression(this.context, bracket(str), cls).getValue(this.context);
    }

    public void setValue(String expression, Object value) {
        ValueExpression ve = this.factory.createValueExpression(this.context, bracket(expression), Object.class);
        ve.setValue(this.context, value);
    }

    public void setVariable(String variable, String expression) {
        if (expression == null) {
            this.manager.setVariable(variable, null);
        } else {
            ValueExpression ve = this.factory.createValueExpression(this.context, bracket(expression), Object.class);
            this.manager.setVariable(variable, ve);
        }
    }

    public void defineFunction(String prefix, String function, String className, String methodName) throws ClassNotFoundException, NoSuchMethodException {
        if (prefix == null || function == null || className == null || methodName == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        Class<?> clazz = this.context.getImportHandler().resolveClass(className);
        if (clazz == null) {
            clazz = Class.forName(className, true, Util.getContextClassLoader());
        }
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new ClassNotFoundException(Util.message(this.context, "elProcessor.defineFunctionInvalidClass", className));
        }
        MethodSignature sig = new MethodSignature(this.context, methodName, className);
        if (function.length() == 0) {
            function = sig.getName();
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && Util.canAccess(null, method) && method.getName().equals(sig.getName())) {
                if (sig.getParamTypeNames() == null) {
                    this.manager.mapFunction(prefix, function, method);
                    return;
                }
                if (sig.getParamTypeNames().length != method.getParameterTypes().length) {
                    continue;
                } else {
                    if (sig.getParamTypeNames().length == 0) {
                        this.manager.mapFunction(prefix, function, method);
                        return;
                    }
                    Class<?>[] types = method.getParameterTypes();
                    String[] typeNames = sig.getParamTypeNames();
                    if (types.length == typeNames.length) {
                        boolean match = true;
                        int i = 0;
                        while (true) {
                            if (i >= types.length) {
                                break;
                            }
                            if (i != types.length - 1 || !method.isVarArgs()) {
                                if (!types[i].getName().equals(typeNames[i])) {
                                    match = false;
                                    break;
                                }
                            } else {
                                String typeName = typeNames[i];
                                if (typeName.endsWith("...")) {
                                    if (!typeName.substring(0, typeName.length() - 3).equals(types[i].getName())) {
                                        match = false;
                                    }
                                } else {
                                    match = false;
                                }
                            }
                            i++;
                        }
                        if (match) {
                            this.manager.mapFunction(prefix, function, method);
                            return;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionNoMethod", methodName, className));
    }

    public void defineFunction(String prefix, String function, Method method) throws NoSuchMethodException {
        if (prefix == null || function == null || method == null) {
            throw new NullPointerException(Util.message(this.context, "elProcessor.defineFunctionNullParams", new Object[0]));
        }
        int modifiers = method.getModifiers();
        if (!Modifier.isStatic(modifiers) || !Util.canAccess(null, method)) {
            throw new NoSuchMethodException(Util.message(this.context, "elProcessor.defineFunctionInvalidMethod", method.getName(), method.getDeclaringClass().getName()));
        }
        this.manager.mapFunction(prefix, function, method);
    }

    public void defineBean(String name, Object bean) {
        this.manager.defineBean(name, bean);
    }

    private static String bracket(String expression) {
        return "${" + expression + "}";
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELProcessor$MethodSignature.class */
    private static class MethodSignature {
        private final String name;
        private final String[] parameterTypeNames;

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:71:0x0245, code lost:            switch(r26) {            case 0: goto L68;            case 1: goto L69;            case 2: goto L70;            case 3: goto L71;            case 4: goto L72;            case 5: goto L73;            case 6: goto L74;            case 7: goto L75;            default: goto L86;        };     */
        /* JADX WARN: Code restructure failed: missing block: B:72:0x0274, code lost:            r20 = "Z";     */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x027b, code lost:            r20 = "B";     */
        /* JADX WARN: Code restructure failed: missing block: B:74:0x0282, code lost:            r20 = "C";     */
        /* JADX WARN: Code restructure failed: missing block: B:75:0x0289, code lost:            r20 = "D";     */
        /* JADX WARN: Code restructure failed: missing block: B:76:0x0290, code lost:            r20 = "F";     */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x0297, code lost:            r20 = "I";     */
        /* JADX WARN: Code restructure failed: missing block: B:78:0x029e, code lost:            r20 = "J";     */
        /* JADX WARN: Code restructure failed: missing block: B:79:0x02a5, code lost:            r20 = "S";     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        MethodSignature(jakarta.el.ELContext r11, java.lang.String r12, java.lang.String r13) throws java.lang.NoSuchMethodException {
            /*
                Method dump skipped, instructions count: 868
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: jakarta.el.ELProcessor.MethodSignature.<init>(jakarta.el.ELContext, java.lang.String, java.lang.String):void");
        }

        public String getName() {
            return this.name;
        }

        public String[] getParamTypeNames() {
            return this.parameterTypeNames;
        }
    }
}
