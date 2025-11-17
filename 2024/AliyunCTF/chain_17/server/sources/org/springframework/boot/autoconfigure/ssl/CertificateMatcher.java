package org.springframework.boot.autoconfigure.ssl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Objects;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/CertificateMatcher.class */
class CertificateMatcher {
    private static final byte[] DATA = new byte[256];
    private final PrivateKey privateKey;
    private final Signature signature;
    private final byte[] generatedSignature;

    static {
        for (int i = 0; i < DATA.length; i++) {
            DATA[i] = (byte) i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CertificateMatcher(PrivateKey privateKey) {
        Assert.notNull(privateKey, "Private key must not be null");
        this.privateKey = privateKey;
        this.signature = createSignature(privateKey);
        Assert.notNull(this.signature, "Failed to create signature");
        this.generatedSignature = sign(this.signature, privateKey);
    }

    private Signature createSignature(PrivateKey privateKey) {
        try {
            String algorithm = getSignatureAlgorithm(privateKey);
            if (algorithm != null) {
                return Signature.getInstance(algorithm);
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String getSignatureAlgorithm(PrivateKey privateKey) {
        String algorithm = privateKey.getAlgorithm();
        boolean z = -1;
        switch (algorithm.hashCode()) {
            case 2206:
                if (algorithm.equals("EC")) {
                    z = 2;
                    break;
                }
                break;
            case 67986:
                if (algorithm.equals("DSA")) {
                    z = true;
                    break;
                }
                break;
            case 81440:
                if (algorithm.equals("RSA")) {
                    z = false;
                    break;
                }
                break;
            case 66770035:
                if (algorithm.equals("EdDSA")) {
                    z = 3;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return "SHA256withRSA";
            case true:
                return "SHA256withDSA";
            case true:
                return "SHA256withECDSA";
            case true:
                return "EdDSA";
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchesAny(List<? extends Certificate> certificates) {
        return this.generatedSignature != null && certificates.stream().anyMatch(this::matches);
    }

    boolean matches(Certificate certificate) {
        return matches(certificate.getPublicKey());
    }

    private boolean matches(PublicKey publicKey) {
        return this.generatedSignature != null && Objects.equals(this.privateKey.getAlgorithm(), publicKey.getAlgorithm()) && verify(publicKey);
    }

    private boolean verify(PublicKey publicKey) {
        try {
            this.signature.initVerify(publicKey);
            this.signature.update(DATA);
            return this.signature.verify(this.generatedSignature);
        } catch (InvalidKeyException | SignatureException e) {
            return false;
        }
    }

    private static byte[] sign(Signature signature, PrivateKey privateKey) {
        try {
            signature.initSign(privateKey);
            signature.update(DATA);
            return signature.sign();
        } catch (InvalidKeyException | SignatureException e) {
            return null;
        }
    }
}
