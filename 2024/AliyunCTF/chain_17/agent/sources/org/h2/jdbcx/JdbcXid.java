package org.h2.jdbcx;

import java.util.Base64;
import javax.transaction.xa.Xid;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.message.TraceObject;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcXid.class */
public final class JdbcXid extends TraceObject implements Xid {
    private static final String PREFIX = "XID";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private final int formatId;
    private final byte[] branchQualifier;
    private final byte[] globalTransactionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcXid(JdbcDataSourceFactory jdbcDataSourceFactory, int i, String str) {
        setTrace(jdbcDataSourceFactory.getTrace(), 15, i);
        try {
            String[] split = str.split("\\|");
            if (split.length == 4 && PREFIX.equals(split[0])) {
                this.formatId = Integer.parseInt(split[1]);
                Base64.Decoder urlDecoder = Base64.getUrlDecoder();
                this.branchQualifier = urlDecoder.decode(split[2]);
                this.globalTransactionId = urlDecoder.decode(split[3]);
                return;
            }
        } catch (IllegalArgumentException e) {
        }
        throw DbException.get(ErrorCode.WRONG_XID_FORMAT_1, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder toString(StringBuilder sb, Xid xid) {
        return sb.append(PREFIX).append('|').append(xid.getFormatId()).append('|').append(ENCODER.encodeToString(xid.getBranchQualifier())).append('|').append(ENCODER.encodeToString(xid.getGlobalTransactionId()));
    }

    public int getFormatId() {
        debugCodeCall("getFormatId");
        return this.formatId;
    }

    public byte[] getBranchQualifier() {
        debugCodeCall("getBranchQualifier");
        return this.branchQualifier;
    }

    public byte[] getGlobalTransactionId() {
        debugCodeCall("getGlobalTransactionId");
        return this.globalTransactionId;
    }
}
