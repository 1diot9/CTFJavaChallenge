package com.alibaba.com.caucho.hessian.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ClassFactory.class */
public class ClassFactory {
    private ClassLoader _loader;
    private boolean _isWhitelist;
    private LinkedList<Allow> _allowList;
    protected static final Logger log = Logger.getLogger(ClassFactory.class.getName());
    private static final Map<String, Object> _allowSubClassSet = new ConcurrentHashMap();
    private static final Map<String, Object> _allowClassSet = new ConcurrentHashMap();
    private static final ArrayList<Allow> _staticAllowList = new ArrayList<>();

    static {
        ClassLoader classLoader = ClassFactory.class.getClassLoader();
        try {
            String[] denyClasses = readLines(classLoader.getResourceAsStream("DENY_CLASS"));
            for (String denyClass : denyClasses) {
                if (!denyClass.startsWith("#")) {
                    if (denyClass.endsWith(".")) {
                        _staticAllowList.add(new AllowPrefix(denyClass, false));
                    } else {
                        _staticAllowList.add(new Allow(toPattern(denyClass), false));
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassFactory(ClassLoader loader) {
        this._loader = loader;
        initAllow();
    }

    public Class<?> load(String className) throws ClassNotFoundException {
        if (isAllow(className)) {
            Class<?> aClass = Class.forName(className, false, this._loader);
            if (_allowClassSet.containsKey(className)) {
                return aClass;
            }
            if (aClass.getInterfaces().length > 0) {
                for (Class<?> anInterface : aClass.getInterfaces()) {
                    if (!isAllow(anInterface.getName())) {
                        log.log(Level.SEVERE, className + "'s interfaces: " + anInterface.getName() + " in blacklist or not in whitelist, deserialization with type 'HashMap' instead.");
                        return HashMap.class;
                    }
                }
            }
            List<Class<?>> allSuperClasses = new LinkedList<>();
            Class<?> superclass = aClass.getSuperclass();
            while (true) {
                Class<?> superClass = superclass;
                if (superClass == null) {
                    break;
                }
                allSuperClasses.add(superClass);
                superclass = superClass.getSuperclass();
            }
            for (Class<?> aSuperClass : allSuperClasses) {
                if (!isAllow(aSuperClass.getName())) {
                    log.log(Level.SEVERE, className + "'s superClass: " + aSuperClass.getName() + " in blacklist or not in whitelist, deserialization with type 'HashMap' instead.");
                    return HashMap.class;
                }
            }
            _allowClassSet.put(className, className);
            return aClass;
        }
        log.log(Level.SEVERE, className + " in blacklist or not in whitelist, deserialization with type 'HashMap' instead.");
        return HashMap.class;
    }

    private boolean isAllow(String className) {
        LinkedList<Allow> allowList = this._allowList;
        if (allowList == null || _allowSubClassSet.containsKey(className)) {
            return true;
        }
        Iterator<Allow> it = allowList.iterator();
        while (it.hasNext()) {
            Allow allow = it.next();
            Boolean isAllow = allow.allow(className);
            if (isAllow != null) {
                if (isAllow.booleanValue()) {
                    _allowSubClassSet.put(className, className);
                }
                return isAllow.booleanValue();
            }
        }
        if (this._isWhitelist) {
            return false;
        }
        _allowSubClassSet.put(className, className);
        return true;
    }

    public void setWhitelist(boolean isWhitelist) {
        _allowClassSet.clear();
        _allowSubClassSet.clear();
        this._isWhitelist = isWhitelist;
        initAllow();
    }

    public void allow(String pattern) {
        _allowClassSet.clear();
        _allowSubClassSet.clear();
        initAllow();
        synchronized (this) {
            this._allowList.addFirst(new Allow(toPattern(pattern), true));
        }
    }

    public void deny(String pattern) {
        _allowClassSet.clear();
        _allowSubClassSet.clear();
        initAllow();
        synchronized (this) {
            this._allowList.addFirst(new Allow(toPattern(pattern), false));
        }
    }

    private static String toPattern(String pattern) {
        return pattern.replace(".", "\\.").replace("*", ".*");
    }

    private void initAllow() {
        synchronized (this) {
            if (this._allowList == null) {
                this._allowList = new LinkedList<>();
                this._allowList.addAll(_staticAllowList);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ClassFactory$Allow.class */
    public static class Allow {
        private Boolean _isAllow;
        private Pattern _pattern;

        public Allow() {
        }

        private Allow(String pattern, boolean isAllow) {
            this._isAllow = Boolean.valueOf(isAllow);
            this._pattern = Pattern.compile(pattern);
        }

        Boolean allow(String className) {
            if (this._pattern.matcher(className).matches()) {
                return this._isAllow;
            }
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ClassFactory$AllowPrefix.class */
    static class AllowPrefix extends Allow {
        private Boolean _isAllow;
        private String _prefix;

        private AllowPrefix(String prefix, boolean isAllow) {
            this._isAllow = Boolean.valueOf(isAllow);
            this._prefix = prefix;
        }

        @Override // com.alibaba.com.caucho.hessian.io.ClassFactory.Allow
        Boolean allow(String className) {
            if (className.startsWith(this._prefix)) {
                return this._isAllow;
            }
            return null;
        }
    }

    public static String[] readLines(InputStream is) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Throwable th = null;
        while (true) {
            try {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lines.add(line);
                } finally {
                }
            } catch (Throwable th2) {
                if (reader != null) {
                    if (th != null) {
                        try {
                            reader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        reader.close();
                    }
                }
                throw th2;
            }
        }
        String[] strArr = (String[]) lines.toArray(new String[0]);
        if (reader != null) {
            if (0 != 0) {
                try {
                    reader.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                reader.close();
            }
        }
        return strArr;
    }
}
