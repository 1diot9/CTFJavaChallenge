package org.springframework.aot.hint;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/MemberHint.class */
public abstract class MemberHint {
    private final String name;

    /* JADX INFO: Access modifiers changed from: protected */
    public MemberHint(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
