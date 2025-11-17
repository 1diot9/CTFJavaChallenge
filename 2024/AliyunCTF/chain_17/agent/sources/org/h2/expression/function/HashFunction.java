package org.h2.expression.function;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.security.SHA3;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/HashFunction.class */
public final class HashFunction extends FunctionN {
    public static final int HASH = 0;
    public static final int ORA_HASH = 1;
    private static final String[] NAMES = {"HASH", "ORA_HASH"};
    private final int function;

    public HashFunction(Expression expression, int i) {
        super(new Expression[]{expression});
        this.function = i;
    }

    public HashFunction(Expression expression, Expression expression2, Expression expression3, int i) {
        super(expression3 == null ? new Expression[]{expression, expression2} : new Expression[]{expression, expression2, expression3});
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        Value oraHash;
        switch (this.function) {
            case 0:
                oraHash = getHash(value.getString(), value2, value3 == null ? 1 : value3.getInt());
                break;
            case 1:
                oraHash = oraHash(value, value2 == null ? 4294967295L : value2.getLong(), value3 == null ? 0L : value3.getLong());
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return oraHash;
    }

    private static Value getHash(String str, Value value, int i) {
        MessageDigest hashImpl;
        if (i <= 0) {
            throw DbException.getInvalidValueException("iterations", Integer.valueOf(i));
        }
        String upperEnglish = StringUtils.toUpperEnglish(str);
        boolean z = -1;
        switch (upperEnglish.hashCode()) {
            case -1850268089:
                if (upperEnglish.equals("SHA256")) {
                    z = 6;
                    break;
                }
                break;
            case -1523887821:
                if (upperEnglish.equals("SHA-224")) {
                    z = 2;
                    break;
                }
                break;
            case -1523887726:
                if (upperEnglish.equals("SHA-256")) {
                    z = 3;
                    break;
                }
                break;
            case -1523886674:
                if (upperEnglish.equals("SHA-384")) {
                    z = 4;
                    break;
                }
                break;
            case -1523884971:
                if (upperEnglish.equals("SHA-512")) {
                    z = 5;
                    break;
                }
                break;
            case 76158:
                if (upperEnglish.equals("MD5")) {
                    z = false;
                    break;
                }
                break;
            case 9509966:
                if (upperEnglish.equals("SHA3-224")) {
                    z = 7;
                    break;
                }
                break;
            case 9510061:
                if (upperEnglish.equals("SHA3-256")) {
                    z = 8;
                    break;
                }
                break;
            case 9511113:
                if (upperEnglish.equals("SHA3-384")) {
                    z = 9;
                    break;
                }
                break;
            case 9512816:
                if (upperEnglish.equals("SHA3-512")) {
                    z = 10;
                    break;
                }
                break;
            case 78861104:
                if (upperEnglish.equals("SHA-1")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
            case true:
            case true:
            case true:
            case true:
                hashImpl = hashImpl(value, str);
                break;
            case true:
                hashImpl = hashImpl(value, "SHA-256");
                break;
            case true:
                hashImpl = hashImpl(value, SHA3.getSha3_224());
                break;
            case true:
                hashImpl = hashImpl(value, SHA3.getSha3_256());
                break;
            case true:
                hashImpl = hashImpl(value, SHA3.getSha3_384());
                break;
            case true:
                hashImpl = hashImpl(value, SHA3.getSha3_512());
                break;
            default:
                throw DbException.getInvalidValueException("algorithm", str);
        }
        byte[] digest = hashImpl.digest();
        for (int i2 = 1; i2 < i; i2++) {
            digest = hashImpl.digest(digest);
        }
        return ValueVarbinary.getNoCopy(digest);
    }

    private static Value oraHash(Value value, long j, long j2) {
        if ((j & (-4294967296L)) != 0) {
            throw DbException.getInvalidValueException("bucket", Long.valueOf(j));
        }
        if ((j2 & (-4294967296L)) != 0) {
            throw DbException.getInvalidValueException("seed", Long.valueOf(j2));
        }
        MessageDigest hashImpl = hashImpl(value, "SHA-1");
        if (hashImpl == null) {
            return ValueNull.INSTANCE;
        }
        if (j2 != 0) {
            byte[] bArr = new byte[4];
            Bits.writeInt(bArr, 0, (int) j2);
            hashImpl.update(bArr);
        }
        return ValueBigint.get((Bits.readLong(hashImpl.digest(), 0) & Long.MAX_VALUE) % (j + 1));
    }

    private static MessageDigest hashImpl(Value value, String str) {
        try {
            return hashImpl(value, MessageDigest.getInstance(str));
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0004. Please report as an issue. */
    private static MessageDigest hashImpl(Value value, MessageDigest messageDigest) {
        try {
            switch (value.getValueType()) {
                case 1:
                case 2:
                case 4:
                    messageDigest.update(value.getString().getBytes(StandardCharsets.UTF_8));
                    return messageDigest;
                case 3:
                case 7:
                    byte[] bArr = new byte[4096];
                    InputStream inputStream = value.getInputStream();
                    Throwable th = null;
                    while (true) {
                        try {
                            try {
                                int read = inputStream.read(bArr);
                                if (read > 0) {
                                    messageDigest.update(bArr, 0, read);
                                } else {
                                    if (inputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                inputStream.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                        } else {
                                            inputStream.close();
                                        }
                                    }
                                    return messageDigest;
                                }
                            } finally {
                            }
                        } finally {
                        }
                    }
                case 5:
                case 6:
                default:
                    messageDigest.update(value.getBytesNoCopy());
                    return messageDigest;
            }
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
                this.type = TypeInfo.TYPE_VARBINARY;
                break;
            case 1:
                this.type = TypeInfo.TYPE_BIGINT;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
