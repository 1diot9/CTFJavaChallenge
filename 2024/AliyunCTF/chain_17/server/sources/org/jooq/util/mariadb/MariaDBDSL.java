package org.jooq.util.mariadb;

import org.jooq.Field;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/mariadb/MariaDBDSL.class */
public class MariaDBDSL extends DSL {
    protected MariaDBDSL() {
    }

    public static Field<String> decode(String cryptString, String keyString) {
        return decode(val(cryptString), val(keyString));
    }

    public static Field<String> decode(Field<String> cryptString, Field<String> keyString) {
        return function("decode", String.class, (Field<?>[]) new Field[]{cryptString, keyString});
    }

    public static Field<String> encode(String string, String keyString) {
        return encode(val(string), val(keyString));
    }

    public static Field<String> encode(Field<String> string, Field<String> keyString) {
        return function("encode", String.class, (Field<?>[]) new Field[]{string, keyString});
    }

    public static Field<String> aesDecrypt(String cryptString, String keyString) {
        return aesDecrypt(val(cryptString), val(keyString));
    }

    public static Field<String> aesDecrypt(Field<String> cryptString, Field<String> keyString) {
        return function("aes_decrypt", String.class, (Field<?>[]) new Field[]{cryptString, keyString});
    }

    public static Field<String> aesEncrypt(String string, String keyString) {
        return aesEncrypt(val(string), val(keyString));
    }

    public static Field<String> aesEncrypt(Field<String> string, Field<String> keyString) {
        return function("aes_encrypt", String.class, (Field<?>[]) new Field[]{string, keyString});
    }

    public static Field<String> desDecrypt(String cryptString) {
        return desDecrypt(val(cryptString));
    }

    public static Field<String> desDecrypt(Field<String> cryptString) {
        return function("des_decrypt", String.class, (Field<?>[]) new Field[]{cryptString});
    }

    public static Field<String> desDecrypt(String cryptString, String keyString) {
        return desDecrypt(val(cryptString), val(keyString));
    }

    public static Field<String> desDecrypt(Field<String> cryptString, Field<String> keyString) {
        return function("des_decrypt", String.class, (Field<?>[]) new Field[]{cryptString, keyString});
    }

    public static Field<String> desEncrypt(String string) {
        return desEncrypt(val(string));
    }

    public static Field<String> desEncrypt(Field<String> string) {
        return function("des_encrypt", String.class, (Field<?>[]) new Field[]{string});
    }

    public static Field<String> desEncrypt(String string, String keyString) {
        return desEncrypt(val(string), val(keyString));
    }

    public static Field<String> desEncrypt(Field<String> string, Field<String> keyString) {
        return function("des_encrypt", String.class, (Field<?>[]) new Field[]{string, keyString});
    }

    public static Field<String> compress(String string) {
        return compress(val(string));
    }

    public static Field<String> compress(Field<String> string) {
        return function("compress", String.class, (Field<?>[]) new Field[]{string});
    }

    public static Field<String> uncompress(String string) {
        return uncompress(val(string));
    }

    public static Field<String> uncompress(Field<String> string) {
        return function("uncompress", String.class, (Field<?>[]) new Field[]{string});
    }

    public static Field<Integer> uncompressedLength(String string) {
        return uncompressedLength(val(string));
    }

    public static Field<Integer> uncompressedLength(Field<String> string) {
        return function("uncompressed_length", Integer.class, (Field<?>[]) new Field[]{string});
    }

    public static Field<String> sha1(String string) {
        return sha1(val(string));
    }

    public static Field<String> sha1(Field<String> string) {
        return function("sha1", String.class, (Field<?>[]) new Field[]{string});
    }

    public static Field<String> password(String string) {
        return password(val(string));
    }

    public static Field<String> password(Field<String> string) {
        return function("password", String.class, (Field<?>[]) new Field[]{string});
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
