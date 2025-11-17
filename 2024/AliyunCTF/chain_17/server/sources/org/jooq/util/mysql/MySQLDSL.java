package org.jooq.util.mysql;

import org.jetbrains.annotations.NotNull;
import org.jooq.Constants;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/mysql/MySQLDSL.class */
public class MySQLDSL extends DSL {
    protected MySQLDSL() {
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> decode(String cryptString, String keyString) {
        return decode(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> decode(byte[] cryptString, byte[] keyString) {
        return decode(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> decode(Field<T> cryptString, Field<T> keyString) {
        return function("decode", cryptString.getType(), (Field<?>[]) new Field[]{cryptString, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> encode(String string, String keyString) {
        return encode(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> encode(byte[] string, byte[] keyString) {
        return encode(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> encode(Field<T> string, Field<T> keyString) {
        return function("encode", string.getType(), (Field<?>[]) new Field[]{string, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> aesDecrypt(String cryptString, String keyString) {
        return aesDecrypt(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> aesDecrypt(byte[] cryptString, byte[] keyString) {
        return aesDecrypt(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> aesDecrypt(Field<T> cryptString, Field<T> keyString) {
        return function("aes_decrypt", cryptString.getType(), (Field<?>[]) new Field[]{cryptString, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> aesEncrypt(String string, String keyString) {
        return aesEncrypt(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> aesEncrypt(byte[] string, byte[] keyString) {
        return aesEncrypt(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> aesEncrypt(Field<T> string, Field<T> keyString) {
        return function("aes_encrypt", string.getType(), (Field<?>[]) new Field[]{string, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> desDecrypt(String cryptString) {
        return desDecrypt(val(cryptString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> desDecrypt(byte[] cryptString) {
        return desDecrypt(val(cryptString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> desDecrypt(Field<T> cryptString) {
        return function("des_decrypt", cryptString.getType(), (Field<?>[]) new Field[]{cryptString});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> desDecrypt(String cryptString, String keyString) {
        return desDecrypt(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> desDecrypt(byte[] cryptString, byte[] keyString) {
        return desDecrypt(val(cryptString), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> desDecrypt(Field<T> cryptString, Field<T> keyString) {
        return function("des_decrypt", cryptString.getType(), (Field<?>[]) new Field[]{cryptString, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> desEncrypt(String string) {
        return desEncrypt(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> desEncrypt(byte[] string) {
        return desEncrypt(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> desEncrypt(Field<T> string) {
        return function("des_encrypt", string.getType(), (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> desEncrypt(String string, String keyString) {
        return desEncrypt(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> desEncrypt(byte[] string, byte[] keyString) {
        return desEncrypt(val(string), val(keyString));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> desEncrypt(Field<T> string, Field<T> keyString) {
        return function("des_encrypt", string.getType(), (Field<?>[]) new Field[]{string, keyString});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> compress(String string) {
        return compress(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> compress(byte[] string) {
        return compress(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> compress(Field<T> string) {
        return function("compress", string.getType(), (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> uncompress(String string) {
        return uncompress(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> uncompress(byte[] string) {
        return uncompress(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> uncompress(Field<T> string) {
        return function("uncompress", string.getType(), (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<Integer> uncompressedLength(String string) {
        return uncompressedLength(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<Integer> uncompressedLength(byte[] string) {
        return uncompressedLength(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<Integer> uncompressedLength(Field<T> string) {
        return function("uncompressed_length", Integer.class, (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> sha1(String string) {
        return sha1(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> sha1(byte[] string) {
        return sha1(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> sha1(Field<T> string) {
        return function("sha1", string.getType(), (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<String> sha2(String string, int hashLength) {
        return sha2(val(string), val(hashLength));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static Field<byte[]> sha2(byte[] string, int hashLength) {
        return sha2(val(string), val(hashLength));
    }

    @Support({SQLDialect.MYSQL})
    @NotNull
    public static <T> Field<T> sha2(Field<T> string, Field<Integer> hashLength) {
        return function("sha2", string.getType(), (Field<?>[]) new Field[]{string, hashLength});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<String> password(String string) {
        return password(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static Field<byte[]> password(byte[] string) {
        return password(val(string));
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
    @NotNull
    public static <T> Field<T> password(Field<T> string) {
        return function("password", string.getType(), (Field<?>[]) new Field[]{string});
    }

    @Support({SQLDialect.MYSQL})
    @Deprecated(forRemoval = true)
    @NotNull
    public static <T> Field<T> values(Field<T> values) {
        return function("values", values.getDataType(), (Field<?>[]) new Field[]{values});
    }

    /* JADX WARN: Incorrect return type in method signature: <E:Ljava/lang/Enum<TE;>;:Lorg/jooq/EnumType;>(Ljava/lang/Class<TE;>;I)TE; */
    public static Enum enumType(Class cls, int index) {
        if (index <= 0) {
            return null;
        }
        Enum[] enumArr = (Enum[]) cls.getEnumConstants();
        if (index > enumArr.length) {
            return null;
        }
        return enumArr[index - 1];
    }
}
