package ch.qos.logback.core.testUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/EnvUtilForTests.class */
public class EnvUtilForTests {
    static String GITHUB_HOME = "/home/runner";
    static String LOCAL_REPOSITORY_PREFIX = GITHUB_HOME;

    public static boolean isGithubAction() {
        String userHome = System.getProperty("user.home");
        String localRepository = System.getProperty("localRepository");
        if (GITHUB_HOME.equals(userHome)) {
            return true;
        }
        if (localRepository != null && localRepository.startsWith(LOCAL_REPOSITORY_PREFIX)) {
            return true;
        }
        return false;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").indexOf("Windows") != -1;
    }

    public static boolean isMac() {
        return System.getProperty("os.name").indexOf("Mac") != -1;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").indexOf("Linux") != -1;
    }

    public static boolean isRunningOnSlowJenkins() {
        return System.getProperty(CoreTestConstants.SLOW_JENKINS) != null;
    }

    public static String getLocalHostName() {
        try {
            InetAddress localhostIA = InetAddress.getLocalHost();
            return localhostIA.getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static boolean isLocalHostNameInList(String[] hostList) {
        String localHostName = getLocalHostName();
        if (localHostName == null) {
            return false;
        }
        for (String host : hostList) {
            if (host.equalsIgnoreCase(localHostName)) {
                return true;
            }
        }
        return false;
    }

    public static String getPathToBash() {
        if (isLinux()) {
            return CoreTestConstants.BASH_PATH_ON_LINUX;
        }
        if (isLocalHostNameInList(new String[]{"hetz", "het"})) {
            return CoreTestConstants.BASH_PATH_ON_CYGWIN;
        }
        return null;
    }
}
