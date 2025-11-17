package org.apache.tomcat.util.res;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/res/StringManager.class */
public class StringManager {
    private final ResourceBundle bundle;
    private final Locale locale;
    private static int LOCALE_CACHE_SIZE = 10;
    private static final Map<String, Map<Locale, StringManager>> managers = new HashMap();

    private StringManager(String packageName, Locale locale) {
        String bundleName = packageName + ".LocalStrings";
        ResourceBundle bnd = null;
        try {
            locale = locale.getLanguage().equals(Locale.ENGLISH.getLanguage()) ? Locale.ROOT : locale;
            bnd = ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException e) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                try {
                    bnd = ResourceBundle.getBundle(bundleName, locale, cl);
                } catch (MissingResourceException e2) {
                }
            }
        }
        this.bundle = bnd;
        if (this.bundle != null) {
            Locale bundleLocale = this.bundle.getLocale();
            if (bundleLocale.equals(Locale.ROOT)) {
                this.locale = Locale.ENGLISH;
                return;
            } else {
                this.locale = bundleLocale;
                return;
            }
        }
        this.locale = null;
    }

    public String getString(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key may not have a null value");
        }
        String str = null;
        try {
            if (this.bundle != null) {
                str = this.bundle.getString(key);
            }
        } catch (MissingResourceException e) {
            str = null;
        }
        return str;
    }

    public String getString(String key, Object... args) {
        String value = getString(key);
        if (value == null) {
            value = key;
        }
        MessageFormat mf = new MessageFormat(value);
        mf.setLocale(this.locale);
        return mf.format(args, new StringBuffer(), (FieldPosition) null).toString();
    }

    public Locale getLocale() {
        return this.locale;
    }

    public static final StringManager getManager(Class<?> clazz) {
        return getManager(clazz.getPackage().getName());
    }

    public static final StringManager getManager(String packageName) {
        return getManager(packageName, Locale.getDefault());
    }

    public static final synchronized StringManager getManager(String packageName, Locale locale) {
        Map<Locale, StringManager> map = managers.get(packageName);
        if (map == null) {
            map = new LinkedHashMap<Locale, StringManager>(LOCALE_CACHE_SIZE, 0.75f, true) { // from class: org.apache.tomcat.util.res.StringManager.1
                private static final long serialVersionUID = 1;

                @Override // java.util.LinkedHashMap
                protected boolean removeEldestEntry(Map.Entry<Locale, StringManager> eldest) {
                    if (size() > StringManager.LOCALE_CACHE_SIZE - 1) {
                        return true;
                    }
                    return false;
                }
            };
            managers.put(packageName, map);
        }
        StringManager mgr = map.get(locale);
        if (mgr == null) {
            mgr = new StringManager(packageName, locale);
            map.put(locale, mgr);
        }
        return mgr;
    }

    public static StringManager getManager(String packageName, Enumeration<Locale> requestedLocales) {
        while (requestedLocales.hasMoreElements()) {
            Locale locale = requestedLocales.nextElement();
            StringManager result = getManager(packageName, locale);
            if (result.getLocale().equals(locale)) {
                return result;
            }
        }
        return getManager(packageName);
    }
}
