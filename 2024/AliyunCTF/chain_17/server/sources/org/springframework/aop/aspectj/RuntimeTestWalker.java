package org.springframework.aop.aspectj;

import java.lang.reflect.Field;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ast.And;
import org.aspectj.weaver.ast.Call;
import org.aspectj.weaver.ast.FieldGetCall;
import org.aspectj.weaver.ast.HasAnnotation;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Not;
import org.aspectj.weaver.ast.Or;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.internal.tools.MatchingContextBasedTest;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionVar;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.ShadowMatch;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker.class */
public class RuntimeTestWalker {
    private static final Field residualTestField;
    private static final Field varTypeField;
    private static final Field myClassField;

    @Nullable
    private final Test runtimeTest;

    static {
        try {
            residualTestField = ShadowMatchImpl.class.getDeclaredField("residualTest");
            varTypeField = ReflectionVar.class.getDeclaredField("varType");
            myClassField = ReflectionBasedReferenceTypeDelegate.class.getDeclaredField("myClass");
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("The version of aspectjtools.jar / aspectjweaver.jar on the classpath is incompatible with this version of Spring: " + ex);
        }
    }

    public RuntimeTestWalker(ShadowMatch shadowMatch) {
        try {
            ReflectionUtils.makeAccessible(residualTestField);
            this.runtimeTest = (Test) residualTestField.get(shadowMatch);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public boolean testsSubtypeSensitiveVars() {
        return this.runtimeTest != null && new SubtypeSensitiveVarTypeTestVisitor().testsSubtypeSensitiveVars(this.runtimeTest);
    }

    public boolean testThisInstanceOfResidue(Class<?> thisClass) {
        return this.runtimeTest != null && new ThisInstanceOfResidueTestVisitor(thisClass).thisInstanceOfMatches(this.runtimeTest);
    }

    public boolean testTargetInstanceOfResidue(Class<?> targetClass) {
        return this.runtimeTest != null && new TargetInstanceOfResidueTestVisitor(targetClass).targetInstanceOfMatches(this.runtimeTest);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker$TestVisitorAdapter.class */
    private static class TestVisitorAdapter implements ITestVisitor {
        protected static final int THIS_VAR = 0;
        protected static final int TARGET_VAR = 1;
        protected static final int AT_THIS_VAR = 3;
        protected static final int AT_TARGET_VAR = 4;
        protected static final int AT_ANNOTATION_VAR = 8;

        private TestVisitorAdapter() {
        }

        public void visit(And e) {
            e.getLeft().accept(this);
            e.getRight().accept(this);
        }

        public void visit(Or e) {
            e.getLeft().accept(this);
            e.getRight().accept(this);
        }

        public void visit(Not e) {
            e.getBody().accept(this);
        }

        public void visit(Instanceof i) {
        }

        public void visit(Literal literal) {
        }

        public void visit(Call call) {
        }

        public void visit(FieldGetCall fieldGetCall) {
        }

        public void visit(HasAnnotation hasAnnotation) {
        }

        public void visit(MatchingContextBasedTest matchingContextTest) {
        }

        protected int getVarType(ReflectionVar v) {
            try {
                ReflectionUtils.makeAccessible(RuntimeTestWalker.varTypeField);
                return ((Integer) RuntimeTestWalker.varTypeField.get(v)).intValue();
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker$InstanceOfResidueTestVisitor.class */
    public static abstract class InstanceOfResidueTestVisitor extends TestVisitorAdapter {
        private final Class<?> matchClass;
        private boolean matches;
        private final int matchVarType;

        public InstanceOfResidueTestVisitor(Class<?> matchClass, boolean defaultMatches, int matchVarType) {
            this.matchClass = matchClass;
            this.matches = defaultMatches;
            this.matchVarType = matchVarType;
        }

        public boolean instanceOfMatches(Test test) {
            test.accept(this);
            return this.matches;
        }

        @Override // org.springframework.aop.aspectj.RuntimeTestWalker.TestVisitorAdapter
        public void visit(Instanceof i) {
            int varType = getVarType((ReflectionVar) i.getVar());
            if (varType != this.matchVarType) {
                return;
            }
            Class<?> typeClass = null;
            ReferenceType referenceType = (ResolvedType) i.getType();
            if (referenceType instanceof ReferenceType) {
                ReferenceType referenceType2 = referenceType;
                ReferenceTypeDelegate delegate = referenceType2.getDelegate();
                if (delegate instanceof ReflectionBasedReferenceTypeDelegate) {
                    try {
                        ReflectionUtils.makeAccessible(RuntimeTestWalker.myClassField);
                        typeClass = (Class) RuntimeTestWalker.myClassField.get(delegate);
                    } catch (IllegalAccessException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
            if (typeClass == null) {
                try {
                    typeClass = ClassUtils.forName(referenceType.getName(), this.matchClass.getClassLoader());
                } catch (ClassNotFoundException e) {
                    this.matches = false;
                    return;
                }
            }
            this.matches = typeClass.isAssignableFrom(this.matchClass);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker$TargetInstanceOfResidueTestVisitor.class */
    private static class TargetInstanceOfResidueTestVisitor extends InstanceOfResidueTestVisitor {
        public TargetInstanceOfResidueTestVisitor(Class<?> targetClass) {
            super(targetClass, false, 1);
        }

        public boolean targetInstanceOfMatches(Test test) {
            return instanceOfMatches(test);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker$ThisInstanceOfResidueTestVisitor.class */
    private static class ThisInstanceOfResidueTestVisitor extends InstanceOfResidueTestVisitor {
        public ThisInstanceOfResidueTestVisitor(Class<?> thisClass) {
            super(thisClass, true, 0);
        }

        public boolean thisInstanceOfMatches(Test test) {
            return instanceOfMatches(test);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/RuntimeTestWalker$SubtypeSensitiveVarTypeTestVisitor.class */
    private static class SubtypeSensitiveVarTypeTestVisitor extends TestVisitorAdapter {
        private final Object thisObj = new Object();
        private final Object targetObj = new Object();
        private final Object[] argsObjs = new Object[0];
        private boolean testsSubtypeSensitiveVars = false;

        private SubtypeSensitiveVarTypeTestVisitor() {
        }

        public boolean testsSubtypeSensitiveVars(Test aTest) {
            aTest.accept(this);
            return this.testsSubtypeSensitiveVars;
        }

        @Override // org.springframework.aop.aspectj.RuntimeTestWalker.TestVisitorAdapter
        public void visit(Instanceof i) {
            ReflectionVar v = i.getVar();
            Object varUnderTest = v.getBindingAtJoinPoint(this.thisObj, this.targetObj, this.argsObjs);
            if (varUnderTest == this.thisObj || varUnderTest == this.targetObj) {
                this.testsSubtypeSensitiveVars = true;
            }
        }

        @Override // org.springframework.aop.aspectj.RuntimeTestWalker.TestVisitorAdapter
        public void visit(HasAnnotation hasAnn) {
            ReflectionVar v = (ReflectionVar) hasAnn.getVar();
            int varType = getVarType(v);
            if (varType == 3 || varType == 4 || varType == 8) {
                this.testsSubtypeSensitiveVars = true;
            }
        }
    }
}
