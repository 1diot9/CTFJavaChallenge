package ch.qos.logback.core.model.util;

import ch.qos.logback.core.model.Model;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/util/TagUtil.class */
public class TagUtil {
    public static String unifiedTag(Model aModel) {
        String tag = aModel.getTag();
        char first = tag.charAt(0);
        if (Character.isUpperCase(first)) {
            char lower = Character.toLowerCase(first);
            return lower + tag.substring(1);
        }
        return tag;
    }
}
