package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.security.BlockCipher;
import org.h2.security.CipherFactory;
import org.h2.util.MathUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarbinary;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CryptFunction.class */
public final class CryptFunction extends FunctionN {
    public static final int ENCRYPT = 0;
    public static final int DECRYPT = 1;
    private static final String[] NAMES = {"ENCRYPT", "DECRYPT"};
    private final int function;

    public CryptFunction(Expression expression, Expression expression2, Expression expression3, int i) {
        super(new Expression[]{expression, expression2, expression3});
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        BlockCipher blockCipher = CipherFactory.getBlockCipher(value.getString());
        blockCipher.setKey(getPaddedArrayCopy(value2.getBytesNoCopy(), blockCipher.getKeyLength()));
        byte[] paddedArrayCopy = getPaddedArrayCopy(value3.getBytesNoCopy(), 16);
        switch (this.function) {
            case 0:
                blockCipher.encrypt(paddedArrayCopy, 0, paddedArrayCopy.length);
                break;
            case 1:
                blockCipher.decrypt(paddedArrayCopy, 0, paddedArrayCopy.length);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueVarbinary.getNoCopy(paddedArrayCopy);
    }

    private static byte[] getPaddedArrayCopy(byte[] bArr, int i) {
        return Utils.copyBytes(bArr, MathUtils.roundUpInt(bArr.length, i));
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        TypeInfo type = this.args[2].getType();
        this.type = DataType.isBinaryStringType(type.getValueType()) ? TypeInfo.getTypeInfo(6, type.getPrecision(), 0, null) : TypeInfo.TYPE_VARBINARY;
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
