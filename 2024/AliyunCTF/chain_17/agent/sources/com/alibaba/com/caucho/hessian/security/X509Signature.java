package com.alibaba.com.caucho.hessian.security;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.HessianEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/security/X509Signature.class */
public class X509Signature extends HessianEnvelope {
    private String _algorithm = "HmacSHA256";
    private X509Certificate _cert;
    private PrivateKey _privateKey;
    private SecureRandom _secureRandom;

    public String getAlgorithm() {
        return this._algorithm;
    }

    public void setAlgorithm(String algorithm) {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        this._algorithm = algorithm;
    }

    public X509Certificate getCertificate() {
        return this._cert;
    }

    public void setCertificate(X509Certificate cert) {
        this._cert = cert;
    }

    public PrivateKey getPrivateKey() {
        return this._privateKey;
    }

    public void setPrivateKey(PrivateKey key) {
        this._privateKey = key;
    }

    public SecureRandom getSecureRandom() {
        return this._secureRandom;
    }

    public void setSecureRandom(SecureRandom random) {
        this._secureRandom = random;
    }

    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        if (this._privateKey == null) {
            throw new IOException("X509Signature.wrap requires a private key");
        }
        if (this._cert == null) {
            throw new IOException("X509Signature.wrap requires a certificate");
        }
        OutputStream os = new SignatureOutputStream(out);
        Hessian2Output filterOut = new Hessian2Output(os);
        filterOut.setCloseStreamOnClose(true);
        return filterOut;
    }

    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        if (this._cert == null) {
            throw new IOException("X509Signature.unwrap requires a certificate");
        }
        in.readEnvelope();
        String method = in.readMethod();
        if (!method.equals(getClass().getName())) {
            throw new IOException("expected hessian Envelope method '" + getClass().getName() + "' at '" + method + "'");
        }
        return unwrapHeaders(in);
    }

    @Override // com.alibaba.com.caucho.hessian.io.HessianEnvelope
    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        if (this._cert == null) {
            throw new IOException("X509Signature.unwrap requires a certificate");
        }
        InputStream is = new SignatureInputStream(in);
        Hessian2Input filter = new Hessian2Input(is);
        filter.setCloseStreamOnClose(true);
        return filter;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/security/X509Signature$SignatureOutputStream.class */
    class SignatureOutputStream extends OutputStream {
        private Hessian2Output _out;
        private OutputStream _bodyOut;
        private Mac _mac;

        SignatureOutputStream(Hessian2Output out) throws IOException {
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance(X509Signature.this._algorithm);
                if (X509Signature.this._secureRandom != null) {
                    keyGen.init(X509Signature.this._secureRandom);
                }
                SecretKey sharedKey = keyGen.generateKey();
                this._out = out;
                this._out.startEnvelope(X509Signature.class.getName());
                PublicKey publicKey = X509Signature.this._cert.getPublicKey();
                byte[] encoded = publicKey.getEncoded();
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(encoded);
                byte[] fingerprint = md.digest();
                String keyAlgorithm = X509Signature.this._privateKey.getAlgorithm();
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                keyCipher.init(3, X509Signature.this._privateKey);
                byte[] encKey = keyCipher.wrap(sharedKey);
                this._out.writeInt(4);
                this._out.writeString("algorithm");
                this._out.writeString(X509Signature.this._algorithm);
                this._out.writeString("fingerprint");
                this._out.writeBytes(fingerprint);
                this._out.writeString("key-algorithm");
                this._out.writeString(keyAlgorithm);
                this._out.writeString("key");
                this._out.writeBytes(encKey);
                this._mac = Mac.getInstance(X509Signature.this._algorithm);
                this._mac.init(sharedKey);
                this._bodyOut = this._out.getBytesOutputStream();
            } catch (IOException e) {
                throw e;
            } catch (RuntimeException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        }

        @Override // java.io.OutputStream
        public void write(int ch2) throws IOException {
            this._bodyOut.write(ch2);
            this._mac.update((byte) ch2);
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._bodyOut.write(buffer, offset, length);
            this._mac.update(buffer, offset, length);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;
            if (out == null) {
                return;
            }
            this._bodyOut.close();
            byte[] sig = this._mac.doFinal();
            out.writeInt(1);
            out.writeString("signature");
            out.writeBytes(sig);
            out.completeEnvelope();
            out.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/security/X509Signature$SignatureInputStream.class */
    public class SignatureInputStream extends InputStream {
        private Hessian2Input _in;
        private Mac _mac;
        private InputStream _bodyIn;
        private CipherInputStream _cipherIn;

        SignatureInputStream(Hessian2Input in) throws IOException {
            try {
                this._in = in;
                String keyAlgorithm = null;
                String algorithm = null;
                byte[] encKey = null;
                int len = in.readInt();
                for (int i = 0; i < len; i++) {
                    String header = in.readString();
                    if ("fingerprint".equals(header)) {
                        in.readBytes();
                    } else if ("key-algorithm".equals(header)) {
                        keyAlgorithm = in.readString();
                    } else if ("algorithm".equals(header)) {
                        algorithm = in.readString();
                    } else if ("key".equals(header)) {
                        encKey = in.readBytes();
                    } else {
                        throw new IOException("'" + header + "' is an unexpected header");
                    }
                }
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                keyCipher.init(4, X509Signature.this._cert);
                Key key = keyCipher.unwrap(encKey, algorithm, 3);
                this._bodyIn = this._in.readInputStream();
                this._mac = Mac.getInstance(algorithm);
                this._mac.init(key);
            } catch (IOException e) {
                throw e;
            } catch (RuntimeException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ch2 = this._bodyIn.read();
            if (ch2 < 0) {
                return ch2;
            }
            this._mac.update((byte) ch2);
            return ch2;
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int len = this._bodyIn.read(buffer, offset, length);
            if (len < 0) {
                return len;
            }
            this._mac.update(buffer, offset, len);
            return len;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;
            if (in != null) {
                this._bodyIn.close();
                int len = in.readInt();
                byte[] signature = null;
                for (int i = 0; i < len; i++) {
                    String header = in.readString();
                    if ("signature".equals(header)) {
                        signature = in.readBytes();
                    }
                }
                in.completeEnvelope();
                in.close();
                if (signature == null) {
                    throw new IOException("Expected signature");
                }
                byte[] sig = this._mac.doFinal();
                if (sig.length != signature.length) {
                    throw new IOException("mismatched signature");
                }
                for (int i2 = 0; i2 < sig.length; i2++) {
                    if (signature[i2] != sig[i2]) {
                        throw new IOException("mismatched signature");
                    }
                }
            }
        }
    }
}
