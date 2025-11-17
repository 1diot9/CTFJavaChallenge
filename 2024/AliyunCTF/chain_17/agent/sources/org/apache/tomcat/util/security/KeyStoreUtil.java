package org.apache.tomcat.util.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/security/KeyStoreUtil.class */
public class KeyStoreUtil {
    private KeyStoreUtil() {
    }

    public static void load(KeyStore keystore, InputStream is, char[] storePass) throws NoSuchAlgorithmException, CertificateException, IOException {
        if (keystore.getType().equals("PKCS12")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (true) {
                int numRead = is.read(buf);
                if (numRead >= 0) {
                    baos.write(buf, 0, numRead);
                } else {
                    baos.close();
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    keystore.load(bais, storePass);
                    return;
                }
            }
        } else {
            keystore.load(is, storePass);
        }
    }
}
