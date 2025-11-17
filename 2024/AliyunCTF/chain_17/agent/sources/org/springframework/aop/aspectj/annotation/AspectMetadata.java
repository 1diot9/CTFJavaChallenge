package org.springframework.aop.aspectj.annotation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.ComposablePointcut;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectMetadata.class */
public class AspectMetadata implements Serializable {
    private final String aspectName;
    private final Class<?> aspectClass;
    private transient AjType<?> ajType;
    private final Pointcut perClausePointcut;

    public AspectMetadata(Class<?> aspectClass, String aspectName) {
        this.aspectName = aspectName;
        Class<?> currClass = aspectClass;
        AjType<?> ajType = null;
        while (true) {
            if (currClass == Object.class) {
                break;
            }
            AjType<?> ajTypeToCheck = AjTypeSystem.getAjType(currClass);
            if (ajTypeToCheck.isAspect()) {
                ajType = ajTypeToCheck;
                break;
            }
            currClass = currClass.getSuperclass();
        }
        if (ajType == null) {
            throw new IllegalArgumentException("Class '" + aspectClass.getName() + "' is not an @AspectJ aspect");
        }
        if (ajType.getDeclarePrecedence().length > 0) {
            throw new IllegalArgumentException("DeclarePrecedence not presently supported in Spring AOP");
        }
        this.aspectClass = ajType.getJavaClass();
        this.ajType = ajType;
        switch (AnonymousClass1.$SwitchMap$org$aspectj$lang$reflect$PerClauseKind[this.ajType.getPerClause().getKind().ordinal()]) {
            case 1:
                this.perClausePointcut = Pointcut.TRUE;
                return;
            case 2:
            case 3:
                AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
                ajexp.setLocation(aspectClass.getName());
                ajexp.setExpression(findPerClause(aspectClass));
                ajexp.setPointcutDeclarationScope(aspectClass);
                this.perClausePointcut = ajexp;
                return;
            case 4:
                this.perClausePointcut = new ComposablePointcut(new TypePatternClassFilter(findPerClause(aspectClass)));
                return;
            default:
                throw new AopConfigException("PerClause " + ajType.getPerClause().getKind() + " not supported by Spring AOP for " + aspectClass);
        }
    }

    /* renamed from: org.springframework.aop.aspectj.annotation.AspectMetadata$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/AspectMetadata$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$PerClauseKind = new int[PerClauseKind.values().length];

        static {
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.SINGLETON.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTARGET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTHIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTYPEWITHIN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private String findPerClause(Class<?> aspectClass) {
        String str = aspectClass.getAnnotation(Aspect.class).value();
        int beginIndex = str.indexOf(40) + 1;
        int endIndex = str.length() - 1;
        return str.substring(beginIndex, endIndex);
    }

    public AjType<?> getAjType() {
        return this.ajType;
    }

    public Class<?> getAspectClass() {
        return this.aspectClass;
    }

    public String getAspectName() {
        return this.aspectName;
    }

    public Pointcut getPerClausePointcut() {
        return this.perClausePointcut;
    }

    public boolean isPerThisOrPerTarget() {
        PerClauseKind kind = getAjType().getPerClause().getKind();
        return kind == PerClauseKind.PERTARGET || kind == PerClauseKind.PERTHIS;
    }

    public boolean isPerTypeWithin() {
        PerClauseKind kind = getAjType().getPerClause().getKind();
        return kind == PerClauseKind.PERTYPEWITHIN;
    }

    public boolean isLazilyInstantiated() {
        return isPerThisOrPerTarget() || isPerTypeWithin();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        this.ajType = AjTypeSystem.getAjType(this.aspectClass);
    }
}
