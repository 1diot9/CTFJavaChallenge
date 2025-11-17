package org.apache.catalina.valves.rewrite;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/RandomizedTextRewriteMap.class */
public class RandomizedTextRewriteMap implements RewriteMap {
    protected static final StringManager sm = StringManager.getManager((Class<?>) RandomizedTextRewriteMap.class);
    private static final Random random = new Random();
    private final Map<String, String[]> map = new HashMap();

    /* JADX WARN: Code restructure failed: missing block: B:29:0x00bd, code lost:            throw new java.lang.IllegalArgumentException(org.apache.catalina.valves.rewrite.RandomizedTextRewriteMap.sm.getString("rewriteMap.txtInvalidLine", r0, r10));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public RandomizedTextRewriteMap(java.lang.String r10, boolean r11) {
        /*
            Method dump skipped, instructions count: 295
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.valves.rewrite.RandomizedTextRewriteMap.<init>(java.lang.String, boolean):void");
    }

    @Override // org.apache.catalina.valves.rewrite.RewriteMap
    public String setParameters(String params) {
        throw new IllegalArgumentException(StringManager.getManager((Class<?>) RewriteMap.class).getString("rewriteMap.tooManyParameters"));
    }

    @Override // org.apache.catalina.valves.rewrite.RewriteMap
    public String lookup(String key) {
        String[] possibleValues = this.map.get(key);
        if (possibleValues != null) {
            if (possibleValues.length > 1) {
                return possibleValues[random.nextInt(possibleValues.length)];
            }
            return possibleValues[0];
        }
        return null;
    }
}
