package cn.hutool.core.net;

import cn.hutool.core.codec.PercentCodec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/RFC3986.class */
public class RFC3986 {
    public static final PercentCodec GEN_DELIMS = PercentCodec.of(":/?#[]@");
    public static final PercentCodec SUB_DELIMS = PercentCodec.of("!$&'()*+,;=");
    public static final PercentCodec RESERVED = GEN_DELIMS.orNew(SUB_DELIMS);
    public static final PercentCodec UNRESERVED = PercentCodec.of(unreservedChars());
    public static final PercentCodec PCHAR = UNRESERVED.orNew(SUB_DELIMS).or(PercentCodec.of(":@"));
    public static final PercentCodec SEGMENT = PCHAR;
    public static final PercentCodec SEGMENT_NZ_NC = PercentCodec.of(SEGMENT).removeSafe(':');
    public static final PercentCodec PATH = SEGMENT.orNew(PercentCodec.of("/"));
    public static final PercentCodec QUERY = PCHAR.orNew(PercentCodec.of("/?"));
    public static final PercentCodec FRAGMENT = QUERY;
    public static final PercentCodec QUERY_PARAM_VALUE = PercentCodec.of(QUERY).removeSafe('&');
    public static final PercentCodec QUERY_PARAM_NAME = PercentCodec.of(QUERY_PARAM_VALUE).removeSafe('=');

    private static StringBuilder unreservedChars() {
        StringBuilder sb = new StringBuilder();
        char c = 'A';
        while (true) {
            char c2 = c;
            if (c2 > 'Z') {
                break;
            }
            sb.append(c2);
            c = (char) (c2 + 1);
        }
        char c3 = 'a';
        while (true) {
            char c4 = c3;
            if (c4 > 'z') {
                break;
            }
            sb.append(c4);
            c3 = (char) (c4 + 1);
        }
        char c5 = '0';
        while (true) {
            char c6 = c5;
            if (c6 <= '9') {
                sb.append(c6);
                c5 = (char) (c6 + 1);
            } else {
                sb.append("_.-~");
                return sb;
            }
        }
    }
}
