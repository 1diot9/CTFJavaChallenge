package org.apache.tomcat.util.http.parser;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/EntityTag.class */
public class EntityTag {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:8:0x0032. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0063 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.Boolean compareEntityTag(java.io.StringReader r4, boolean r5, java.lang.String r6) throws java.io.IOException {
        /*
            r0 = r5
            if (r0 == 0) goto L16
            r0 = r6
            java.lang.String r1 = "W/"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L16
            r0 = r6
            r1 = 2
            java.lang.String r0 = r0.substring(r1)
            r7 = r0
            goto L18
        L16:
            r0 = r6
            r7 = r0
        L18:
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            r8 = r0
        L1d:
            r0 = 0
            r9 = r0
            r0 = r4
            int r0 = org.apache.tomcat.util.http.parser.HttpParser.skipLws(r0)
            int[] r0 = org.apache.tomcat.util.http.parser.EntityTag.AnonymousClass1.$SwitchMap$org$apache$tomcat$util$http$parser$SkipResult
            r1 = r4
            java.lang.String r2 = "W/"
            org.apache.tomcat.util.http.parser.SkipResult r1 = org.apache.tomcat.util.http.parser.HttpParser.skipConstant(r1, r2)
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto L4c;
                case 2: goto L4e;
                case 3: goto L54;
                default: goto L57;
            }
        L4c:
            r0 = 0
            return r0
        L4e:
            r0 = 1
            r9 = r0
            goto L57
        L54:
            r0 = 0
            r9 = r0
        L57:
            r0 = r4
            r1 = 1
            java.lang.String r0 = org.apache.tomcat.util.http.parser.HttpParser.readQuotedString(r0, r1)
            r10 = r0
            r0 = r10
            if (r0 != 0) goto L65
            r0 = 0
            return r0
        L65:
            r0 = r9
            if (r0 != 0) goto L6e
            r0 = r5
            if (r0 == 0) goto L7c
        L6e:
            r0 = r7
            r1 = r10
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L7c
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            r8 = r0
        L7c:
            r0 = r4
            int r0 = org.apache.tomcat.util.http.parser.HttpParser.skipLws(r0)
            int[] r0 = org.apache.tomcat.util.http.parser.EntityTag.AnonymousClass1.$SwitchMap$org$apache$tomcat$util$http$parser$SkipResult
            r1 = r4
            java.lang.String r2 = ","
            org.apache.tomcat.util.http.parser.SkipResult r1 = org.apache.tomcat.util.http.parser.HttpParser.skipConstant(r1, r2)
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                case 1: goto La8;
                case 2: goto Lab;
                case 3: goto Lad;
                default: goto Lad;
            }
        La8:
            r0 = r8
            return r0
        Lab:
            r0 = 0
            return r0
        Lad:
            goto L1d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.EntityTag.compareEntityTag(java.io.StringReader, boolean, java.lang.String):java.lang.Boolean");
    }
}
