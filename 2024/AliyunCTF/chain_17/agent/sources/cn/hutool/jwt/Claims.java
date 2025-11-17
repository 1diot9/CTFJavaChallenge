package cn.hutool.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/Claims.class */
public class Claims implements Serializable {
    private static final long serialVersionUID = 1;
    private final JSONConfig CONFIG = JSONConfig.create().setDateFormat(GlobalCustomFormat.FORMAT_SECONDS);
    private JSONObject claimJSON;

    /* JADX INFO: Access modifiers changed from: protected */
    public void setClaim(String name, Object value) {
        init();
        Assert.notNull(name, "Name must be not null!", new Object[0]);
        if (value == null) {
            this.claimJSON.remove(name);
        } else {
            this.claimJSON.set(name, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void putAll(Map<String, ?> headerClaims) {
        if (MapUtil.isNotEmpty(headerClaims)) {
            for (Map.Entry<String, ?> entry : headerClaims.entrySet()) {
                setClaim(entry.getKey(), entry.getValue());
            }
        }
    }

    public Object getClaim(String name) {
        init();
        return this.claimJSON.getObj(name);
    }

    public JSONObject getClaimsJson() {
        init();
        return this.claimJSON;
    }

    public void parse(String tokenPart, Charset charset) {
        this.claimJSON = JSONUtil.parseObj(Base64.decodeStr(tokenPart, charset), this.CONFIG);
    }

    public String toString() {
        init();
        return this.claimJSON.toString();
    }

    private void init() {
        if (null == this.claimJSON) {
            this.claimJSON = new JSONObject(this.CONFIG);
        }
    }
}
