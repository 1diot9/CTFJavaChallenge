package org.apache.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/naming/SelectorContext.class */
public class SelectorContext implements Context {
    public static final String prefix = "java:";
    public static final String IC_PREFIX = "IC_";
    protected final Hashtable<String, Object> env;
    protected final boolean initialContext;
    public static final int prefixLength = "java:".length();
    private static final Log log = LogFactory.getLog((Class<?>) SelectorContext.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) SelectorContext.class);

    public SelectorContext(Hashtable<String, Object> env) {
        this.env = env;
        this.initialContext = false;
    }

    public SelectorContext(Hashtable<String, Object> env, boolean initialContext) {
        this.env = env;
        this.initialContext = initialContext;
    }

    public Object lookup(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingName", "lookup", name));
        }
        return getBoundContext().lookup(parseName(name));
    }

    public Object lookup(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingString", "lookup", name));
        }
        return getBoundContext().lookup(parseName(name));
    }

    public void bind(Name name, Object obj) throws NamingException {
        getBoundContext().bind(parseName(name), obj);
    }

    public void bind(String name, Object obj) throws NamingException {
        getBoundContext().bind(parseName(name), obj);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        getBoundContext().rebind(parseName(name), obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        getBoundContext().rebind(parseName(name), obj);
    }

    public void unbind(Name name) throws NamingException {
        getBoundContext().unbind(parseName(name));
    }

    public void unbind(String name) throws NamingException {
        getBoundContext().unbind(parseName(name));
    }

    public void rename(Name oldName, Name newName) throws NamingException {
        getBoundContext().rename(parseName(oldName), parseName(newName));
    }

    public void rename(String oldName, String newName) throws NamingException {
        getBoundContext().rename(parseName(oldName), parseName(newName));
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingName", BeanDefinitionParserDelegate.LIST_ELEMENT, name));
        }
        return getBoundContext().list(parseName(name));
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingString", BeanDefinitionParserDelegate.LIST_ELEMENT, name));
        }
        return getBoundContext().list(parseName(name));
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingName", "listBindings", name));
        }
        return getBoundContext().listBindings(parseName(name));
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingString", "listBindings", name));
        }
        return getBoundContext().listBindings(parseName(name));
    }

    public void destroySubcontext(Name name) throws NamingException {
        getBoundContext().destroySubcontext(parseName(name));
    }

    public void destroySubcontext(String name) throws NamingException {
        getBoundContext().destroySubcontext(parseName(name));
    }

    public Context createSubcontext(Name name) throws NamingException {
        return getBoundContext().createSubcontext(parseName(name));
    }

    public Context createSubcontext(String name) throws NamingException {
        return getBoundContext().createSubcontext(parseName(name));
    }

    public Object lookupLink(Name name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingName", "lookupLink", name));
        }
        return getBoundContext().lookupLink(parseName(name));
    }

    public Object lookupLink(String name) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("selectorContext.methodUsingString", "lookupLink", name));
        }
        return getBoundContext().lookupLink(parseName(name));
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return getBoundContext().getNameParser(parseName(name));
    }

    public NameParser getNameParser(String name) throws NamingException {
        return getBoundContext().getNameParser(parseName(name));
    }

    public Name composeName(Name name, Name prefix2) throws NamingException {
        Name prefixClone = (Name) prefix2.clone();
        return prefixClone.addAll(name);
    }

    public String composeName(String name, String prefix2) throws NamingException {
        return prefix2 + "/" + name;
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return getBoundContext().addToEnvironment(propName, propVal);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return getBoundContext().removeFromEnvironment(propName);
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return getBoundContext().getEnvironment();
    }

    public void close() throws NamingException {
        getBoundContext().close();
    }

    public String getNameInNamespace() throws NamingException {
        return "java:";
    }

    protected Context getBoundContext() throws NamingException {
        if (this.initialContext) {
            String ICName = IC_PREFIX;
            if (ContextBindings.isThreadBound()) {
                ICName = ICName + ContextBindings.getThreadName();
            } else if (ContextBindings.isClassLoaderBound()) {
                ICName = ICName + ContextBindings.getClassLoaderName();
            }
            Context initialContext = ContextBindings.getContext(ICName);
            if (initialContext == null) {
                initialContext = new NamingContext(this.env, ICName);
                ContextBindings.bindContext(ICName, initialContext);
            }
            return initialContext;
        }
        if (ContextBindings.isThreadBound()) {
            return ContextBindings.getThread();
        }
        return ContextBindings.getClassLoader();
    }

    protected String parseName(String name) throws NamingException {
        if (!this.initialContext && name.startsWith("java:")) {
            return name.substring(prefixLength);
        }
        if (this.initialContext) {
            return name;
        }
        throw new NamingException(sm.getString("selectorContext.noJavaUrl"));
    }

    protected Name parseName(Name name) throws NamingException {
        if (!this.initialContext && !name.isEmpty() && name.get(0).startsWith("java:")) {
            if (name.get(0).equals("java:")) {
                return name.getSuffix(1);
            }
            Name result = name.getSuffix(1);
            result.add(0, name.get(0).substring(prefixLength));
            return result;
        }
        if (this.initialContext) {
            return name;
        }
        throw new NamingException(sm.getString("selectorContext.noJavaUrl"));
    }
}
