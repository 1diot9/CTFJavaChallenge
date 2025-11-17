package org.springframework.boot.ssl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.core.style.ToStringCreator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslOptions.class */
public interface SslOptions {
    public static final SslOptions NONE = of((Set<String>) null, (Set<String>) null);

    String[] getCiphers();

    String[] getEnabledProtocols();

    default boolean isSpecified() {
        return (getCiphers() == null || getEnabledProtocols() == null) ? false : true;
    }

    static SslOptions of(final String[] ciphers, final String[] enabledProtocols) {
        return new SslOptions() { // from class: org.springframework.boot.ssl.SslOptions.1
            @Override // org.springframework.boot.ssl.SslOptions
            public String[] getCiphers() {
                return ciphers;
            }

            @Override // org.springframework.boot.ssl.SslOptions
            public String[] getEnabledProtocols() {
                return enabledProtocols;
            }

            public String toString() {
                ToStringCreator creator = new ToStringCreator(this);
                creator.append("ciphers", ciphers);
                creator.append("enabledProtocols", enabledProtocols);
                return creator.toString();
            }
        };
    }

    static SslOptions of(Set<String> ciphers, Set<String> enabledProtocols) {
        return of(toArray(ciphers), toArray(enabledProtocols));
    }

    static Set<String> asSet(String[] array) {
        if (array != null) {
            return Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(array)));
        }
        return null;
    }

    private static String[] toArray(Collection<String> collection) {
        if (collection != null) {
            return (String[]) collection.toArray(x$0 -> {
                return new String[x$0];
            });
        }
        return null;
    }
}
