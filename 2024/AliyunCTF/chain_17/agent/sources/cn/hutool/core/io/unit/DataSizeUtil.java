package cn.hutool.core.io.unit;

import cn.hutool.core.text.CharSequenceUtil;
import java.text.DecimalFormat;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/unit/DataSizeUtil.class */
public class DataSizeUtil {
    public static long parse(String text) {
        return DataSize.parse(text).toBytes();
    }

    public static String format(long size) {
        if (size <= 0) {
            return CustomBooleanEditor.VALUE_0;
        }
        int digitGroups = Math.min(DataUnit.UNIT_NAMES.length - 1, (int) (Math.log10(size) / Math.log10(1024.0d)));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024.0d, digitGroups)) + CharSequenceUtil.SPACE + DataUnit.UNIT_NAMES[digitGroups];
    }
}
