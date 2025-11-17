package org.apache.tomcat.util.net.jsse;

import cn.hutool.crypto.KeyUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.buf.Asn1Parser;
import org.apache.tomcat.util.buf.Asn1Writer;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/PEMFile.class */
public class PEMFile {
    private static final String DEFAULT_PRF = "HmacSHA1";
    private static final Map<String, Algorithm> OID_TO_ALGORITHM;
    private List<X509Certificate> certificates;
    private PrivateKey privateKey;
    private static final StringManager sm = StringManager.getManager((Class<?>) PEMFile.class);
    private static final byte[] OID_EC_PUBLIC_KEY = {6, 7, 42, -122, 72, -50, 61, 2, 1};
    private static final byte[] OID_PBES2 = {42, -122, 72, -122, -9, 13, 1, 5, 13};
    private static final byte[] OID_PBKDF2 = {42, -122, 72, -122, -9, 13, 1, 5, 12};
    private static final Map<String, String> OID_TO_PRF = new HashMap();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/PEMFile$Format.class */
    public enum Format {
        PKCS1,
        PKCS8,
        RFC5915
    }

    static {
        OID_TO_PRF.put("2a864886f70d0207", DEFAULT_PRF);
        OID_TO_PRF.put("2a864886f70d0208", "HmacSHA224");
        OID_TO_PRF.put("2a864886f70d0209", "HmacSHA256");
        OID_TO_PRF.put("2a864886f70d020a", "HmacSHA384");
        OID_TO_PRF.put("2a864886f70d020b", "HmacSHA512");
        OID_TO_PRF.put("2a864886f70d020c", "HmacSHA512/224");
        OID_TO_PRF.put("2a864886f70d020d", "HmacSHA512/256");
        OID_TO_ALGORITHM = new HashMap();
        OID_TO_ALGORITHM.put("2a864886f70d0307", Algorithm.DES_EDE3_CBC);
        OID_TO_ALGORITHM.put("608648016503040102", Algorithm.AES128_CBC_PAD);
        OID_TO_ALGORITHM.put("60864801650304012a", Algorithm.AES256_CBC_PAD);
    }

    public static String toPEM(X509Certificate certificate) throws CertificateEncodingException {
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN CERTIFICATE-----");
        result.append(System.lineSeparator());
        Base64 b64 = new Base64(64);
        result.append(b64.encodeAsString(certificate.getEncoded()));
        result.append("-----END CERTIFICATE-----");
        return result.toString();
    }

    public List<X509Certificate> getCertificates() {
        return this.certificates;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PEMFile(String filename) throws IOException, GeneralSecurityException {
        this(filename, null);
    }

    public PEMFile(String filename, String password) throws IOException, GeneralSecurityException {
        this(filename, password, null);
    }

    public PEMFile(String filename, String password, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, ConfigFileLoader.getSource().getResource(filename).getInputStream(), password, keyAlgorithm);
    }

