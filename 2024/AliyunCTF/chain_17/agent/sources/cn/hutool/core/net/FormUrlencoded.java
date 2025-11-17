package cn.hutool.core.net;

import cn.hutool.core.codec.PercentCodec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/FormUrlencoded.class */
public class FormUrlencoded {
    public static final PercentCodec ALL = PercentCodec.of(RFC3986.UNRESERVED).removeSafe('~').addSafe('*').setEncodeSpaceAsPlus(true);
}
