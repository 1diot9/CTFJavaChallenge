package cn.hutool.crypto;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.util.ASN1Dump;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/ASN1Util.class */
public class ASN1Util {
    public static byte[] encodeDer(ASN1Encodable... elements) {
        return encode("DER", elements);
    }

    public static byte[] encode(String asn1Encoding, ASN1Encodable... elements) {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        encodeTo(asn1Encoding, out, elements);
        return out.toByteArray();
    }

    public static void encodeTo(String asn1Encoding, OutputStream out, ASN1Encodable... elements) {
        DERSequence dLSequence;
        boolean z = -1;
        switch (asn1Encoding.hashCode()) {
            case 2184:
                if (asn1Encoding.equals("DL")) {
                    z = 2;
                    break;
                }
                break;
            case 65647:
                if (asn1Encoding.equals("BER")) {
                    z = true;
                    break;
                }
                break;
            case 67569:
                if (asn1Encoding.equals("DER")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                dLSequence = new DERSequence(elements);
                break;
            case true:
                dLSequence = new BERSequence(elements);
                break;
            case true:
                dLSequence = new DLSequence(elements);
                break;
            default:
                throw new CryptoException("Unsupported ASN1 encoding: {}", asn1Encoding);
        }
        try {
            dLSequence.encodeTo(out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static ASN1Object decode(InputStream in) {
        ASN1InputStream asn1In = new ASN1InputStream(in);
        try {
            return asn1In.readObject();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static String getDumpStr(InputStream in) {
        return ASN1Dump.dumpAsString(decode(in));
    }
}
