package cn.hutool.crypto.asymmetric;

import java.security.PrivateKey;
import java.security.PublicKey;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/ECIES.class */
public class ECIES extends AsymmetricCrypto {
    private static final long serialVersionUID = 1;
    private static final String ALGORITHM_ECIES = "ECIES";

    public ECIES() {
        super(ALGORITHM_ECIES);
    }

    public ECIES(String eciesAlgorithm) {
        super(eciesAlgorithm);
    }

    public ECIES(String privateKeyStr, String publicKeyStr) {
        super(ALGORITHM_ECIES, privateKeyStr, publicKeyStr);
    }

    public ECIES(String eciesAlgorithm, String privateKeyStr, String publicKeyStr) {
        super(eciesAlgorithm, privateKeyStr, publicKeyStr);
    }

    public ECIES(byte[] privateKey, byte[] publicKey) {
        super(ALGORITHM_ECIES, privateKey, publicKey);
    }

    public ECIES(PrivateKey privateKey, PublicKey publicKey) {
        super(ALGORITHM_ECIES, privateKey, publicKey);
    }

    public ECIES(String eciesAlgorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(eciesAlgorithm, privateKey, publicKey);
    }
}
