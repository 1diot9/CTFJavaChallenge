package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/support/DefaultClientCodecConfigurer.class */
public class DefaultClientCodecConfigurer extends BaseCodecConfigurer implements ClientCodecConfigurer {
    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getWriters() {
        return super.getWriters();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getReaders() {
        return super.getReaders();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ CodecConfigurer.CustomCodecs customCodecs() {
        return super.customCodecs();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ void registerDefaults(boolean shouldRegister) {
        super.registerDefaults(shouldRegister);
    }

    public DefaultClientCodecConfigurer() {
        super(new ClientDefaultCodecsImpl());
    }

    private DefaultClientCodecConfigurer(DefaultClientCodecConfigurer other) {
        super(other);
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
        return (ClientCodecConfigurer.ClientDefaultCodecs) super.defaultCodecs();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    /* renamed from: clone */
    public DefaultClientCodecConfigurer mo2521clone() {
        return new DefaultClientCodecConfigurer(this);
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer
    protected BaseDefaultCodecs cloneDefaultCodecs() {
        return new ClientDefaultCodecsImpl((ClientDefaultCodecsImpl) defaultCodecs());
    }
}
