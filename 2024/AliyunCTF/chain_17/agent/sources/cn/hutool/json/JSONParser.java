package cn.hutool.json;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.Mutable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONParser.class */
public class JSONParser {
    private final JSONTokener tokener;

    public static JSONParser of(JSONTokener tokener) {
        return new JSONParser(tokener);
    }

    public JSONParser(JSONTokener tokener) {
        this.tokener = tokener;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0023. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0083  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007c A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void parseTo(cn.hutool.json.JSONObject r7, cn.hutool.core.lang.Filter<cn.hutool.core.lang.mutable.MutablePair<java.lang.String, java.lang.Object>> r8) {
        /*
            r6 = this;
            r0 = r6
            cn.hutool.json.JSONTokener r0 = r0.tokener
            r9 = r0
            r0 = r9
            char r0 = r0.nextClean()
            r1 = 123(0x7b, float:1.72E-43)
            if (r0 == r1) goto L15
            r0 = r9
            java.lang.String r1 = "A JSONObject text must begin with '{'"
            cn.hutool.json.JSONException r0 = r0.syntaxError(r1)
            throw r0
        L15:
            r0 = r9
            char r0 = r0.getPrevious()
            r10 = r0
            r0 = r9
            char r0 = r0.nextClean()
            r11 = r0
            r0 = r11
            switch(r0) {
                case 0: goto L4c;
                case 91: goto L54;
                case 123: goto L54;
                case 125: goto L53;
                default: goto L62;
            }
        L4c:
            r0 = r9
            java.lang.String r1 = "A JSONObject text must end with '}'"
            cn.hutool.json.JSONException r0 = r0.syntaxError(r1)
            throw r0
        L53:
            return
        L54:
            r0 = r10
            r1 = 123(0x7b, float:1.72E-43)
            if (r0 != r1) goto L62
            r0 = r9
            java.lang.String r1 = "A JSONObject can not directly nest another JSONObject or JSONArray."
            cn.hutool.json.JSONException r0 = r0.syntaxError(r1)
            throw r0
        L62:
            r0 = r9
            r0.back()
            r0 = r9
            java.lang.Object r0 = r0.nextValue()
            java.lang.String r0 = r0.toString()
            r12 = r0
            r0 = r9
            char r0 = r0.nextClean()
            r11 = r0
            r0 = r11
            r1 = 58
            if (r0 == r1) goto L83
            r0 = r9
            java.lang.String r1 = "Expected a ':' after a key"
            cn.hutool.json.JSONException r0 = r0.syntaxError(r1)
            throw r0
        L83:
            r0 = r7
            r1 = r12
            r2 = r9
            java.lang.Object r2 = r2.nextValue()
            r3 = r8
            r4 = r7
            cn.hutool.json.JSONConfig r4 = r4.getConfig()
            boolean r4 = r4.isCheckDuplicate()
            cn.hutool.json.JSONObject r0 = r0.set(r1, r2, r3, r4)
            r0 = r9
            char r0 = r0.nextClean()
            switch(r0) {
                case 44: goto Lbc;
                case 59: goto Lbc;
                case 125: goto Lcd;
                default: goto Lce;
            }
        Lbc:
            r0 = r9
            char r0 = r0.nextClean()
            r1 = 125(0x7d, float:1.75E-43)
            if (r0 != r1) goto Lc6
            return
        Lc6:
            r0 = r9
            r0.back()
            goto L15
        Lcd:
            return
        Lce:
            r0 = r9
            java.lang.String r1 = "Expected a ',' or '}'"
            cn.hutool.json.JSONException r0 = r0.syntaxError(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.json.JSONParser.parseTo(cn.hutool.json.JSONObject, cn.hutool.core.lang.Filter):void");
    }

    public void parseTo(JSONArray jsonArray, Filter<Mutable<Object>> filter) {
        JSONTokener x = this.tokener;
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() != ']') {
            x.back();
            while (true) {
                if (x.nextClean() == ',') {
                    x.back();
                    jsonArray.addRaw(JSONNull.NULL, filter);
                } else {
                    x.back();
                    jsonArray.addRaw(x.nextValue(), filter);
                }
                switch (x.nextClean()) {
                    case ',':
                        if (x.nextClean() == ']') {
                            return;
                        } else {
                            x.back();
                        }
                    case ']':
                        return;
                    default:
                        throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }
}
