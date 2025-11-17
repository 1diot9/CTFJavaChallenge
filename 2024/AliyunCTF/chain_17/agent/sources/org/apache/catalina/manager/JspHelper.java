package org.apache.catalina.manager;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.catalina.Session;
import org.apache.catalina.manager.util.SessionUtils;
import org.apache.tomcat.util.security.Escape;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/JspHelper.class */
public class JspHelper {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int HIGHEST_SPECIAL = 62;
    private static final char[][] specialCharactersRepresentation = new char[63];

    private JspHelper() {
    }

    public static String guessDisplayLocaleFromSession(Session in_session) {
        return localeToString(SessionUtils.guessLocaleFromSession(in_session));
    }

    private static String localeToString(Locale locale) {
        if (locale != null) {
            return escapeXml(locale.toString());
        }
        return "";
    }

    public static String guessDisplayUserFromSession(Session in_session) {
        Object user = SessionUtils.guessUserFromSession(in_session);
        return escapeXml(user);
    }

    public static String getDisplayCreationTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0) {
                return "";
            }
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(new Date(in_session.getCreationTime()));
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String getDisplayLastAccessedTimeForSession(Session in_session) {
        try {
            if (in_session.getLastAccessedTime() == 0) {
                return "";
            }
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(new Date(in_session.getLastAccessedTime()));
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String getDisplayUsedTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0) {
                return "";
            }
            return secondsToTimeString(SessionUtils.getUsedTimeForSession(in_session) / 1000);
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String getDisplayTTLForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0) {
                return "";
            }
            return secondsToTimeString(SessionUtils.getTTLForSession(in_session) / 1000);
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String getDisplayInactiveTimeForSession(Session in_session) {
        try {
            if (in_session.getCreationTime() == 0) {
                return "";
            }
            return secondsToTimeString(SessionUtils.getInactiveTimeForSession(in_session) / 1000);
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String secondsToTimeString(long in_seconds) {
        StringBuilder buff = new StringBuilder(9);
        if (in_seconds < 0) {
            buff.append('-');
            in_seconds = -in_seconds;
        }
        long rest = in_seconds;
        long hour = rest / 3600;
        long rest2 = rest % 3600;
        long minute = rest2 / 60;
        long rest3 = rest2 % 60;
        if (hour < 10) {
            buff.append('0');
        }
        buff.append(hour);
        buff.append(':');
        if (minute < 10) {
            buff.append('0');
        }
        buff.append(minute);
        buff.append(':');
        if (rest3 < 10) {
            buff.append('0');
        }
        buff.append(rest3);
        return buff.toString();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [char[], char[][]] */
    static {
        specialCharactersRepresentation[38] = "&amp;".toCharArray();
        specialCharactersRepresentation[60] = "&lt;".toCharArray();
        specialCharactersRepresentation[62] = "&gt;".toCharArray();
        specialCharactersRepresentation[34] = "&#034;".toCharArray();
        specialCharactersRepresentation[39] = "&#039;".toCharArray();
    }

    public static String escapeXml(Object obj) {
        String obj2;
        String value = null;
        if (obj == null) {
            obj2 = null;
        } else {
            try {
                obj2 = obj.toString();
            } catch (Exception e) {
            }
        }
        value = obj2;
        return escapeXml(value);
    }

    public static String escapeXml(String buffer) {
        if (buffer == null) {
            return "";
        }
        return Escape.xml(buffer);
    }

    public static String formatNumber(long number) {
        return NumberFormat.getNumberInstance().format(number);
    }
}
