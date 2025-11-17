package org.springframework.cglib.reflect;

import java.lang.reflect.Member;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/reflect/FastMember.class */
public abstract class FastMember {
    protected FastClass fc;
    protected Member member;
    protected int index;

    public abstract Class[] getParameterTypes();

    public abstract Class[] getExceptionTypes();

    /* JADX INFO: Access modifiers changed from: protected */
    public FastMember(FastClass fc, Member member, int index) {
        this.fc = fc;
        this.member = member;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.member.getName();
    }

    public Class getDeclaringClass() {
        return this.fc.getJavaClass();
    }

    public int getModifiers() {
        return this.member.getModifiers();
    }

    public String toString() {
        return this.member.toString();
    }

    public int hashCode() {
        return this.member.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof FastMember)) {
            return false;
        }
        FastMember other = (FastMember) o;
        return this.member.equals(other.member);
    }
}
