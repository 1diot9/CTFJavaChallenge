package org.apache.catalina.util;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/StandardSessionIdGenerator.class */
public class StandardSessionIdGenerator extends SessionIdGeneratorBase {
    @Override // org.apache.catalina.SessionIdGenerator
    public String generateSessionId(String route) {
        byte[] random = new byte[16];
        int sessionIdLength = getSessionIdLength();
        StringBuilder buffer = new StringBuilder((2 * sessionIdLength) + 20);
        int resultLenBytes = 0;
        while (resultLenBytes < sessionIdLength) {
            getRandomBytes(random);
            for (int j = 0; j < random.length && resultLenBytes < sessionIdLength; j++) {
                byte b1 = (byte) ((random[j] & 240) >> 4);
                byte b2 = (byte) (random[j] & 15);
                if (b1 < 10) {
                    buffer.append((char) (48 + b1));
                } else {
                    buffer.append((char) (65 + (b1 - 10)));
                }
                if (b2 < 10) {
                    buffer.append((char) (48 + b2));
                } else {
                    buffer.append((char) (65 + (b2 - 10)));
                }
                resultLenBytes++;
            }
        }
        if (route != null && route.length() > 0) {
            buffer.append('.').append(route);
        } else {
            String jvmRoute = getJvmRoute();
            if (jvmRoute != null && jvmRoute.length() > 0) {
                buffer.append('.').append(jvmRoute);
            }
        }
        return buffer.toString();
    }
}
