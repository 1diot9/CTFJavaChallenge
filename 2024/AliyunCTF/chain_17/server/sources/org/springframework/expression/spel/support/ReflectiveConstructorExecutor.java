package org.springframework.expression.spel.support;

import java.lang.reflect.Constructor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/ReflectiveConstructorExecutor.class */
public class ReflectiveConstructorExecutor implements ConstructorExecutor {
    private final Constructor<?> ctor;

    @Nullable
    private final Integer varargsPosition;

    public ReflectiveConstructorExecutor(Constructor<?> ctor) {
        this.ctor = ctor;
        if (ctor.isVarArgs()) {
            this.varargsPosition = Integer.valueOf(ctor.getParameterCount() - 1);
        } else {
            this.varargsPosition = null;
        }
    }

    @Override // org.springframework.expression.ConstructorExecutor
    public TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException {
        try {
            ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.ctor, this.varargsPosition);
            if (this.ctor.isVarArgs()) {
                arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.ctor.getParameterTypes(), arguments);
            }
            ReflectionUtils.makeAccessible(this.ctor);
            return new TypedValue(this.ctor.newInstance(arguments));
        } catch (Exception ex) {
            throw new AccessException("Problem invoking constructor: " + this.ctor, ex);
        }
    }

    public Constructor<?> getConstructor() {
        return this.ctor;
    }
}
