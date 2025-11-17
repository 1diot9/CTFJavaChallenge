package org.apache.catalina.valves.rewrite;

import ch.qos.logback.core.net.ssl.SSL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.catalina.util.URLEncoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution.class */
public class Substitution {
    protected SubstitutionElement[] elements = null;
    protected String sub = null;
    private boolean escapeBackReferences;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$SubstitutionElement.class */
    public static abstract class SubstitutionElement {
        public abstract String evaluate(Matcher matcher, Matcher matcher2, Resolver resolver);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$StaticElement.class */
    public static class StaticElement extends SubstitutionElement {
        public String value;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return this.value;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$RewriteRuleBackReferenceElement.class */
    public class RewriteRuleBackReferenceElement extends SubstitutionElement {
        public int n;

        public RewriteRuleBackReferenceElement() {
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = rule.group(this.n);
            if (result == null) {
                result = "";
            }
            if (Substitution.this.escapeBackReferences) {
                return URLEncoder.DEFAULT.encode(result, resolver.getUriCharset());
            }
            return result;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$RewriteCondBackReferenceElement.class */
    public static class RewriteCondBackReferenceElement extends SubstitutionElement {
        public int n;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return cond.group(this.n) == null ? "" : cond.group(this.n);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableElement.class */
    public static class ServerVariableElement extends SubstitutionElement {
        public String key;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolve(this.key);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableEnvElement.class */
    public static class ServerVariableEnvElement extends SubstitutionElement {
        public String key;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveEnv(this.key);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableSslElement.class */
    public static class ServerVariableSslElement extends SubstitutionElement {
        public String key;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveSsl(this.key);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableHttpElement.class */
    public static class ServerVariableHttpElement extends SubstitutionElement {
        public String key;

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveHttp(this.key);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/Substitution$MapElement.class */
    public class MapElement extends SubstitutionElement {
        public RewriteMap map = null;
        public SubstitutionElement[] defaultValue = null;
        public SubstitutionElement[] key = null;

        public MapElement() {
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = this.map.lookup(Substitution.this.evaluateSubstitution(this.key, rule, cond, resolver));
            if (result == null && this.defaultValue != null) {
                result = Substitution.this.evaluateSubstitution(this.defaultValue, rule, cond, resolver);
            }
            return result;
        }
    }

    public String getSub() {
        return this.sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEscapeBackReferences(boolean escapeBackReferences) {
        this.escapeBackReferences = escapeBackReferences;
    }

    public void parse(Map<String, RewriteMap> maps) {
        this.elements = parseSubstitution(this.sub, maps);
    }

    private SubstitutionElement[] parseSubstitution(String sub, Map<String, RewriteMap> maps) {
        String key;
        SubstitutionElement newElement;
        List<SubstitutionElement> elements = new ArrayList<>();
        int pos = 0;
        while (pos < sub.length()) {
            int percentPos = sub.indexOf(37, pos);
            int dollarPos = sub.indexOf(36, pos);
            int backslashPos = sub.indexOf(92, pos);
            if (percentPos == -1 && dollarPos == -1 && backslashPos == -1) {
                StaticElement newElement2 = new StaticElement();
                newElement2.value = sub.substring(pos);
                pos = sub.length();
                elements.add(newElement2);
            } else if (isFirstPos(backslashPos, dollarPos, percentPos)) {
                if (backslashPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                StaticElement newElement3 = new StaticElement();
                newElement3.value = sub.substring(pos, backslashPos) + sub.substring(backslashPos + 1, backslashPos + 2);
                pos = backslashPos + 2;
                elements.add(newElement3);
            } else if (isFirstPos(dollarPos, percentPos)) {
                if (dollarPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < dollarPos) {
                    StaticElement newElement4 = new StaticElement();
                    newElement4.value = sub.substring(pos, dollarPos);
                    elements.add(newElement4);
                }
                if (Character.isDigit(sub.charAt(dollarPos + 1))) {
                    RewriteRuleBackReferenceElement newElement5 = new RewriteRuleBackReferenceElement();
                    newElement5.n = Character.digit(sub.charAt(dollarPos + 1), 10);
                    pos = dollarPos + 2;
                    elements.add(newElement5);
                } else if (sub.charAt(dollarPos + 1) == '{') {
                    MapElement newElement6 = new MapElement();
                    int open = sub.indexOf(123, dollarPos);
                    int colon = findMatchingColonOrBar(true, sub, open);
                    int def = findMatchingColonOrBar(false, sub, open);
                    int close = findMatchingBrace(sub, open);
                    if (-1 >= open || open >= colon || colon >= close) {
                        throw new IllegalArgumentException(sub);
                    }
                    newElement6.map = maps.get(sub.substring(open + 1, colon));
                    if (newElement6.map == null) {
                        throw new IllegalArgumentException(sub + ": No map: " + sub.substring(open + 1, colon));
                    }
                    String defaultValue = null;
                    if (def > -1) {
                        if (colon >= def || def >= close) {
                            throw new IllegalArgumentException(sub);
                        }
                        key = sub.substring(colon + 1, def);
                        defaultValue = sub.substring(def + 1, close);
                    } else {
                        key = sub.substring(colon + 1, close);
                    }
                    newElement6.key = parseSubstitution(key, maps);
                    if (defaultValue != null) {
                        newElement6.defaultValue = parseSubstitution(defaultValue, maps);
                    }
                    pos = close + 1;
                    elements.add(newElement6);
                } else {
                    throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                }
            } else {
                if (percentPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < percentPos) {
                    StaticElement newElement7 = new StaticElement();
                    newElement7.value = sub.substring(pos, percentPos);
                    elements.add(newElement7);
                }
                if (Character.isDigit(sub.charAt(percentPos + 1))) {
                    RewriteCondBackReferenceElement newElement8 = new RewriteCondBackReferenceElement();
                    newElement8.n = Character.digit(sub.charAt(percentPos + 1), 10);
                    pos = percentPos + 2;
                    elements.add(newElement8);
                } else if (sub.charAt(percentPos + 1) == '{') {
                    int open2 = sub.indexOf(123, percentPos);
                    int colon2 = findMatchingColonOrBar(true, sub, open2);
                    int close2 = findMatchingBrace(sub, open2);
                    if (-1 >= open2 || open2 >= close2) {
                        throw new IllegalArgumentException(sub);
                    }
                    if (colon2 > -1 && open2 < colon2 && colon2 < close2) {
                        String type = sub.substring(open2 + 1, colon2);
                        if (type.equals("ENV")) {
                            newElement = new ServerVariableEnvElement();
                            ((ServerVariableEnvElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else if (type.equals(SSL.DEFAULT_PROTOCOL)) {
                            newElement = new ServerVariableSslElement();
                            ((ServerVariableSslElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else if (type.equals("HTTP")) {
                            newElement = new ServerVariableHttpElement();
                            ((ServerVariableHttpElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else {
                            throw new IllegalArgumentException(sub + ": Bad type: " + type);
                        }
                    } else {
                        newElement = new ServerVariableElement();
                        ((ServerVariableElement) newElement).key = sub.substring(open2 + 1, close2);
                    }
                    pos = close2 + 1;
                    elements.add(newElement);
                } else {
                    throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                }
            }
        }
        return (SubstitutionElement[]) elements.toArray(new SubstitutionElement[0]);
    }

    private static int findMatchingBrace(String sub, int start) {
        int nesting = 1;
        for (int i = start + 1; i < sub.length(); i++) {
            char c = sub.charAt(i);
            if (c == '{') {
                char previousChar = sub.charAt(i - 1);
                if (previousChar == '$' || previousChar == '%') {
                    nesting++;
                }
            } else if (c == '}') {
                nesting--;
                if (nesting == 0) {
                    return i;
                }
            } else {
                continue;
            }
        }
        return -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0063 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0066 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int findMatchingColonOrBar(boolean r4, java.lang.String r5, int r6) {
        /*
            r0 = 0
            r7 = r0
            r0 = r6
            r1 = 1
            int r0 = r0 + r1
            r8 = r0
        L7:
            r0 = r8
            r1 = r5
            int r1 = r1.length()
            if (r0 >= r1) goto L6c
            r0 = r5
            r1 = r8
            char r0 = r0.charAt(r1)
            r9 = r0
            r0 = r9
            r1 = 123(0x7b, float:1.72E-43)
            if (r0 != r1) goto L3d
            r0 = r5
            r1 = r8
            r2 = 1
            int r1 = r1 - r2
            char r0 = r0.charAt(r1)
            r10 = r0
            r0 = r10
            r1 = 36
            if (r0 == r1) goto L37
            r0 = r10
            r1 = 37
            if (r0 != r1) goto L3a
        L37:
            int r7 = r7 + 1
        L3a:
            goto L66
        L3d:
            r0 = r9
            r1 = 125(0x7d, float:1.75E-43)
            if (r0 != r1) goto L4a
            int r7 = r7 + (-1)
            goto L66
        L4a:
            r0 = r4
            if (r0 == 0) goto L58
            r0 = r9
            r1 = 58
            if (r0 != r1) goto L66
            goto L5f
        L58:
            r0 = r9
            r1 = 124(0x7c, float:1.74E-43)
            if (r0 != r1) goto L66
        L5f:
            r0 = r7
            if (r0 != 0) goto L66
            r0 = r8
            return r0
        L66:
            int r8 = r8 + 1
            goto L7
        L6c:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.valves.rewrite.Substitution.findMatchingColonOrBar(boolean, java.lang.String, int):int");
    }

    public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
        return evaluateSubstitution(this.elements, rule, cond, resolver);
    }

    private String evaluateSubstitution(SubstitutionElement[] elements, Matcher rule, Matcher cond, Resolver resolver) {
        StringBuilder buf = new StringBuilder();
        for (SubstitutionElement element : elements) {
            buf.append(element.evaluate(rule, cond, resolver));
        }
        return buf.toString();
    }

    private boolean isFirstPos(int testPos, int... others) {
        if (testPos < 0) {
            return false;
        }
        for (int other : others) {
            if (other >= 0 && other < testPos) {
                return false;
            }
        }
        return true;
    }
}
