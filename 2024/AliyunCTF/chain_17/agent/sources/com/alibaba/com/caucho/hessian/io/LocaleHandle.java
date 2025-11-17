package com.alibaba.com.caucho.hessian.io;

import java.io.Serializable;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/LocaleHandle.class */
public class LocaleHandle implements Serializable, HessianHandle {
    private String value;

    public LocaleHandle(String locale) {
        this.value = locale;
    }

    private Object readResolve() {
        if (this.value == null) {
            return null;
        }
        if (this.value.length() == 0) {
            return new Locale("");
        }
        int extStart = this.value.indexOf("_#");
        if (extStart != -1) {
            this.value = this.value.substring(0, extStart);
        }
        String language = this.value;
        String country = "";
        String variant = "";
        int pos1 = this.value.indexOf(95);
        if (pos1 != -1) {
            int pos12 = pos1 + 1;
            language = this.value.substring(0, pos1);
            int pos2 = this.value.indexOf(95, pos12);
            if (pos2 == -1) {
                country = this.value.substring(pos12);
            } else {
                country = this.value.substring(pos12, pos2);
                variant = this.value.substring(pos2 + 1);
            }
        }
        return new Locale(language, country, variant);
    }
}
