package cn.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/BCMacEngine.class */
public class BCMacEngine implements MacEngine {
    private org.bouncycastle.crypto.Mac mac;

    public BCMacEngine(org.bouncycastle.crypto.Mac mac, CipherParameters params) {
        init(mac, params);
    }

    public BCMacEngine init(org.bouncycastle.crypto.Mac mac, CipherParameters params) {
        mac.init(params);
        this.mac = mac;
        return this;
    }

    public org.bouncycastle.crypto.Mac getMac() {
        return this.mac;
    }

    @Override // cn.hutool.crypto.digest.mac.MacEngine
    public void update(byte[] in, int inOff, int len) {
        this.mac.update(in, inOff, len);
    }

    @Override // cn.hutool.crypto.digest.mac.MacEngine
    public byte[] doFinal() {
        byte[] result = new byte[getMacLength()];
        this.mac.doFinal(result, 0);
        return result;
    }

    @Override // cn.hutool.crypto.digest.mac.MacEngine
    public void reset() {
        this.mac.reset();
    }

    @Override // cn.hutool.crypto.digest.mac.MacEngine
    public int getMacLength() {
        return this.mac.getMacSize();
    }

    @Override // cn.hutool.crypto.digest.mac.MacEngine
    public String getAlgorithm() {
        return this.mac.getAlgorithmName();
    }
}
