package ch.qos.logback.core.joran.util;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/util/ParentTag_Tag_Class_Tuple.class */
public class ParentTag_Tag_Class_Tuple {
    public final String parentTag;
    public final String tag;
    public final String className;

    public ParentTag_Tag_Class_Tuple(String parentTag, String tag, String className) {
        this.parentTag = parentTag;
        this.tag = tag;
        this.className = className;
    }

    public String toString() {
        return "ParentTag_Tag_Class_Tuple [parentTag=" + this.parentTag + ", tag=" + this.tag + ", className=" + this.className + "]";
    }
}
