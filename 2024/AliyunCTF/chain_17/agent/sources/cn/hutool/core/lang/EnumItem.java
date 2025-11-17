package cn.hutool.core.lang;

import cn.hutool.core.lang.EnumItem;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/EnumItem.class */
public interface EnumItem<E extends EnumItem<E>> extends Serializable {
    String name();

    int intVal();

    default String text() {
        return name();
    }

    default E[] items() {
        return (E[]) ((EnumItem[]) getClass().getEnumConstants());
    }

    default E fromInt(Integer intVal) {
        if (intVal == null) {
            return null;
        }
        E[] vs = items();
        for (E enumItem : vs) {
            if (enumItem.intVal() == intVal.intValue()) {
                return enumItem;
            }
        }
        return null;
    }

    default E fromStr(String strVal) {
        if (strVal == null) {
            return null;
        }
        E[] vs = items();
        for (E enumItem : vs) {
            if (strVal.equalsIgnoreCase(enumItem.name())) {
                return enumItem;
            }
        }
        return null;
    }
}
