package cn.hutool.system;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import java.io.Serializable;
import org.apache.tomcat.websocket.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/JavaInfo.class */
public class JavaInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private final String JAVA_VERSION = SystemUtil.get("java.version", false);
    private final float JAVA_VERSION_FLOAT = getJavaVersionAsFloat();
    private final int JAVA_VERSION_INT = getJavaVersionAsInt();
    private final String JAVA_VENDOR = SystemUtil.get("java.vendor", false);
    private final String JAVA_VENDOR_URL = SystemUtil.get("java.vendor.url", false);
    private final boolean IS_JAVA_1_8 = getJavaVersionMatches("1.8");
    private final boolean IS_JAVA_9 = getJavaVersionMatches("9");
    private final boolean IS_JAVA_10 = getJavaVersionMatches("10");
    private final boolean IS_JAVA_11 = getJavaVersionMatches("11");
    private final boolean IS_JAVA_12 = getJavaVersionMatches("12");
    private final boolean IS_JAVA_13 = getJavaVersionMatches(Constants.WS_VERSION_HEADER_VALUE);
    private final boolean IS_JAVA_14 = getJavaVersionMatches("14");
    private final boolean IS_JAVA_15 = getJavaVersionMatches("15");
    private final boolean IS_JAVA_16 = getJavaVersionMatches("16");
    private final boolean IS_JAVA_17 = getJavaVersionMatches("17");
    private final boolean IS_JAVA_18 = getJavaVersionMatches("18");

    public final String getVersion() {
        return this.JAVA_VERSION;
    }

    public final float getVersionFloat() {
        return this.JAVA_VERSION_FLOAT;
    }

    public final int getVersionInt() {
        return this.JAVA_VERSION_INT;
    }

    private float getJavaVersionAsFloat() {
        if (this.JAVA_VERSION == null) {
            return 0.0f;
        }
        String str = this.JAVA_VERSION;
        return Float.parseFloat(ReUtil.get("^[0-9]{1,2}(\\.[0-9]{1,2})?", str, 0));
    }

    private int getJavaVersionAsInt() {
        if (this.JAVA_VERSION == null) {
            return 0;
        }
        String javaVersion = ReUtil.get("^[0-9]{1,2}(\\.[0-9]{1,2}){0,2}", this.JAVA_VERSION, 0);
        String[] split = javaVersion.split("\\.");
        String result = ArrayUtil.join((Object[]) split, (CharSequence) "");
        if (split[0].length() > 1) {
            result = (result + "0000").substring(0, 4);
        }
        return Integer.parseInt(result);
    }

    public final String getVendor() {
        return this.JAVA_VENDOR;
    }

    public final String getVendorURL() {
        return this.JAVA_VENDOR_URL;
    }

    @Deprecated
    public final boolean isJava1_1() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_2() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_3() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_4() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_5() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_6() {
        return false;
    }

    @Deprecated
    public final boolean isJava1_7() {
        return false;
    }

    public final boolean isJava1_8() {
        return this.IS_JAVA_1_8;
    }

    public final boolean isJava9() {
        return this.IS_JAVA_9;
    }

    public final boolean isJava10() {
        return this.IS_JAVA_10;
    }

    public final boolean isJava11() {
        return this.IS_JAVA_11;
    }

    public final boolean isJava12() {
        return this.IS_JAVA_12;
    }

    public final boolean isJava13() {
        return this.IS_JAVA_13;
    }

    public final boolean isJava14() {
        return this.IS_JAVA_14;
    }

    public final boolean isJava15() {
        return this.IS_JAVA_15;
    }

    public final boolean isJava16() {
        return this.IS_JAVA_16;
    }

    public final boolean isJava17() {
        return this.IS_JAVA_17;
    }

    public final boolean isJava18() {
        return this.IS_JAVA_18;
    }

    private boolean getJavaVersionMatches(String versionPrefix) {
        if (this.JAVA_VERSION == null) {
            return false;
        }
        return this.JAVA_VERSION.startsWith(versionPrefix);
    }

    public final boolean isJavaVersionAtLeast(float requiredVersion) {
        return getVersionFloat() >= requiredVersion;
    }

    public final boolean isJavaVersionAtLeast(int requiredVersion) {
        return getVersionInt() >= requiredVersion;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        SystemUtil.append(builder, "Java Version:    ", getVersion());
        SystemUtil.append(builder, "Java Vendor:     ", getVendor());
        SystemUtil.append(builder, "Java Vendor URL: ", getVendorURL());
        return builder.toString();
    }
}
