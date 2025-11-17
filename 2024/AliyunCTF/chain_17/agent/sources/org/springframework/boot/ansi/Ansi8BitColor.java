package org.springframework.boot.ansi;

import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/Ansi8BitColor.class */
public final class Ansi8BitColor implements AnsiElement {
    private final String prefix;
    private final int code;

    private Ansi8BitColor(String prefix, int code) {
        Assert.isTrue(code >= 0 && code <= 255, "Code must be between 0 and 255");
        this.prefix = prefix;
        this.code = code;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Ansi8BitColor other = (Ansi8BitColor) obj;
        return this.prefix.equals(other.prefix) && this.code == other.code;
    }

    public int hashCode() {
        return (this.prefix.hashCode() * 31) + this.code;
    }

    @Override // org.springframework.boot.ansi.AnsiElement
    public String toString() {
        return this.prefix + this.code;
    }

    public static Ansi8BitColor foreground(int code) {
        return new Ansi8BitColor("38;5;", code);
    }

    public static Ansi8BitColor background(int code) {
        return new Ansi8BitColor("48;5;", code);
    }
}
