package cn.hutool.json;

import cn.hutool.core.bean.BeanUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONSupport.class */
public class JSONSupport implements JSONString, JSONBeanParser<JSON> {
    public void parse(String jsonString) {
        parse((JSON) new JSONObject(jsonString));
    }

    @Override // cn.hutool.json.JSONBeanParser
    public void parse(JSON json) {
        JSONSupport support = (JSONSupport) JSONConverter.jsonToBean(getClass(), json, false);
        BeanUtil.copyProperties(support, this, new String[0]);
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }

    @Override // cn.hutool.json.JSONString
    public String toJSONString() {
        return toJSON().toString();
    }

    public String toPrettyString() {
        return toJSON().toStringPretty();
    }

    public String toString() {
        return toJSONString();
    }
}
