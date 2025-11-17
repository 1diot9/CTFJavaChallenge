package jakarta.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/StandardELContext.class */
public class StandardELContext extends ELContext {
    private final ELContext wrappedContext;
    private final VariableMapper variableMapper;
    private final FunctionMapper functionMapper;
    private final CompositeELResolver standardResolver;
    private final CompositeELResolver customResolvers;
    private final Map<String, Object> localBeans;

    public StandardELContext(ExpressionFactory factory) {
        this.localBeans = new HashMap();
        this.wrappedContext = null;
        this.variableMapper = new StandardVariableMapper();
        this.functionMapper = new StandardFunctionMapper(factory.getInitFunctionMap());
        this.standardResolver = new CompositeELResolver();
        this.customResolvers = new CompositeELResolver();
        ELResolver streamResolver = factory.getStreamELResolver();
        this.standardResolver.add(new BeanNameELResolver(new StandardBeanNameResolver(this.localBeans)));
        this.standardResolver.add(this.customResolvers);
        if (streamResolver != null) {
            this.standardResolver.add(streamResolver);
        }
        this.standardResolver.add(new StaticFieldELResolver());
        this.standardResolver.add(new MapELResolver());
        this.standardResolver.add(new ResourceBundleELResolver());
        this.standardResolver.add(new ListELResolver());
        this.standardResolver.add(new ArrayELResolver());
        this.standardResolver.add(new BeanELResolver());
    }

    public StandardELContext(ELContext context) {
        this.localBeans = new HashMap();
        this.wrappedContext = context;
        this.variableMapper = context.getVariableMapper();
        this.functionMapper = context.getFunctionMapper();
        this.standardResolver = new CompositeELResolver();
        this.customResolvers = new CompositeELResolver();
        this.standardResolver.add(new BeanNameELResolver(new StandardBeanNameResolver(this.localBeans)));
        this.standardResolver.add(this.customResolvers);
        this.standardResolver.add(context.getELResolver());
    }

    @Override // jakarta.el.ELContext
    public void putContext(Class<?> key, Object contextObject) {
        if (this.wrappedContext == null) {
            super.putContext(key, contextObject);
        } else {
            this.wrappedContext.putContext(key, contextObject);
        }
    }

    @Override // jakarta.el.ELContext
    public Object getContext(Class<?> key) {
        if (this.wrappedContext == null) {
            return super.getContext(key);
        }
        return this.wrappedContext.getContext(key);
    }

    @Override // jakarta.el.ELContext
    public ELResolver getELResolver() {
        return this.standardResolver;
    }

    public void addELResolver(ELResolver resolver) {
        this.customResolvers.add(resolver);
    }

    @Override // jakarta.el.ELContext
    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }

    @Override // jakarta.el.ELContext
    public VariableMapper getVariableMapper() {
        return this.variableMapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, Object> getLocalBeans() {
        return this.localBeans;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/StandardELContext$StandardVariableMapper.class */
    private static class StandardVariableMapper extends VariableMapper {
        private Map<String, ValueExpression> vars;

        private StandardVariableMapper() {
        }

        @Override // jakarta.el.VariableMapper
        public ValueExpression resolveVariable(String variable) {
            if (this.vars == null) {
                return null;
            }
            return this.vars.get(variable);
        }

        @Override // jakarta.el.VariableMapper
        public ValueExpression setVariable(String variable, ValueExpression expression) {
            if (this.vars == null) {
                this.vars = new HashMap();
            }
            if (expression == null) {
                return this.vars.remove(variable);
            }
            return this.vars.put(variable, expression);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/StandardELContext$StandardBeanNameResolver.class */
    private static class StandardBeanNameResolver extends BeanNameResolver {
        private final Map<String, Object> beans;

        StandardBeanNameResolver(Map<String, Object> beans) {
            this.beans = beans;
        }

        @Override // jakarta.el.BeanNameResolver
        public boolean isNameResolved(String beanName) {
            return this.beans.containsKey(beanName);
        }

        @Override // jakarta.el.BeanNameResolver
        public Object getBean(String beanName) {
            return this.beans.get(beanName);
        }

        @Override // jakarta.el.BeanNameResolver
        public void setBeanValue(String beanName, Object value) throws PropertyNotWritableException {
            this.beans.put(beanName, value);
        }

        @Override // jakarta.el.BeanNameResolver
        public boolean isReadOnly(String beanName) {
            return false;
        }

        @Override // jakarta.el.BeanNameResolver
        public boolean canCreateBean(String beanName) {
            return true;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:jakarta/el/StandardELContext$StandardFunctionMapper.class */
    private static class StandardFunctionMapper extends FunctionMapper {
        private final Map<String, Method> methods = new HashMap();

        StandardFunctionMapper(Map<String, Method> initFunctionMap) {
            if (initFunctionMap != null) {
                this.methods.putAll(initFunctionMap);
            }
        }

        @Override // jakarta.el.FunctionMapper
        public Method resolveFunction(String prefix, String localName) {
            String key = prefix + ":" + localName;
            return this.methods.get(key);
        }

        @Override // jakarta.el.FunctionMapper
        public void mapFunction(String prefix, String localName, Method method) {
            String key = prefix + ":" + localName;
            if (method == null) {
                this.methods.remove(key);
            } else {
                this.methods.put(key, method);
            }
        }
    }
}
