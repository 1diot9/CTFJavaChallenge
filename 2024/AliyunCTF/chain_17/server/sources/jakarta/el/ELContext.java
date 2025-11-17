package jakarta.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/ELContext.class */
public abstract class ELContext {
    private Locale locale;
    private Map<Class<?>, Object> map;
    private List<EvaluationListener> listeners;
    private ImportHandler importHandler = null;
    private Deque<Map<String, Object>> lambdaArguments = new ArrayDeque();
    private boolean resolved = false;

    public abstract ELResolver getELResolver();

    public abstract FunctionMapper getFunctionMapper();

    public abstract VariableMapper getVariableMapper();

    public void setPropertyResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setPropertyResolved(Object base, Object property) {
        setPropertyResolved(true);
        notifyPropertyResolved(base, property);
    }

    public boolean isPropertyResolved() {
        return this.resolved;
    }

    public void putContext(Class<?> key, Object contextObject) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(contextObject);
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(key, contextObject);
    }

    public Object getContext(Class<?> key) {
        Objects.requireNonNull(key);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public ImportHandler getImportHandler() {
        if (this.importHandler == null) {
            this.importHandler = new ImportHandler();
        }
        return this.importHandler;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void addEvaluationListener(EvaluationListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    public List<EvaluationListener> getEvaluationListeners() {
        return this.listeners == null ? Collections.emptyList() : this.listeners;
    }

    public void notifyBeforeEvaluation(String expression) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.beforeEvaluation(this, expression);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyAfterEvaluation(String expression) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.afterEvaluation(this, expression);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyPropertyResolved(Object base, Object property) {
        if (this.listeners == null) {
            return;
        }
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.propertyResolved(this, base, property);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public boolean isLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            if (arguments.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public Object getLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            Object result = arguments.get(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void enterLambdaScope(Map<String, Object> arguments) {
        this.lambdaArguments.push(arguments);
    }

    public void exitLambdaScope() {
        this.lambdaArguments.pop();
    }

    public <T> T convertToType(Object obj, Class<T> cls) {
        boolean isPropertyResolved = isPropertyResolved();
        setPropertyResolved(false);
        try {
            ELResolver eLResolver = getELResolver();
            if (eLResolver != null) {
                T t = (T) eLResolver.convertToType(this, obj, cls);
                if (isPropertyResolved()) {
                    return t;
                }
            }
            setPropertyResolved(isPropertyResolved);
            if ((obj instanceof LambdaExpression) && isFunctionalInterface(cls)) {
                ((LambdaExpression) obj).setELContext(this);
            }
            return (T) ELManager.getExpressionFactory().coerceToType(obj, cls);
        } finally {
            setPropertyResolved(isPropertyResolved);
        }
    }

    static boolean isFunctionalInterface(Class<?> type) {
        if (!type.isInterface()) {
            return false;
        }
        boolean foundAbstractMethod = false;
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers()) && !overridesObjectMethod(method)) {
                if (foundAbstractMethod) {
                    return false;
                }
                foundAbstractMethod = true;
            }
        }
        return foundAbstractMethod;
    }

    private static boolean overridesObjectMethod(Method method) {
        if ("equals".equals(method.getName())) {
            if (method.getReturnType().equals(Boolean.TYPE) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(Object.class)) {
                return true;
            }
            return false;
        }
        if (IdentityNamingStrategy.HASH_CODE_KEY.equals(method.getName())) {
            if (method.getReturnType().equals(Integer.TYPE) && method.getParameterCount() == 0) {
                return true;
            }
            return false;
        }
        if ("toString".equals(method.getName()) && method.getReturnType().equals(String.class) && method.getParameterCount() == 0) {
            return true;
        }
        return false;
    }
}
