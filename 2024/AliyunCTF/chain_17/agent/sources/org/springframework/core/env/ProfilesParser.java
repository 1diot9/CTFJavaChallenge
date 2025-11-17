package org.springframework.core.env;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ProfilesParser.class */
public final class ProfilesParser {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ProfilesParser$Context.class */
    public enum Context {
        NONE,
        NEGATE,
        PARENTHESIS
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ProfilesParser$Operator.class */
    public enum Operator {
        AND,
        OR
    }

    private ProfilesParser() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Profiles parse(String... expressions) {
        Assert.notEmpty(expressions, "Must specify at least one profile expression");
        Profiles[] parsed = new Profiles[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            parsed[i] = parseExpression(expressions[i]);
        }
        return new ParsedProfiles(expressions, parsed);
    }

    private static Profiles parseExpression(String expression) {
        Assert.hasText(expression, (Supplier<String>) () -> {
            return "Invalid profile expression [" + expression + "]: must contain text";
        });
        StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
        return parseTokens(expression, tokens);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens) {
        return parseTokens(expression, tokens, Context.NONE);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00b3, code lost:            switch(r12) {            case 0: goto L68;            case 1: goto L63;            case 2: goto L64;            case 3: goto L65;            case 4: goto L66;            default: goto L67;        };     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00f7, code lost:            if (r9 == null) goto L36;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00ff, code lost:            if (r9 != org.springframework.core.env.ProfilesParser.Operator.AND) goto L37;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0106, code lost:            r1 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0107, code lost:            assertWellFormed(r5, r1);        r9 = org.springframework.core.env.ProfilesParser.Operator.AND;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0102, code lost:            r1 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0115, code lost:            if (r9 == null) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x011d, code lost:            if (r9 != org.springframework.core.env.ProfilesParser.Operator.OR) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0124, code lost:            r1 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0125, code lost:            assertWellFormed(r5, r1);        r9 = org.springframework.core.env.ProfilesParser.Operator.OR;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0120, code lost:            r1 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0130, code lost:            r0.add(not(parseTokens(r5, r6, org.springframework.core.env.ProfilesParser.Context.NEGATE)));     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0145, code lost:            r0 = merge(r5, r0, r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0152, code lost:            if (r7 != org.springframework.core.env.ProfilesParser.Context.PARENTHESIS) goto L51;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0158, code lost:            r0.clear();        r0.add(r0);        r9 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0157, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x016d, code lost:            r0 = equals(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0178, code lost:            if (r7 != org.springframework.core.env.ProfilesParser.Context.NEGATE) goto L56;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x017e, code lost:            r0.add(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x017d, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00d4, code lost:            r0 = parseTokens(r5, r6, org.springframework.core.env.ProfilesParser.Context.PARENTHESIS);     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e2, code lost:            if (r7 != org.springframework.core.env.ProfilesParser.Context.NEGATE) goto L31;     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x00e8, code lost:            r0.add(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x00e7, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.springframework.core.env.Profiles parseTokens(java.lang.String r5, java.util.StringTokenizer r6, org.springframework.core.env.ProfilesParser.Context r7) {
        /*
            Method dump skipped, instructions count: 402
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.env.ProfilesParser.parseTokens(java.lang.String, java.util.StringTokenizer, org.springframework.core.env.ProfilesParser$Context):org.springframework.core.env.Profiles");
    }

    private static Profiles merge(String expression, List<Profiles> elements, @Nullable Operator operator) {
        assertWellFormed(expression, !elements.isEmpty());
        if (elements.size() == 1) {
            return elements.get(0);
        }
        Profiles[] profiles = (Profiles[]) elements.toArray(new Profiles[0]);
        return operator == Operator.AND ? and(profiles) : or(profiles);
    }

    private static void assertWellFormed(String expression, boolean wellFormed) {
        Assert.isTrue(wellFormed, (Supplier<String>) () -> {
            return "Malformed profile expression [" + expression + "]";
        });
    }

    private static Profiles or(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).anyMatch(isMatch(activeProfile));
        };
    }

    private static Profiles and(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).allMatch(isMatch(activeProfile));
        };
    }

    private static Profiles not(Profiles profiles) {
        return activeProfile -> {
            return !profiles.matches(activeProfile);
        };
    }

    private static Profiles equals(String profile) {
        return activeProfile -> {
            return activeProfile.test(profile);
        };
    }

    private static Predicate<Profiles> isMatch(Predicate<String> activeProfiles) {
        return profiles -> {
            return profiles.matches(activeProfiles);
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ProfilesParser$ParsedProfiles.class */
    public static class ParsedProfiles implements Profiles {
        private final Set<String> expressions = new LinkedHashSet();
        private final Profiles[] parsed;

        ParsedProfiles(String[] expressions, Profiles[] parsed) {
            Collections.addAll(this.expressions, expressions);
            this.parsed = parsed;
        }

        @Override // org.springframework.core.env.Profiles
        public boolean matches(Predicate<String> activeProfiles) {
            for (Profiles candidate : this.parsed) {
                if (candidate.matches(activeProfiles)) {
                    return true;
                }
            }
            return false;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ParsedProfiles) {
                    ParsedProfiles that = (ParsedProfiles) other;
                    if (this.expressions.equals(that.expressions)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.expressions.hashCode();
        }

        public String toString() {
            if (this.expressions.size() == 1) {
                return this.expressions.iterator().next();
            }
            return (String) this.expressions.stream().map(this::wrap).collect(Collectors.joining(" | "));
        }

        private String wrap(String str) {
            return "(" + str + ")";
        }
    }
}
