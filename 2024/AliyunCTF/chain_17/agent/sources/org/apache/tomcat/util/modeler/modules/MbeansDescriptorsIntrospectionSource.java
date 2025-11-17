package org.apache.tomcat.util.modeler.modules;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.AttributeInfo;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.OperationInfo;
import org.apache.tomcat.util.modeler.ParameterInfo;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/modules/MbeansDescriptorsIntrospectionSource.class */
public class MbeansDescriptorsIntrospectionSource extends ModelerSource {
    private Registry registry;
    private String type;
    private final List<ObjectName> mbeans = new ArrayList();
    private static final Class<?>[] supportedTypes;
    private static final Log log = LogFactory.getLog((Class<?>) MbeansDescriptorsIntrospectionSource.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) MbeansDescriptorsIntrospectionSource.class);
    private static final Map<String, String> specialMethods = new HashMap();

    static {
        specialMethods.put("preDeregister", "");
        specialMethods.put("postDeregister", "");
        supportedTypes = new Class[]{Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE, Character.class, Character.TYPE, Short.class, Short.TYPE, Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE, String.class, String[].class, BigDecimal.class, BigInteger.class, ObjectName.class, Object[].class, File.class};
    }

    public void setRegistry(Registry reg) {
        this.registry = reg;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override // org.apache.tomcat.util.modeler.modules.ModelerSource
    public List<ObjectName> loadDescriptors(Registry registry, String type, Object source) throws Exception {
        setRegistry(registry);
        setType(type);
        setSource(source);
        execute();
        return this.mbeans;
    }

    public void execute() throws Exception {
        if (this.registry == null) {
            this.registry = Registry.getRegistry(null, null);
        }
        try {
            ManagedBean managed = createManagedBean(this.registry, null, (Class) this.source, this.type);
            if (managed == null) {
                return;
            }
            managed.setName(this.type);
            this.registry.addManagedBean(managed);
        } catch (Exception ex) {
            log.error(sm.getString("modules.readDescriptorsError"), ex);
        }
    }

    private boolean supportedType(Class<?> ret) {
        for (Class<?> supportedType : supportedTypes) {
            if (ret == supportedType) {
                return true;
            }
        }
        if (isBeanCompatible(ret)) {
            return true;
        }
        return false;
    }

    private boolean isBeanCompatible(Class<?> javaType) {
        if (javaType.isArray() || javaType.isPrimitive() || javaType.getName().startsWith("java.") || javaType.getName().startsWith("javax.")) {
            return false;
        }
        try {
            javaType.getConstructor(new Class[0]);
            Class<?> superClass = javaType.getSuperclass();
            if (superClass != null && superClass != Object.class && superClass != Exception.class && superClass != Throwable.class && !isBeanCompatible(superClass)) {
                return false;
            }
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void initMethods(Class<?> realClass, Set<String> attNames, Map<String, Method> getAttMap, Map<String, Method> setAttMap, List<Method> invokeAttList) {
        Method[] methods = realClass.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (!Modifier.isStatic(method.getModifiers())) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    if (log.isDebugEnabled()) {
                        log.debug("Not public " + method);
                    }
                } else if (method.getDeclaringClass() != Object.class) {
                    Class<?>[] params = method.getParameterTypes();
                    if (name.startsWith(BeanUtil.PREFIX_GETTER_GET) && params.length == 0) {
                        if (!supportedType(method.getReturnType())) {
                            if (log.isDebugEnabled()) {
                                log.debug("Unsupported type " + method);
                            }
                        } else {
                            String name2 = unCapitalize(name.substring(3));
                            getAttMap.put(name2, method);
                            attNames.add(name2);
                        }
                    } else if (name.startsWith(BeanUtil.PREFIX_GETTER_IS) && params.length == 0) {
                        Class<?> ret = method.getReturnType();
                        if (Boolean.TYPE != ret) {
                            if (log.isDebugEnabled()) {
                                log.debug("Unsupported type " + method + " " + ret);
                            }
                        } else {
                            String name3 = unCapitalize(name.substring(2));
                            getAttMap.put(name3, method);
                            attNames.add(name3);
                        }
                    } else if (name.startsWith("set") && params.length == 1) {
                        if (!supportedType(params[0])) {
                            if (log.isDebugEnabled()) {
                                log.debug("Unsupported type " + method + " " + params[0]);
                            }
                        } else {
                            String name4 = unCapitalize(name.substring(3));
                            setAttMap.put(name4, method);
                            attNames.add(name4);
                        }
                    } else if (params.length == 0) {
                        if (specialMethods.get(name) == null) {
                            invokeAttList.add(method);
                        }
                    } else {
                        boolean supported = true;
                        int length = params.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            Class<?> param = params[i];
                            if (supportedType(param)) {
                                i++;
                            } else {
                                supported = false;
                                break;
                            }
                        }
                        if (supported) {
                            invokeAttList.add(method);
                        }
                    }
                }
            }
        }
    }

    public ManagedBean createManagedBean(Registry registry, String domain, Class<?> realClass, String type) {
        ManagedBean mbean = new ManagedBean();
        Set<String> attrNames = new HashSet<>();
        Map<String, Method> getAttMap = new HashMap<>();
        Map<String, Method> setAttMap = new HashMap<>();
        List<Method> invokeAttList = new ArrayList<>();
        initMethods(realClass, attrNames, getAttMap, setAttMap, invokeAttList);
        try {
            for (String name : attrNames) {
                AttributeInfo ai = new AttributeInfo();
                ai.setName(name);
                Method gm = getAttMap.get(name);
                if (gm != null) {
                    ai.setGetMethod(gm.getName());
                    Class<?> t = gm.getReturnType();
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                }
                Method sm2 = setAttMap.get(name);
                if (sm2 != null) {
                    Class<?> t2 = sm2.getParameterTypes()[0];
                    if (t2 != null) {
                        ai.setType(t2.getName());
                    }
                    ai.setSetMethod(sm2.getName());
                }
                ai.setDescription("Introspected attribute " + name);
                if (log.isDebugEnabled()) {
                    log.debug("Introspected attribute " + name + " " + gm + " " + sm2);
                }
                if (gm == null) {
                    ai.setReadable(false);
                }
                if (sm2 == null) {
                    ai.setWriteable(false);
                }
                if (sm2 != null || gm != null) {
                    mbean.addAttribute(ai);
                }
            }
            for (Method method : invokeAttList) {
                String name2 = method.getName();
                OperationInfo op = new OperationInfo();
                op.setName(name2);
                op.setReturnType(method.getReturnType().getName());
                op.setDescription("Introspected operation " + name2);
                Class<?>[] params = method.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    ParameterInfo pi = new ParameterInfo();
                    pi.setType(params[i].getName());
                    pi.setName(("param" + i).intern());
                    pi.setDescription(("Introspected parameter param" + i).intern());
                    op.addParameter(pi);
                }
                mbean.addOperation(op);
            }
            if (log.isDebugEnabled()) {
                log.debug("Setting name: " + type);
            }
            mbean.setName(type);
            return mbean;
        } catch (Exception ex) {
            log.error(sm.getString("source.introspectionError", realClass.getName()), ex);
            return null;
        }
    }

    private static String unCapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
