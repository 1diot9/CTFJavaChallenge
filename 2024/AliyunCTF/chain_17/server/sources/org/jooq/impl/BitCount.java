package org.jooq.impl;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.FileSize;
import org.apache.tomcat.jni.SSL;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.asm.Opcodes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitCount.class */
public final class BitCount extends AbstractField<Integer> implements QOM.BitCount {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitCount(Field<? extends Number> value) {
        super(Names.N_BIT_COUNT, Tools.allNotNull(SQLDataType.INTEGER, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case MARIADB:
            case MYSQL:
                ctx.visit(Names.N_BIT_COUNT).sql('(').visit((Field<?>) this.value).sql(')');
                return;
            case H2:
            case HSQLDB:
                bitAndDivEmulation(ctx);
                return;
            default:
                bitAndShrEmulation(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    private final void bitAndDivEmulation(Context<?> ctx) {
        if (this.value.getType() == Byte.class) {
            Field<? extends Number> field = this.value;
            ctx.visit(DSL.bitAnd(field, DSL.inline((byte) 1)).add(DSL.bitAnd(field, DSL.inline((byte) 2)).div(DSL.inline((byte) 2))).add(DSL.bitAnd(field, DSL.inline((byte) 4)).div(DSL.inline((byte) 4))).add(DSL.bitAnd(field, DSL.inline((byte) 8)).div(DSL.inline((byte) 8))).add(DSL.bitAnd(field, DSL.inline((byte) 16)).div(DSL.inline((byte) 16))).add(DSL.bitAnd(field, DSL.inline((byte) 32)).div(DSL.inline((byte) 32))).add(DSL.bitAnd(field, DSL.inline((byte) 64)).div(DSL.inline((byte) 64))).add(DSL.bitAnd(field, DSL.inline(Byte.MIN_VALUE)).div(DSL.inline(Byte.MIN_VALUE))).cast(Integer.class));
            return;
        }
        if (this.value.getType() == Short.class) {
            Field<? extends Number> field2 = this.value;
            ctx.visit(DSL.bitAnd(field2, DSL.inline((short) 1)).add(DSL.bitAnd(field2, DSL.inline((short) 2)).div(DSL.inline((short) 2))).add(DSL.bitAnd(field2, DSL.inline((short) 4)).div(DSL.inline((short) 4))).add(DSL.bitAnd(field2, DSL.inline((short) 8)).div(DSL.inline((short) 8))).add(DSL.bitAnd(field2, DSL.inline((short) 16)).div(DSL.inline((short) 16))).add(DSL.bitAnd(field2, DSL.inline((short) 32)).div(DSL.inline((short) 32))).add(DSL.bitAnd(field2, DSL.inline((short) 64)).div(DSL.inline((short) 64))).add(DSL.bitAnd(field2, DSL.inline((short) 128)).div(DSL.inline((short) 128))).add(DSL.bitAnd(field2, DSL.inline((short) 256)).div(DSL.inline((short) 256))).add(DSL.bitAnd(field2, DSL.inline((short) 512)).div(DSL.inline((short) 512))).add(DSL.bitAnd(field2, DSL.inline((short) 1024)).div(DSL.inline((short) 1024))).add(DSL.bitAnd(field2, DSL.inline((short) 2048)).div(DSL.inline((short) 2048))).add(DSL.bitAnd(field2, DSL.inline((short) 4096)).div(DSL.inline((short) 4096))).add(DSL.bitAnd(field2, DSL.inline((short) 8192)).div(DSL.inline((short) 8192))).add(DSL.bitAnd(field2, DSL.inline((short) 16384)).div(DSL.inline((short) 16384))).add(DSL.bitAnd(field2, DSL.inline(Short.MIN_VALUE)).div(DSL.inline(Short.MIN_VALUE))).cast(Integer.class));
        } else if (this.value.getType() == Integer.class) {
            Field<? extends Number> field3 = this.value;
            ctx.visit(DSL.bitAnd(field3, DSL.inline(1)).add(DSL.bitAnd(field3, DSL.inline(2)).div(DSL.inline(2))).add(DSL.bitAnd(field3, DSL.inline(4)).div(DSL.inline(4))).add(DSL.bitAnd(field3, DSL.inline(8)).div(DSL.inline(8))).add(DSL.bitAnd(field3, DSL.inline(16)).div(DSL.inline(16))).add(DSL.bitAnd(field3, DSL.inline(32)).div(DSL.inline(32))).add(DSL.bitAnd(field3, DSL.inline(64)).div(DSL.inline(64))).add(DSL.bitAnd(field3, DSL.inline(128)).div(DSL.inline(128))).add(DSL.bitAnd(field3, DSL.inline(256)).div(DSL.inline(256))).add(DSL.bitAnd(field3, DSL.inline(512)).div(DSL.inline(512))).add(DSL.bitAnd(field3, DSL.inline(1024)).div(DSL.inline(1024))).add(DSL.bitAnd(field3, DSL.inline(2048)).div(DSL.inline(2048))).add(DSL.bitAnd(field3, DSL.inline(Opcodes.ACC_SYNTHETIC)).div(DSL.inline(Opcodes.ACC_SYNTHETIC))).add(DSL.bitAnd(field3, DSL.inline(8192)).div(DSL.inline(8192))).add(DSL.bitAnd(field3, DSL.inline(16384)).div(DSL.inline(16384))).add(DSL.bitAnd(field3, DSL.inline(32768)).div(DSL.inline(32768))).add(DSL.bitAnd(field3, DSL.inline(65536)).div(DSL.inline(65536))).add(DSL.bitAnd(field3, DSL.inline(131072)).div(DSL.inline(131072))).add(DSL.bitAnd(field3, DSL.inline(262144)).div(DSL.inline(262144))).add(DSL.bitAnd(field3, DSL.inline(524288)).div(DSL.inline(524288))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_SINGLE_DH_USE)).div(DSL.inline(SSL.SSL_OP_SINGLE_DH_USE))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_EPHEMERAL_RSA)).div(DSL.inline(SSL.SSL_OP_EPHEMERAL_RSA))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_CIPHER_SERVER_PREFERENCE)).div(DSL.inline(SSL.SSL_OP_CIPHER_SERVER_PREFERENCE))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_TLS_ROLLBACK_BUG)).div(DSL.inline(SSL.SSL_OP_TLS_ROLLBACK_BUG))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_SSLv2)).div(DSL.inline(SSL.SSL_OP_NO_SSLv2))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_SSLv3)).div(DSL.inline(SSL.SSL_OP_NO_SSLv3))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1)).div(DSL.inline(SSL.SSL_OP_NO_TLSv1))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1_2)).div(DSL.inline(SSL.SSL_OP_NO_TLSv1_2))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1_1)).div(DSL.inline(SSL.SSL_OP_NO_TLSv1_1))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NETSCAPE_CA_DN_BUG)).div(DSL.inline(SSL.SSL_OP_NETSCAPE_CA_DN_BUG))).add(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG)).div(DSL.inline(SSL.SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG))).add(DSL.bitAnd(field3, DSL.inline(Integer.MIN_VALUE)).div(DSL.inline(Integer.MIN_VALUE))));
        } else if (this.value.getType() == Long.class) {
            Field<? extends Number> field4 = this.value;
            ctx.visit(DSL.bitAnd(field4, DSL.inline(1L)).add(DSL.bitAnd(field4, DSL.inline(2L)).div(DSL.inline(2L))).add(DSL.bitAnd(field4, DSL.inline(4L)).div(DSL.inline(4L))).add(DSL.bitAnd(field4, DSL.inline(8L)).div(DSL.inline(8L))).add(DSL.bitAnd(field4, DSL.inline(16L)).div(DSL.inline(16L))).add(DSL.bitAnd(field4, DSL.inline(32L)).div(DSL.inline(32L))).add(DSL.bitAnd(field4, DSL.inline(64L)).div(DSL.inline(64L))).add(DSL.bitAnd(field4, DSL.inline(128L)).div(DSL.inline(128L))).add(DSL.bitAnd(field4, DSL.inline(256L)).div(DSL.inline(256L))).add(DSL.bitAnd(field4, DSL.inline(512L)).div(DSL.inline(512L))).add(DSL.bitAnd(field4, DSL.inline(FileSize.KB_COEFFICIENT)).div(DSL.inline(FileSize.KB_COEFFICIENT))).add(DSL.bitAnd(field4, DSL.inline(2048L)).div(DSL.inline(2048L))).add(DSL.bitAnd(field4, DSL.inline(4096L)).div(DSL.inline(4096L))).add(DSL.bitAnd(field4, DSL.inline(FileAppender.DEFAULT_BUFFER_SIZE)).div(DSL.inline(FileAppender.DEFAULT_BUFFER_SIZE))).add(DSL.bitAnd(field4, DSL.inline(16384L)).div(DSL.inline(16384L))).add(DSL.bitAnd(field4, DSL.inline(32768L)).div(DSL.inline(32768L))).add(DSL.bitAnd(field4, DSL.inline(65536L)).div(DSL.inline(65536L))).add(DSL.bitAnd(field4, DSL.inline(131072L)).div(DSL.inline(131072L))).add(DSL.bitAnd(field4, DSL.inline(262144L)).div(DSL.inline(262144L))).add(DSL.bitAnd(field4, DSL.inline(524288L)).div(DSL.inline(524288L))).add(DSL.bitAnd(field4, DSL.inline(FileSize.MB_COEFFICIENT)).div(DSL.inline(FileSize.MB_COEFFICIENT))).add(DSL.bitAnd(field4, DSL.inline(2097152L)).div(DSL.inline(2097152L))).add(DSL.bitAnd(field4, DSL.inline(4194304L)).div(DSL.inline(4194304L))).add(DSL.bitAnd(field4, DSL.inline(8388608L)).div(DSL.inline(8388608L))).add(DSL.bitAnd(field4, DSL.inline(16777216L)).div(DSL.inline(16777216L))).add(DSL.bitAnd(field4, DSL.inline(33554432L)).div(DSL.inline(33554432L))).add(DSL.bitAnd(field4, DSL.inline(67108864L)).div(DSL.inline(67108864L))).add(DSL.bitAnd(field4, DSL.inline(134217728L)).div(DSL.inline(134217728L))).add(DSL.bitAnd(field4, DSL.inline(268435456L)).div(DSL.inline(268435456L))).add(DSL.bitAnd(field4, DSL.inline(536870912L)).div(DSL.inline(536870912L))).add(DSL.bitAnd(field4, DSL.inline(FileSize.GB_COEFFICIENT)).div(DSL.inline(FileSize.GB_COEFFICIENT))).add(DSL.bitAnd(field4, DSL.inline(2147483648L)).div(DSL.inline(2147483648L))).add(DSL.bitAnd(field4, DSL.inline(4294967296L)).div(DSL.inline(4294967296L))).add(DSL.bitAnd(field4, DSL.inline(8589934592L)).div(DSL.inline(8589934592L))).add(DSL.bitAnd(field4, DSL.inline(17179869184L)).div(DSL.inline(17179869184L))).add(DSL.bitAnd(field4, DSL.inline(34359738368L)).div(DSL.inline(34359738368L))).add(DSL.bitAnd(field4, DSL.inline(68719476736L)).div(DSL.inline(68719476736L))).add(DSL.bitAnd(field4, DSL.inline(137438953472L)).div(DSL.inline(137438953472L))).add(DSL.bitAnd(field4, DSL.inline(274877906944L)).div(DSL.inline(274877906944L))).add(DSL.bitAnd(field4, DSL.inline(549755813888L)).div(DSL.inline(549755813888L))).add(DSL.bitAnd(field4, DSL.inline(1099511627776L)).div(DSL.inline(1099511627776L))).add(DSL.bitAnd(field4, DSL.inline(2199023255552L)).div(DSL.inline(2199023255552L))).add(DSL.bitAnd(field4, DSL.inline(4398046511104L)).div(DSL.inline(4398046511104L))).add(DSL.bitAnd(field4, DSL.inline(8796093022208L)).div(DSL.inline(8796093022208L))).add(DSL.bitAnd(field4, DSL.inline(17592186044416L)).div(DSL.inline(17592186044416L))).add(DSL.bitAnd(field4, DSL.inline(35184372088832L)).div(DSL.inline(35184372088832L))).add(DSL.bitAnd(field4, DSL.inline(70368744177664L)).div(DSL.inline(70368744177664L))).add(DSL.bitAnd(field4, DSL.inline(140737488355328L)).div(DSL.inline(140737488355328L))).add(DSL.bitAnd(field4, DSL.inline(281474976710656L)).div(DSL.inline(281474976710656L))).add(DSL.bitAnd(field4, DSL.inline(562949953421312L)).div(DSL.inline(562949953421312L))).add(DSL.bitAnd(field4, DSL.inline(1125899906842624L)).div(DSL.inline(1125899906842624L))).add(DSL.bitAnd(field4, DSL.inline(2251799813685248L)).div(DSL.inline(2251799813685248L))).add(DSL.bitAnd(field4, DSL.inline(4503599627370496L)).div(DSL.inline(4503599627370496L))).add(DSL.bitAnd(field4, DSL.inline(9007199254740992L)).div(DSL.inline(9007199254740992L))).add(DSL.bitAnd(field4, DSL.inline(18014398509481984L)).div(DSL.inline(18014398509481984L))).add(DSL.bitAnd(field4, DSL.inline(36028797018963968L)).div(DSL.inline(36028797018963968L))).add(DSL.bitAnd(field4, DSL.inline(72057594037927936L)).div(DSL.inline(72057594037927936L))).add(DSL.bitAnd(field4, DSL.inline(144115188075855872L)).div(DSL.inline(144115188075855872L))).add(DSL.bitAnd(field4, DSL.inline(288230376151711744L)).div(DSL.inline(288230376151711744L))).add(DSL.bitAnd(field4, DSL.inline(576460752303423488L)).div(DSL.inline(576460752303423488L))).add(DSL.bitAnd(field4, DSL.inline(1152921504606846976L)).div(DSL.inline(1152921504606846976L))).add(DSL.bitAnd(field4, DSL.inline(2305843009213693952L)).div(DSL.inline(2305843009213693952L))).add(DSL.bitAnd(field4, DSL.inline(4611686018427387904L)).div(DSL.inline(4611686018427387904L))).add(DSL.bitAnd(field4, DSL.inline(Long.MIN_VALUE)).div(DSL.inline(0L))).cast(Integer.class));
        } else {
            ctx.visit(Names.N_BIT_COUNT).sql('(').visit((Field<?>) this.value).sql(')');
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void bitAndShrEmulation(Context<?> context) {
        if (this.value.getType() == Byte.class) {
            Field<? extends Number> field = this.value;
            byte i = (byte) (0 + 1);
            byte i2 = (byte) (i + 1);
            byte i3 = (byte) (i2 + 1);
            byte i4 = (byte) (i3 + 1);
            byte i5 = (byte) (i4 + 1);
            byte i6 = (byte) (i5 + 1);
            context.visit((Field<?>) DSL.bitAnd(field, DSL.inline((byte) 1)).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 2)), DSL.inline(i))).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 4)), DSL.inline(i2))).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 8)), DSL.inline(i3))).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 16)), DSL.inline(i4))).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 32)), DSL.inline(i5))).add(DSL.shr(DSL.bitAnd(field, DSL.inline((byte) 64)), DSL.inline(i6))).add(DSL.shr(DSL.bitAnd(field, DSL.inline(Byte.MIN_VALUE)), DSL.inline((byte) (i6 + 1)))).cast(Integer.class));
            return;
        }
        if (this.value.getType() == Short.class) {
            Field<? extends Number> field2 = this.value;
            short i7 = (short) (0 + 1);
            short i8 = (short) (i7 + 1);
            short i9 = (short) (i8 + 1);
            short i10 = (short) (i9 + 1);
            short i11 = (short) (i10 + 1);
            short i12 = (short) (i11 + 1);
            short i13 = (short) (i12 + 1);
            short i14 = (short) (i13 + 1);
            short i15 = (short) (i14 + 1);
            short i16 = (short) (i15 + 1);
            short i17 = (short) (i16 + 1);
            short i18 = (short) (i17 + 1);
            short i19 = (short) (i18 + 1);
            short i20 = (short) (i19 + 1);
            context.visit((Field<?>) DSL.bitAnd(field2, DSL.inline((short) 1)).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 2)), DSL.inline(i7))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 4)), DSL.inline(i8))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 8)), DSL.inline(i9))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 16)), DSL.inline(i10))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 32)), DSL.inline(i11))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 64)), DSL.inline(i12))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 128)), DSL.inline(i13))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 256)), DSL.inline(i14))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 512)), DSL.inline(i15))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 1024)), DSL.inline(i16))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 2048)), DSL.inline(i17))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 4096)), DSL.inline(i18))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 8192)), DSL.inline(i19))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline((short) 16384)), DSL.inline(i20))).add(DSL.shr(DSL.bitAnd(field2, DSL.inline(Short.MIN_VALUE)), DSL.inline((short) (i20 + 1)))).cast(Integer.class));
            return;
        }
        if (this.value.getType() == Integer.class) {
            Field<? extends Number> field3 = this.value;
            int i21 = 0 + 1;
            Field add = DSL.bitAnd(field3, DSL.inline(1)).add(DSL.shr(DSL.bitAnd(field3, DSL.inline(2)), DSL.inline(i21)));
            int i22 = i21 + 1;
            Field add2 = add.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(4)), DSL.inline(i22)));
            int i23 = i22 + 1;
            Field add3 = add2.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(8)), DSL.inline(i23)));
            int i24 = i23 + 1;
            Field add4 = add3.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(16)), DSL.inline(i24)));
            int i25 = i24 + 1;
            Field add5 = add4.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(32)), DSL.inline(i25)));
            int i26 = i25 + 1;
            Field add6 = add5.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(64)), DSL.inline(i26)));
            int i27 = i26 + 1;
            Field add7 = add6.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(128)), DSL.inline(i27)));
            int i28 = i27 + 1;
            Field add8 = add7.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(256)), DSL.inline(i28)));
            int i29 = i28 + 1;
            Field add9 = add8.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(512)), DSL.inline(i29)));
            int i30 = i29 + 1;
            Field add10 = add9.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(1024)), DSL.inline(i30)));
            int i31 = i30 + 1;
            Field add11 = add10.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(2048)), DSL.inline(i31)));
            int i32 = i31 + 1;
            Field add12 = add11.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(Opcodes.ACC_SYNTHETIC)), DSL.inline(i32)));
            int i33 = i32 + 1;
            Field add13 = add12.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(8192)), DSL.inline(i33)));
            int i34 = i33 + 1;
            Field add14 = add13.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(16384)), DSL.inline(i34)));
            int i35 = i34 + 1;
            Field add15 = add14.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(32768)), DSL.inline(i35)));
            int i36 = i35 + 1;
            Field add16 = add15.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(65536)), DSL.inline(i36)));
            int i37 = i36 + 1;
            Field add17 = add16.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(131072)), DSL.inline(i37)));
            int i38 = i37 + 1;
            Field add18 = add17.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(262144)), DSL.inline(i38)));
            int i39 = i38 + 1;
            Field add19 = add18.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(524288)), DSL.inline(i39)));
            int i40 = i39 + 1;
            Field add20 = add19.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_SINGLE_DH_USE)), DSL.inline(i40)));
            int i41 = i40 + 1;
            Field add21 = add20.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_EPHEMERAL_RSA)), DSL.inline(i41)));
            int i42 = i41 + 1;
            Field add22 = add21.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_CIPHER_SERVER_PREFERENCE)), DSL.inline(i42)));
            int i43 = i42 + 1;
            Field add23 = add22.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_TLS_ROLLBACK_BUG)), DSL.inline(i43)));
            int i44 = i43 + 1;
            Field add24 = add23.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_SSLv2)), DSL.inline(i44)));
            int i45 = i44 + 1;
            Field add25 = add24.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_SSLv3)), DSL.inline(i45)));
            int i46 = i45 + 1;
            Field add26 = add25.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1)), DSL.inline(i46)));
            int i47 = i46 + 1;
            Field add27 = add26.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1_2)), DSL.inline(i47)));
            int i48 = i47 + 1;
            Field add28 = add27.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NO_TLSv1_1)), DSL.inline(i48)));
            int i49 = i48 + 1;
            Field add29 = add28.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NETSCAPE_CA_DN_BUG)), DSL.inline(i49)));
            int i50 = i49 + 1;
            context.visit((Field<?>) add29.add(DSL.shr(DSL.bitAnd(field3, DSL.inline(SSL.SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG)), DSL.inline(i50))).add(DSL.shr(DSL.bitAnd(field3, DSL.inline(Integer.MIN_VALUE)), DSL.inline(i50 + 1))));
            return;
        }
        if (this.value.getType() == Long.class) {
            Field<? extends Number> field4 = this.value;
            context.visit((Field<?>) DSL.bitAnd(field4, DSL.inline(1L)).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2L)), DSL.inline(0 + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(8L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(16L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(32L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(64L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(128L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(256L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(512L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(FileSize.KB_COEFFICIENT)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2048L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4096L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(FileAppender.DEFAULT_BUFFER_SIZE)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(16384L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(32768L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(65536L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(131072L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(262144L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(524288L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(FileSize.MB_COEFFICIENT)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2097152L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4194304L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(8388608L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(16777216L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(33554432L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(67108864L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(134217728L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(268435456L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(536870912L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(FileSize.GB_COEFFICIENT)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2147483648L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4294967296L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(8589934592L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(17179869184L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(34359738368L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(68719476736L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(137438953472L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(274877906944L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(549755813888L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(1099511627776L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2199023255552L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4398046511104L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(8796093022208L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(17592186044416L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(35184372088832L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(70368744177664L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(140737488355328L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(281474976710656L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(562949953421312L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(1125899906842624L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2251799813685248L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4503599627370496L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(9007199254740992L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(18014398509481984L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(36028797018963968L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(72057594037927936L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(144115188075855872L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(288230376151711744L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(576460752303423488L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(1152921504606846976L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(2305843009213693952L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(4611686018427387904L)), DSL.inline(context + 1))).add(DSL.shr(DSL.bitAnd(field4, DSL.inline(Long.MIN_VALUE)), DSL.inline(context + 1))).cast(Integer.class));
        } else {
            context.visit(Names.N_BIT_COUNT).sql('(').visit((Field<?>) this.value).sql(')');
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.BitCount $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.BitCount> $constructor() {
        return a1 -> {
            return new BitCount(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BitCount) {
            QOM.BitCount o = (QOM.BitCount) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}