    public PEMFile(String filename, String password, String passwordFilename, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, ConfigFileLoader.getSource().getResource(filename).getInputStream(), password, passwordFilename, passwordFilename != null ? ConfigFileLoader.getSource().getResource(passwordFilename).getInputStream() : null, keyAlgorithm);
    }

    public PEMFile(String filename, InputStream fileStream, String password, String keyAlgorithm) throws IOException, GeneralSecurityException {
        this(filename, fileStream, password, null, null, keyAlgorithm);
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x02be A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0175 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0258 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x026a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x027c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0290 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public PEMFile(java.lang.String r8, java.io.InputStream r9, java.lang.String r10, java.lang.String r11, java.io.InputStream r12, java.lang.String r13) throws java.io.IOException, java.security.GeneralSecurityException {
        /*
            Method dump skipped, instructions count: 721
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.jsse.PEMFile.<init>(java.lang.String, java.io.InputStream, java.lang.String, java.lang.String, java.io.InputStream, java.lang.String):void");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/PEMFile$Part.class */
    private static class Part {
        public static final String BEGIN_BOUNDARY = "-----BEGIN ";
        public static final String END_BOUNDARY = "-----END ";
        public static final String FINISH_BOUNDARY = "-----";
        public static final String PRIVATE_KEY = "PRIVATE KEY";
        public static final String EC_PRIVATE_KEY = "EC PRIVATE KEY";
        public static final String ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
        public static final String RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
        public static final String CERTIFICATE = "CERTIFICATE";
        public static final String X509_CERTIFICATE = "X509 CERTIFICATE";
        public String type;
        public String content = "";
        public String algorithm = null;
        public String ivHex = null;

        private Part() {
        }

        private byte[] decode() {
            return Base64.decodeBase64(this.content);
        }

        public X509Certificate toCertificate() throws CertificateException {
            CertificateFactory factory = CertificateFactory.getInstance(KeyUtil.CERT_TYPE_X509);
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(decode()));
        }

        public PrivateKey toPrivateKey(String keyAlgorithm, Format format, String filename) throws GeneralSecurityException {
            return toPrivateKey(keyAlgorithm, format, filename, decode());
        }

        public PrivateKey toPrivateKey(String password, String keyAlgorithm, Format format, String filename) throws GeneralSecurityException, IOException {
            String prf;
            String secretKeyAlgorithm;
            String cipherTransformation;
            int keyLength;
            switch (format) {
                case PKCS1:
                    String str = this.algorithm;
                    boolean z = -1;
                    switch (str.hashCode()) {
                        case -2020788375:
                            if (str.equals("DES-CBC")) {
                                z = false;
                                break;
                            }
                            break;
                        case -1390896596:
                            if (str.equals("AES-256-CBC")) {
                                z = 2;
                                break;
                            }
                            break;
                        case -165238049:
                            if (str.equals("DES-EDE3-CBC")) {
                                z = true;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case false:
                            secretKeyAlgorithm = "DES";
                            cipherTransformation = "DES/CBC/PKCS5Padding";
                            keyLength = 8;
                            break;
                        case true:
                            secretKeyAlgorithm = "DESede";
                            cipherTransformation = "DESede/CBC/PKCS5Padding";
                            keyLength = 24;
                            break;
                        case true:
                            secretKeyAlgorithm = "AES";
                            cipherTransformation = "AES/CBC/PKCS5Padding";
                            keyLength = 32;
                            break;
                        default:
                            secretKeyAlgorithm = this.algorithm;
                            cipherTransformation = this.algorithm;
                            keyLength = 8;
                            break;
                    }
                    byte[] iv = fromHex(this.ivHex);
                    byte[] key = deriveKeyPBKDF1(keyLength, password, iv);
                    SecretKey secretKey = new SecretKeySpec(key, secretKeyAlgorithm);
                    Cipher cipher = Cipher.getInstance(cipherTransformation);
                    cipher.init(2, secretKey, new IvParameterSpec(iv));
                    byte[] pkcs1 = cipher.doFinal(decode());
                    return toPrivateKey(keyAlgorithm, format, filename, pkcs1);
                case PKCS8:
                    Asn1Parser p = new Asn1Parser(decode());
                    p.parseTagSequence();
                    p.parseFullLength();
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidEncryptionAlgorithm = p.parseOIDAsBytes();
                    if (!Arrays.equals(oidEncryptionAlgorithm, PEMFile.OID_PBES2)) {
                        throw new NoSuchAlgorithmException(PEMFile.sm.getString("pemFile.unknownPkcs8Algorithm", toDottedOidString(oidEncryptionAlgorithm)));
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] oidKDF = p.parseOIDAsBytes();
                    if (!Arrays.equals(oidKDF, PEMFile.OID_PBKDF2)) {
                        throw new NoSuchAlgorithmException(PEMFile.sm.getString("pemFile.notPbkdf2", toDottedOidString(oidKDF)));
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    byte[] salt = p.parseOctetString();
                    int iterationCount = p.parseInt().intValue();
                    if (p.peekTag() == 2) {
                        p.parseInt().intValue();
                    }
                    p.parseTagSequence();
                    p.parseLength();
                    if (p.getNestedSequenceLevel() == 6) {
                        byte[] oidPRF = p.parseOIDAsBytes();
                        prf = PEMFile.OID_TO_PRF.get(HexUtils.toHexString(oidPRF));
                        if (prf == null) {
                            throw new NoSuchAlgorithmException(PEMFile.sm.getString("pemFile.unknownPrfAlgorithm", toDottedOidString(oidPRF)));
                        }
                        p.parseNull();
                        p.parseTagSequence();
                        p.parseLength();
                    } else {
                        prf = PEMFile.DEFAULT_PRF;
                    }
                    byte[] oidCipher = p.parseOIDAsBytes();
                    Algorithm algorithm = PEMFile.OID_TO_ALGORITHM.get(HexUtils.toHexString(oidCipher));
                    if (algorithm == null) {
                        throw new NoSuchAlgorithmException(PEMFile.sm.getString("pemFile.unknownEncryptionAlgorithm", toDottedOidString(oidCipher)));
                    }
                    byte[] iv2 = p.parseOctetString();
                    byte[] encryptedData = p.parseOctetString();
                    byte[] key2 = deriveKeyPBKDF2("PBKDF2With" + prf, password, salt, iterationCount, algorithm.getKeyLength());
                    SecretKey secretKey2 = new SecretKeySpec(key2, algorithm.getSecretKeyAlgorithm());
                    Cipher cipher2 = Cipher.getInstance(algorithm.getTransformation());
                    cipher2.init(2, secretKey2, new IvParameterSpec(iv2));
                    byte[] decryptedData = cipher2.doFinal(encryptedData);
                    return toPrivateKey(keyAlgorithm, format, filename, decryptedData);
                default:
                    throw new NoSuchAlgorithmException(PEMFile.sm.getString("pemFile.unknownEncryptedFormat", format));
            }
        }

        private PrivateKey toPrivateKey(String keyAlgorithm, Format format, String filename, byte[] source) throws GeneralSecurityException {
            KeySpec keySpec = null;
            switch (format) {
                case PKCS1:
                    keySpec = parsePKCS1(source);
                    break;
                case PKCS8:
                    keySpec = new PKCS8EncodedKeySpec(source);
                    break;
                case RFC5915:
                    keySpec = new PKCS8EncodedKeySpec(rfc5915ToPkcs8(source));
                    break;
            }
            InvalidKeyException exception = new InvalidKeyException(PEMFile.sm.getString("pemFile.parseError", filename));
            if (keyAlgorithm == null) {
                for (String algorithm : new String[]{"RSA", "DSA", "EC"}) {
                    try {
                        return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                    } catch (InvalidKeySpecException e) {
                        exception.addSuppressed(e);
                    }
                }
            } else {
                try {
                    return KeyFactory.getInstance(keyAlgorithm).generatePrivate(keySpec);
                } catch (InvalidKeySpecException e2) {
                    exception.addSuppressed(e2);
                }
            }
            throw exception;
        }

        private byte[] deriveKeyPBKDF1(int keyLength, String password, byte[] salt) throws NoSuchAlgorithmException {
            byte[] key = new byte[keyLength];
            int insertPosition = 0;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] pw = password.getBytes(StandardCharsets.UTF_8);
            while (insertPosition < keyLength) {
                digest.update(pw);
                digest.update(salt, 0, 8);
                byte[] round = digest.digest();
                digest.update(round);
                System.arraycopy(round, 0, key, insertPosition, Math.min(keyLength - insertPosition, round.length));
                insertPosition += round.length;
            }
            return key;
        }

        private byte[] deriveKeyPBKDF2(String algorithm, String password, byte[] salt, int iterations, int keyLength) throws GeneralSecurityException {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
            return secretKey.getEncoded();
        }

        /* JADX WARN: Type inference failed for: r0v30, types: [byte[], byte[][]] */
        /* JADX WARN: Type inference failed for: r3v3, types: [byte[], byte[][]] */
        /* JADX WARN: Type inference failed for: r3v6, types: [byte[], byte[][]] */
        private byte[] rfc5915ToPkcs8(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() != 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(4);
            int privateKeyLen = p.parseLength();
            byte[] privateKey = new byte[privateKeyLen];
            p.parseBytes(privateKey);
            p.parseTag(160);
            int oidLen = p.parseLength();
            byte[] oid = new byte[oidLen];
            p.parseBytes(oid);
            if (oid[0] != 6) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(Opcodes.IF_ICMPLT);
            int publicKeyLen = p.parseLength();
            byte[] publicKey = new byte[publicKeyLen];
            p.parseBytes(publicKey);
            if (publicKey[0] != 3) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            return Asn1Writer.writeSequence(new byte[]{Asn1Writer.writeInteger(0), Asn1Writer.writeSequence(new byte[]{PEMFile.OID_EC_PUBLIC_KEY, oid}), Asn1Writer.writeOctetString(Asn1Writer.writeSequence(new byte[]{Asn1Writer.writeInteger(1), Asn1Writer.writeOctetString(privateKey), Asn1Writer.writeTag((byte) -95, publicKey)}))});
        }

        private RSAPrivateCrtKeySpec parsePKCS1(byte[] source) {
            Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            BigInteger version = p.parseInt();
            if (version.intValue() == 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.noMultiPrimes"));
            }
            return new RSAPrivateCrtKeySpec(p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt());
        }

        private byte[] fromHex(String hexString) {
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length(); i += 2) {
                bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
            }
            return bytes;
        }

        private String toDottedOidString(byte[] oidBytes) {
            try {
                Oid oid = new Oid(oidBytes);
                return oid.toString();
            } catch (GSSException e) {
                return HexUtils.toHexString(oidBytes);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/PEMFile$Algorithm.class */
    public enum Algorithm {
        AES128_CBC_PAD("AES/CBC/PKCS5PADDING", "AES", 128),
        AES256_CBC_PAD("AES/CBC/PKCS5PADDING", "AES", 256),
        DES_EDE3_CBC("DESede/CBC/PKCS5Padding", "DESede", Opcodes.CHECKCAST);

        private final String transformation;
        private final String secretKeyAlgorithm;
        private final int keyLength;

        Algorithm(String transformation, String secretKeyAlgorithm, int keyLength) {
            this.transformation = transformation;
            this.secretKeyAlgorithm = secretKeyAlgorithm;
            this.keyLength = keyLength;
        }

        public String getTransformation() {
            return this.transformation;
        }

        public String getSecretKeyAlgorithm() {
            return this.secretKeyAlgorithm;
        }

        public int getKeyLength() {
            return this.keyLength;
        }
    }
}
