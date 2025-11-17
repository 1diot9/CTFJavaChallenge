package org.h2.engine;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/SettingsBase.class */
public class SettingsBase {
    private final HashMap<String, String> settings;

    /* JADX INFO: Access modifiers changed from: protected */
    public SettingsBase(HashMap<String, String> hashMap) {
        this.settings = hashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean get(String str, boolean z) {
        String str2 = get(str, Boolean.toString(z));
        try {
            return Utils.parseBoolean(str2, z, true);
        } catch (IllegalArgumentException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, "key:" + str + " value:" + str2);
        }
    }

    void set(String str, boolean z) {
        this.settings.put(str, Boolean.toString(z));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int get(String str, int i) {
        String str2 = get(str, Integer.toString(i));
        try {
            return Integer.decode(str2).intValue();
        } catch (NumberFormatException e) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, e, "key:" + str + " value:" + str2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String get(String str, String str2) {
        boolean z;
        String str3 = this.settings.get(str);
        if (str3 != null) {
            return str3;
        }
        StringBuilder sb = new StringBuilder("h2.");
        boolean z2 = false;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == '_') {
                z = true;
            } else {
                sb.append(z2 ? Character.toUpperCase(charAt) : Character.toLowerCase(charAt));
                z = false;
            }
            z2 = z;
        }
        String property = Utils.getProperty(sb.toString(), str2);
        this.settings.put(str, property);
        return property;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean containsKey(String str) {
        return this.settings.containsKey(str);
    }

    public HashMap<String, String> getSettings() {
        return this.settings;
    }

    public Map.Entry<String, String>[] getSortedSettings() {
        Map.Entry<String, String>[] entryArr = (Map.Entry[]) this.settings.entrySet().toArray(new Map.Entry[0]);
        Arrays.sort(entryArr, Comparator.comparing((v0) -> {
            return v0.getKey();
        }));
        return entryArr;
    }
}
