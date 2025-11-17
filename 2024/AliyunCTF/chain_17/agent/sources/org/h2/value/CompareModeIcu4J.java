package org.h2.value;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Locale;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CompareModeIcu4J.class */
public class CompareModeIcu4J extends CompareMode {
    private final Comparator<String> collator;
    private volatile CompareModeIcu4J caseInsensitive;

    /* JADX INFO: Access modifiers changed from: protected */
    public CompareModeIcu4J(String str, int i) {
        super(str, i);
        this.collator = getIcu4jCollator(str, i);
    }

    @Override // org.h2.value.CompareMode
    public int compareString(String str, String str2, boolean z) {
        if (z && getStrength() > 1) {
            CompareModeIcu4J compareModeIcu4J = this.caseInsensitive;
            if (compareModeIcu4J == null) {
                CompareModeIcu4J compareModeIcu4J2 = new CompareModeIcu4J(getName(), 1);
                compareModeIcu4J = compareModeIcu4J2;
                this.caseInsensitive = compareModeIcu4J2;
            }
            return compareModeIcu4J.compareString(str, str2, false);
        }
        return this.collator.compare(str, str2);
    }

    @Override // org.h2.value.CompareMode
    public boolean equalsChars(String str, int i, String str2, int i2, boolean z) {
        return compareString(str.substring(i, i + 1), str2.substring(i2, i2 + 1), z) == 0;
    }

    private static Comparator<String> getIcu4jCollator(String str, int i) {
        int indexOf;
        try {
            Comparator<String> comparator = null;
            Class loadUserClass = JdbcUtils.loadUserClass("com.ibm.icu.text.Collator");
            Method method = loadUserClass.getMethod("getInstance", Locale.class);
            int length = str.length();
            if (length == 2) {
                Locale locale = new Locale(StringUtils.toLowerEnglish(str), "");
                if (compareLocaleNames(locale, str)) {
                    comparator = (Comparator) method.invoke(null, locale);
                }
            } else if (length == 5 && (indexOf = str.indexOf(95)) >= 0) {
                Locale locale2 = new Locale(StringUtils.toLowerEnglish(str.substring(0, indexOf)), str.substring(indexOf + 1));
                if (compareLocaleNames(locale2, str)) {
                    comparator = (Comparator) method.invoke(null, locale2);
                }
            }
            if (comparator == null) {
                Locale[] localeArr = (Locale[]) loadUserClass.getMethod("getAvailableLocales", new Class[0]).invoke(null, new Object[0]);
                int length2 = localeArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length2) {
                        break;
                    }
                    Locale locale3 = localeArr[i2];
                    if (!compareLocaleNames(locale3, str)) {
                        i2++;
                    } else {
                        comparator = (Comparator) method.invoke(null, locale3);
                        break;
                    }
                }
            }
            if (comparator == null) {
                throw DbException.getInvalidValueException("collator", str);
            }
            loadUserClass.getMethod("setStrength", Integer.TYPE).invoke(comparator, Integer.valueOf(i));
            return comparator;
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }
}
