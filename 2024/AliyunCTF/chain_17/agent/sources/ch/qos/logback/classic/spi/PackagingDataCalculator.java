package ch.qos.logback.classic.spi;

import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/PackagingDataCalculator.class */
public class PackagingDataCalculator {
    HashMap<String, ClassPackagingData> cache = new HashMap<>();
    static final StackTraceElementProxy[] STEP_ARRAY_TEMPLATE = new StackTraceElementProxy[0];
    private static boolean GET_CALLER_CLASS_METHOD_AVAILABLE = false;

    public void calculate(IThrowableProxy tp) {
        while (tp != null) {
            populateFrames(tp.getStackTraceElementProxyArray());
            IThrowableProxy[] suppressed = tp.getSuppressed();
            if (suppressed != null) {
                for (IThrowableProxy current : suppressed) {
                    populateFrames(current.getStackTraceElementProxyArray());
                }
            }
            tp = tp.getCause();
        }
    }

    void populateFrames(StackTraceElementProxy[] stepArray) {
        Throwable t = new Throwable("local stack reference");
        StackTraceElement[] localSTEArray = t.getStackTrace();
        int commonFrames = STEUtil.findNumberOfCommonFrames(localSTEArray, stepArray);
        int length = localSTEArray.length - commonFrames;
        int stepFirstCommon = stepArray.length - commonFrames;
        ClassLoader lastExactClassLoader = null;
        ClassLoader firsExactClassLoader = null;
        int missfireCount = 0;
        for (int i = 0; i < commonFrames; i++) {
            Class<?> callerClass = null;
            if (GET_CALLER_CLASS_METHOD_AVAILABLE) {
            }
            StackTraceElementProxy step = stepArray[stepFirstCommon + i];
            String stepClassname = step.ste.getClassName();
            if (0 != 0 && stepClassname.equals(callerClass.getName())) {
                lastExactClassLoader = callerClass.getClassLoader();
                if (firsExactClassLoader == null) {
                    firsExactClassLoader = lastExactClassLoader;
                }
                ClassPackagingData pi = calculateByExactType(null);
                step.setClassPackagingData(pi);
            } else {
                missfireCount++;
                ClassPackagingData pi2 = computeBySTEP(step, lastExactClassLoader);
                step.setClassPackagingData(pi2);
            }
        }
        populateUncommonFrames(commonFrames, stepArray, firsExactClassLoader);
    }

    void populateUncommonFrames(int commonFrames, StackTraceElementProxy[] stepArray, ClassLoader firstExactClassLoader) {
        int uncommonFrames = stepArray.length - commonFrames;
        for (int i = 0; i < uncommonFrames; i++) {
            StackTraceElementProxy step = stepArray[i];
            ClassPackagingData pi = computeBySTEP(step, firstExactClassLoader);
            step.setClassPackagingData(pi);
        }
    }

    private ClassPackagingData calculateByExactType(Class<?> type) {
        String className = type.getName();
        ClassPackagingData cpd = this.cache.get(className);
        if (cpd != null) {
            return cpd;
        }
        String version = getImplementationVersion(type);
        String codeLocation = getCodeLocation(type);
        ClassPackagingData cpd2 = new ClassPackagingData(codeLocation, version);
        this.cache.put(className, cpd2);
        return cpd2;
    }

    private ClassPackagingData computeBySTEP(StackTraceElementProxy step, ClassLoader lastExactClassLoader) {
        String className = step.ste.getClassName();
        ClassPackagingData cpd = this.cache.get(className);
        if (cpd != null) {
            return cpd;
        }
        Class<?> type = bestEffortLoadClass(lastExactClassLoader, className);
        String version = getImplementationVersion(type);
        String codeLocation = getCodeLocation(type);
        ClassPackagingData cpd2 = new ClassPackagingData(codeLocation, version, false);
        this.cache.put(className, cpd2);
        return cpd2;
    }

    String getImplementationVersion(Class<?> type) {
        Package aPackage;
        String v;
        if (type == null || (aPackage = type.getPackage()) == null || (v = aPackage.getImplementationVersion()) == null) {
            return "na";
        }
        return v;
    }

    String getCodeLocation(Class<?> type) {
        URL resource;
        if (type != null) {
            try {
                CodeSource codeSource = type.getProtectionDomain().getCodeSource();
                if (codeSource != null && (resource = codeSource.getLocation()) != null) {
                    String locationStr = resource.toString();
                    String result = getCodeLocation(locationStr, '/');
                    if (result != null) {
                        return result;
                    }
                    return getCodeLocation(locationStr, '\\');
                }
                return "na";
            } catch (Exception e) {
                return "na";
            }
        }
        return "na";
    }

    private String getCodeLocation(String locationStr, char separator) {
        int idx = locationStr.lastIndexOf(separator);
        if (isFolder(idx, locationStr)) {
            return locationStr.substring(locationStr.lastIndexOf(separator, idx - 1) + 1);
        }
        if (idx > 0) {
            return locationStr.substring(idx + 1);
        }
        return null;
    }

    private boolean isFolder(int idx, String text) {
        return idx != -1 && idx + 1 == text.length();
    }

    private Class<?> loadClass(ClassLoader cl, String className) {
        if (cl == null) {
            return null;
        }
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        } catch (NoClassDefFoundError e3) {
            return null;
        }
    }

    private Class<?> bestEffortLoadClass(ClassLoader lastGuaranteedClassLoader, String className) {
        Class<?> result = loadClass(lastGuaranteedClassLoader, className);
        if (result != null) {
            return result;
        }
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (tccl != lastGuaranteedClassLoader) {
            result = loadClass(tccl, className);
        }
        if (result != null) {
            return result;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        } catch (NoClassDefFoundError e3) {
            return null;
        }
    }
}
