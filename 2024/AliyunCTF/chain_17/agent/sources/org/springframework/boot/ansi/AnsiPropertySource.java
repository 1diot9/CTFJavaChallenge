package org.springframework.boot.ansi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiPropertySource.class */
public class AnsiPropertySource extends PropertySource<AnsiElement> {
    private static final Iterable<Mapping> MAPPINGS;
    private final boolean encode;

    static {
        List<Mapping> mappings = new ArrayList<>();
        mappings.add(new EnumMapping("AnsiStyle.", AnsiStyle.class));
        mappings.add(new EnumMapping("AnsiColor.", AnsiColor.class));
        mappings.add(new Ansi8BitColorMapping("AnsiColor.", Ansi8BitColor::foreground));
        mappings.add(new EnumMapping("AnsiBackground.", AnsiBackground.class));
        mappings.add(new Ansi8BitColorMapping("AnsiBackground.", Ansi8BitColor::background));
        mappings.add(new EnumMapping("Ansi.", AnsiStyle.class));
        mappings.add(new EnumMapping("Ansi.", AnsiColor.class));
        mappings.add(new EnumMapping("Ansi.BG_", AnsiBackground.class));
        MAPPINGS = Collections.unmodifiableList(mappings);
    }

    public AnsiPropertySource(String name, boolean encode) {
        super(name);
        this.encode = encode;
    }

    @Override // org.springframework.core.env.PropertySource
    public Object getProperty(String name) {
        if (StringUtils.hasLength(name)) {
            for (Mapping mapping : MAPPINGS) {
                String prefix = mapping.getPrefix();
                if (name.startsWith(prefix)) {
                    String postfix = name.substring(prefix.length());
                    AnsiElement element = mapping.getElement(postfix);
                    if (element != null) {
                        return this.encode ? AnsiOutput.encode(element) : element;
                    }
                }
            }
            return null;
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiPropertySource$Mapping.class */
    private static abstract class Mapping {
        private final String prefix;

        abstract AnsiElement getElement(String postfix);

        Mapping(String prefix) {
            this.prefix = prefix;
        }

        String getPrefix() {
            return this.prefix;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiPropertySource$EnumMapping.class */
    private static class EnumMapping<E extends Enum<E> & AnsiElement> extends Mapping {
        private final Set<E> enums;

        EnumMapping(String prefix, Class<E> enumType) {
            super(prefix);
            this.enums = EnumSet.allOf(enumType);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.springframework.boot.ansi.AnsiPropertySource.Mapping
        AnsiElement getElement(String str) {
            for (E e : this.enums) {
                if (e.name().equals(str)) {
                    return (AnsiElement) e;
                }
            }
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiPropertySource$Ansi8BitColorMapping.class */
    private static class Ansi8BitColorMapping extends Mapping {
        private final IntFunction<Ansi8BitColor> factory;

        Ansi8BitColorMapping(String prefix, IntFunction<Ansi8BitColor> factory) {
            super(prefix);
            this.factory = factory;
        }

        @Override // org.springframework.boot.ansi.AnsiPropertySource.Mapping
        AnsiElement getElement(String postfix) {
            if (containsOnlyDigits(postfix)) {
                try {
                    return this.factory.apply(Integer.parseInt(postfix));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }

        private boolean containsOnlyDigits(String postfix) {
            for (int i = 0; i < postfix.length(); i++) {
                if (!Character.isDigit(postfix.charAt(i))) {
                    return false;
                }
            }
            return !postfix.isEmpty();
        }
    }
}
