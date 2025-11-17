package org.h2.value;

import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/CompareMode.class */
public class CompareMode implements Comparator<Value> {
    public static final String OFF = "OFF";
    public static final String DEFAULT = "DEFAULT_";
    public static final String ICU4J = "ICU4J_";
    public static final String CHARSET = "CHARSET_";
    private static Locale[] LOCALES;
    private static volatile CompareMode lastUsed;
    private static final boolean CAN_USE_ICU4J;
    private final String name;
    private final int strength;

    static {
        boolean z = false;
        try {
            Class.forName("com.ibm.icu.text.Collator");
            z = true;
        } catch (Exception e) {
        }
        CAN_USE_ICU4J = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CompareMode(String str, int i) {
        this.name = str;
        this.strength = i;
    }

    public static CompareMode getInstance(String str, int i) {
        CompareMode compareMode;
        boolean z;
        CompareMode compareMode2 = lastUsed;
        if (compareMode2 != null && Objects.equals(compareMode2.name, str) && compareMode2.strength == i) {
            return compareMode2;
        }
        if (str == null || str.equals(OFF)) {
            compareMode = new CompareMode(str, i);
        } else {
            if (str.startsWith(ICU4J)) {
                z = true;
                str = str.substring(ICU4J.length());
            } else if (str.startsWith(DEFAULT)) {
                z = false;
                str = str.substring(DEFAULT.length());
            } else if (str.startsWith(CHARSET)) {
                z = false;
            } else {
                z = CAN_USE_ICU4J;
            }
            if (z) {
                compareMode = new CompareModeIcu4J(str, i);
            } else {
                compareMode = new CompareModeDefault(str, i);
            }
        }
        lastUsed = compareMode;
        return compareMode;
    }

    public static Locale[] getCollationLocales(boolean z) {
        Locale[] localeArr = LOCALES;
        if (localeArr == null && !z) {
            Locale[] availableLocales = Collator.getAvailableLocales();
            localeArr = availableLocales;
            LOCALES = availableLocales;
        }
        return localeArr;
    }

    public boolean equalsChars(String str, int i, String str2, int i2, boolean z) {
        char charAt = str.charAt(i);
        char charAt2 = str2.charAt(i2);
        if (charAt == charAt2) {
            return true;
        }
        if (z) {
            if (Character.toUpperCase(charAt) == Character.toUpperCase(charAt2) || Character.toLowerCase(charAt) == Character.toLowerCase(charAt2)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public int compareString(String str, String str2, boolean z) {
        if (z) {
            return str.compareToIgnoreCase(str2);
        }
        return str.compareTo(str2);
    }

    public static String getName(Locale locale) {
        Locale locale2 = Locale.ENGLISH;
        return StringUtils.toUpperEnglish((locale.getDisplayLanguage(locale2) + ' ' + locale.getDisplayCountry(locale2) + ' ' + locale.getVariant()).trim().replace(' ', '_'));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean compareLocaleNames(Locale locale, String str) {
        return str.equalsIgnoreCase(locale.toString()) || str.equalsIgnoreCase(locale.toLanguageTag()) || str.equalsIgnoreCase(getName(locale));
    }

    public static Collator getCollator(String str) {
        Collator collator = null;
        if (str.startsWith(ICU4J)) {
            str = str.substring(ICU4J.length());
        } else if (str.startsWith(DEFAULT)) {
            str = str.substring(DEFAULT.length());
        } else if (str.startsWith(CHARSET)) {
            return new CharsetCollator(Charset.forName(str.substring(CHARSET.length())));
        }
        int length = str.length();
        if (length == 2) {
            Locale locale = new Locale(StringUtils.toLowerEnglish(str), "");
            if (compareLocaleNames(locale, str)) {
                collator = Collator.getInstance(locale);
            }
        } else if (length == 5) {
            int indexOf = str.indexOf(95);
            if (indexOf >= 0) {
                Locale locale2 = new Locale(StringUtils.toLowerEnglish(str.substring(0, indexOf)), str.substring(indexOf + 1));
                if (compareLocaleNames(locale2, str)) {
                    collator = Collator.getInstance(locale2);
                }
            }
        } else if (str.indexOf(45) > 0) {
            Locale forLanguageTag = Locale.forLanguageTag(str);
            if (!forLanguageTag.getLanguage().isEmpty()) {
                return Collator.getInstance(forLanguageTag);
            }
        }
        if (collator == null) {
            Locale[] collationLocales = getCollationLocales(false);
            int length2 = collationLocales.length;
            int i = 0;
            while (true) {
                if (i >= length2) {
                    break;
                }
                Locale locale3 = collationLocales[i];
                if (!compareLocaleNames(locale3, str)) {
                    i++;
                } else {
                    collator = Collator.getInstance(locale3);
                    break;
                }
            }
        }
        return collator;
    }

    public String getName() {
        return this.name == null ? OFF : this.name;
    }

    public int getStrength() {
        return this.strength;
    }

    @Override // java.util.Comparator
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompareMode)) {
            return false;
        }
        CompareMode compareMode = (CompareMode) obj;
        if (!getName().equals(compareMode.getName()) || this.strength != compareMode.strength) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (31 * ((31 * 1) + getName().hashCode())) + this.strength;
    }

    @Override // java.util.Comparator
    public int compare(Value value, Value value2) {
        return value.compareTo(value2, null, this);
    }
}
