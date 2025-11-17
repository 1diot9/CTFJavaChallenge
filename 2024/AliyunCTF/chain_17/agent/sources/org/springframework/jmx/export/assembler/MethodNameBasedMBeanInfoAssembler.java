package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/MethodNameBasedMBeanInfoAssembler.class */
public class MethodNameBasedMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler {

    @Nullable
    private Set<String> managedMethods;

    @Nullable
    private Map<String, Set<String>> methodMappings;

    public void setManagedMethods(String... methodNames) {
        this.managedMethods = Set.of((Object[]) methodNames);
    }

    public void setMethodMappings(Properties mappings) {
        this.methodMappings = new HashMap();
        Enumeration<?> en = mappings.keys();
        while (en.hasMoreElements()) {
            String beanKey = (String) en.nextElement();
            String[] methodNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
            this.methodMappings.put(beanKey, Set.of((Object[]) methodNames));
        }
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return isMatch(method, beanKey);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return isMatch(method, beanKey);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeOperation(Method method, String beanKey) {
        return isMatch(method, beanKey);
    }

    protected boolean isMatch(Method method, String beanKey) {
        Set<String> methodNames;
        if (this.methodMappings == null || (methodNames = this.methodMappings.get(beanKey)) == null) {
            return this.managedMethods != null && this.managedMethods.contains(method.getName());
        }
        return methodNames.contains(method.getName());
    }
}
