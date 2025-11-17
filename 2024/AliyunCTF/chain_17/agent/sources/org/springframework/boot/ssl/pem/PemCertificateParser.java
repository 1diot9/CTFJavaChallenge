package org.springframework.boot.ssl.pem;

import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.KeyUtil;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/pem/PemCertificateParser.class */
final class PemCertificateParser {
    private static final String HEADER = "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+";
    private static final String BASE64_TEXT = "([a-z0-9+/=\\r\\n]+)";
    private static final String FOOTER = "-+END\\s+.*CERTIFICATE[^-]*-+";
    private static final Pattern PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);

    private PemCertificateParser() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<X509Certificate> parse(String text) {
        if (text == null) {
            return null;
        }
        CertificateFactory factory = getCertificateFactory();
        List<X509Certificate> certs = new ArrayList<>();
        Objects.requireNonNull(certs);
        readCertificates(text, factory, (v1) -> {
            r2.add(v1);
        });
        Assert.state(!CollectionUtils.isEmpty(certs), "Missing certificates or unrecognized format");
        return List.copyOf(certs);
    }

    private static CertificateFactory getCertificateFactory() {
        try {
            return CertificateFactory.getInstance(KeyUtil.CERT_TYPE_X509);
        } catch (CertificateException ex) {
            throw new IllegalStateException("Unable to get X.509 certificate factory", ex);
        }
    }

    private static void readCertificates(String text, CertificateFactory factory, Consumer<X509Certificate> consumer) {
        try {
            Matcher matcher = PATTERN.matcher(text);
            while (matcher.find()) {
                String encodedText = matcher.group(1);
                byte[] decodedBytes = decodeBase64(encodedText);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                while (inputStream.available() > 0) {
                    consumer.accept((X509Certificate) factory.generateCertificate(inputStream));
                }
            }
        } catch (CertificateException ex) {
            throw new IllegalStateException("Error reading certificate: " + ex.getMessage(), ex);
        }
    }

    private static byte[] decodeBase64(String content) {
        byte[] bytes = content.replaceAll(StrPool.CR, "").replaceAll(StrPool.LF, "").getBytes();
        return Base64.getDecoder().decode(bytes);
    }
}
