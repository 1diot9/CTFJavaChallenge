package org.h2.security;

import cn.hutool.crypto.KeyUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Properties;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.h2.api.ErrorCode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.security.auth.impl.JaasCredentialsValidator;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/CipherFactory.class */
public class CipherFactory {
    public static final String KEYSTORE_PASSWORD = "h2pass";
    public static final String LEGACY_ALGORITHMS_SECURITY_KEY = "jdk.tls.legacyAlgorithms";
    public static final String DEFAULT_LEGACY_ALGORITHMS = getLegacyAlgorithmsSilently();
    private static final String KEYSTORE = "~/.h2.keystore";
    private static final String KEYSTORE_KEY = "javax.net.ssl.keyStore";
    private static final String KEYSTORE_PASSWORD_KEY = "javax.net.ssl.keyStorePassword";

    private CipherFactory() {
    }

    public static BlockCipher getBlockCipher(String str) {
        if ("XTEA".equalsIgnoreCase(str)) {
            return new XTEA();
        }
        if ("AES".equalsIgnoreCase(str)) {
            return new AES();
        }
        if ("FOG".equalsIgnoreCase(str)) {
            return new Fog();
        }
        throw DbException.get(ErrorCode.UNSUPPORTED_CIPHER, str);
    }

    public static Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        setKeystore();
        SSLSocket sSLSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket();
        sSLSocket.connect(new InetSocketAddress(inetAddress, i), SysProperties.SOCKET_CONNECT_TIMEOUT);
        sSLSocket.setEnabledProtocols(disableSSL(sSLSocket.getEnabledProtocols()));
        if (SysProperties.ENABLE_ANONYMOUS_TLS) {
            sSLSocket.setEnabledCipherSuites(enableAnonymous(sSLSocket.getEnabledCipherSuites(), sSLSocket.getSupportedCipherSuites()));
        }
        return sSLSocket;
    }

    public static ServerSocket createServerSocket(int i, InetAddress inetAddress) throws IOException {
        SSLServerSocket sSLServerSocket;
        if (SysProperties.ENABLE_ANONYMOUS_TLS) {
            removeAnonFromLegacyAlgorithms();
        }
        setKeystore();
        ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
        if (inetAddress == null) {
            sSLServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(i);
        } else {
            sSLServerSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(i, 0, inetAddress);
        }
        sSLServerSocket.setEnabledProtocols(disableSSL(sSLServerSocket.getEnabledProtocols()));
        if (SysProperties.ENABLE_ANONYMOUS_TLS) {
            sSLServerSocket.setEnabledCipherSuites(enableAnonymous(sSLServerSocket.getEnabledCipherSuites(), sSLServerSocket.getSupportedCipherSuites()));
        }
        return sSLServerSocket;
    }

    public static String removeDhAnonFromCommaSeparatedList(String str) {
        if (str == null) {
            return str;
        }
        LinkedList linkedList = new LinkedList(Arrays.asList(str.split("\\s*,\\s*")));
        boolean remove = linkedList.remove("DH_anon");
        boolean remove2 = linkedList.remove("ECDH_anon");
        if (remove || remove2) {
            String arrays = Arrays.toString(linkedList.toArray(new String[linkedList.size()]));
            return !linkedList.isEmpty() ? arrays.substring(1, arrays.length() - 1) : "";
        }
        return str;
    }

    public static synchronized void removeAnonFromLegacyAlgorithms() {
        String legacyAlgorithmsSilently = getLegacyAlgorithmsSilently();
        if (legacyAlgorithmsSilently == null) {
            return;
        }
        String removeDhAnonFromCommaSeparatedList = removeDhAnonFromCommaSeparatedList(legacyAlgorithmsSilently);
        if (!legacyAlgorithmsSilently.equals(removeDhAnonFromCommaSeparatedList)) {
            setLegacyAlgorithmsSilently(removeDhAnonFromCommaSeparatedList);
        }
    }

    public static synchronized void resetDefaultLegacyAlgorithms() {
        setLegacyAlgorithmsSilently(DEFAULT_LEGACY_ALGORITHMS);
    }

    public static String getLegacyAlgorithmsSilently() {
        String str = null;
        try {
            str = Security.getProperty(LEGACY_ALGORITHMS_SECURITY_KEY);
        } catch (SecurityException e) {
        }
        return str;
    }

    private static void setLegacyAlgorithmsSilently(String str) {
        if (str == null) {
            return;
        }
        try {
            Security.setProperty(LEGACY_ALGORITHMS_SECURITY_KEY, str);
        } catch (SecurityException e) {
        }
    }

    private static byte[] getKeyStoreBytes(KeyStore keyStore, String str) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            keyStore.store(byteArrayOutputStream, str.toCharArray());
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    public static KeyStore getKeyStore(String str) throws IOException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, str.toCharArray());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            keyStore.load(null, str.toCharArray());
            keyStore.setKeyEntry(JaasCredentialsValidator.DEFAULT_APPNAME, keyFactory.generatePrivate(new PKCS8EncodedKeySpec(StringUtils.convertHexToBytes("30820277020100300d06092a864886f70d0101010500048202613082025d02010002818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d02030100010281810099aa4ff4d0a09a5af0bd953cb10c4d08c3d98df565664ac5582e494314d5c3c92dddedd5d316a32a206be4ec084616fe57be15e27cad111aa3c21fa79e32258c6ca8430afc69eddd52d3b751b37da6b6860910b94653192c0db1d02abcfd6ce14c01f238eec7c20bd3bb750940004bacba2880349a9494d10e139ecb2355d101024100ffdc3defd9c05a2d377ef6019fa62b3fbd5b0020a04cc8533bca730e1f6fcf5dfceea1b044fbe17d9eababfbc7d955edad6bc60f9be826ad2c22ba77d19a9f65024100dc28d43fdbbc93852cc3567093157702bc16f156f709fb7db0d9eec028f41fd0edcd17224c866e66be1744141fb724a10fd741c8a96afdd9141b36d67fff6309024077b1cddbde0f69604bdcfe33263fb36ddf24aa3b9922327915b890f8a36648295d0139ecdf68c245652c4489c6257b58744fbdd961834a4cab201801a3b1e52d024100b17142e8991d1b350a0802624759d48ae2b8071a158ff91fabeb6a8f7c328e762143dc726b8529f42b1fab6220d1c676fdc27ba5d44e847c72c52064afd351a902407c6e23fe35bcfcd1a662aa82a2aa725fcece311644d5b6e3894853fd4ce9fe78218c957b1ff03fc9e5ef8ffeb6bd58235f6a215c97d354fdace7e781e4a63e8b"))), str.toCharArray(), new Certificate[]{CertificateFactory.getInstance(KeyUtil.CERT_TYPE_X509).generateCertificate(new ByteArrayInputStream(StringUtils.convertHexToBytes("3082018b3081f502044295ce6b300d06092a864886f70d0101040500300d310b3009060355040313024832301e170d3035303532363133323630335a170d3337303933303036353734375a300d310b300906035504031302483230819f300d06092a864886f70d010101050003818d0030818902818100dc0a13c602b7141110eade2f051b54777b060d0f74e6a110f9cce81159f271ebc88d8e8aa1f743b505fc2e7dfe38d33b8d3f64d1b363d1af4d877833897954cbaec2fa384c22a415498cf306bb07ac09b76b001cd68bf77ea0a628f5101959cf2993a9c23dbee79b19305977f8715ae78d023471194cc900b231eecb0aaea98d0203010001300d06092a864886f70d01010405000381810083f4401a279453701bef9a7681a5b8b24f153f7d18c7c892133d97bd5f13736be7505290a445a7d5ceb75522403e5097515cd966ded6351ff60d5193de34cd36e5cb04d380398e66286f99923fd92296645fd4ada45844d194dfd815e6cd57f385c117be982809028bba1116c85740b3d27a55b1a0948bf291ddba44bed337b9")))});
            return keyStore;
        } catch (Exception e) {
            throw DataUtils.convertToIOException(e);
        }
    }

    private static void setKeystore() throws IOException {
        byte[] readBytesAndClose;
        Properties properties = System.getProperties();
        if (properties.getProperty(KEYSTORE_KEY) == null) {
            byte[] keyStoreBytes = getKeyStoreBytes(getKeyStore(KEYSTORE_PASSWORD), KEYSTORE_PASSWORD);
            boolean z = true;
            if (FileUtils.exists(KEYSTORE) && FileUtils.size(KEYSTORE) == keyStoreBytes.length && (readBytesAndClose = IOUtils.readBytesAndClose(FileUtils.newInputStream(KEYSTORE), 0)) != null && Arrays.equals(keyStoreBytes, readBytesAndClose)) {
                z = false;
            }
            if (z) {
                try {
                    OutputStream newOutputStream = FileUtils.newOutputStream(KEYSTORE, false);
                    newOutputStream.write(keyStoreBytes);
                    newOutputStream.close();
                } catch (Exception e) {
                    throw DataUtils.convertToIOException(e);
                }
            }
            System.setProperty(KEYSTORE_KEY, FileUtils.toRealPath(KEYSTORE));
        }
        if (properties.getProperty(KEYSTORE_PASSWORD_KEY) == null) {
            System.setProperty(KEYSTORE_PASSWORD_KEY, KEYSTORE_PASSWORD);
        }
    }

    private static String[] enableAnonymous(String[] strArr, String[] strArr2) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : strArr2) {
            if (!str.startsWith("SSL") && str.contains("_anon_") && ((str.contains("_AES_") || str.contains("_3DES_")) && str.contains("_SHA"))) {
                linkedHashSet.add(str);
            }
        }
        Collections.addAll(linkedHashSet, strArr);
        return (String[]) linkedHashSet.toArray(new String[0]);
    }

    private static String[] disableSSL(String[] strArr) {
        HashSet hashSet = new HashSet();
        for (String str : strArr) {
            if (!str.startsWith("SSL")) {
                hashSet.add(str);
            }
        }
        return (String[]) hashSet.toArray(new String[0]);
    }
}
