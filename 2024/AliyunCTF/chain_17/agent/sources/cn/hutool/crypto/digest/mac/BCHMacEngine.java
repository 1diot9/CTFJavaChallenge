package cn.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/BCHMacEngine.class */
public class BCHMacEngine extends BCMacEngine {
    public BCHMacEngine(Digest digest, byte[] key, byte[] iv) {
        this(digest, (CipherParameters) new ParametersWithIV(new KeyParameter(key), iv));
    }

    public BCHMacEngine(Digest digest, byte[] key) {
        this(digest, (CipherParameters) new KeyParameter(key));
    }

    public BCHMacEngine(Digest digest, CipherParameters params) {
        this(new HMac(digest), params);
    }

    public BCHMacEngine(HMac mac, CipherParameters params) {
        super(mac, params);
    }

    public BCHMacEngine init(Digest digest, CipherParameters params) {
        return (BCHMacEngine) init((org.bouncycastle.crypto.Mac) new HMac(digest), params);
    }
}
