package org.springframework.cglib.core;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/MethodWrapper.class */
public class MethodWrapper {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/MethodWrapper$MethodWrapperKey.class */
    public static final class MethodWrapperKey extends Record {
        private final String name;
        private final List<String> parameterTypes;
        private final String returnType;

        private MethodWrapperKey(String name, List<String> parameterTypes, String returnType) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, MethodWrapperKey.class), MethodWrapperKey.class, "name;parameterTypes;returnType", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->parameterTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->returnType:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MethodWrapperKey.class), MethodWrapperKey.class, "name;parameterTypes;returnType", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->parameterTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->returnType:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MethodWrapperKey.class, Object.class), MethodWrapperKey.class, "name;parameterTypes;returnType", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->name:Ljava/lang/String;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->parameterTypes:Ljava/util/List;", "FIELD:Lorg/springframework/cglib/core/MethodWrapper$MethodWrapperKey;->returnType:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String name() {
            return this.name;
        }

        public List<String> parameterTypes() {
            return this.parameterTypes;
        }

        public String returnType() {
            return this.returnType;
        }
    }

    private MethodWrapper() {
    }

    public static Object create(Method method) {
        return new MethodWrapperKey(method.getName(), Arrays.asList(ReflectUtils.getNames(method.getParameterTypes())), method.getReturnType().getName());
    }

    public static Set createSet(Collection methods) {
        Set set = new HashSet();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            set.add(create((Method) it.next()));
        }
        return set;
    }
}
