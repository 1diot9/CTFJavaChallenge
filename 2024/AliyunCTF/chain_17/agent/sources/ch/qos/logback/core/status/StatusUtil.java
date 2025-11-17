package ch.qos.logback.core.status;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/StatusUtil.class */
public class StatusUtil {
    StatusManager sm;

    public StatusUtil(StatusManager sm) {
        this.sm = sm;
    }

    public StatusUtil(Context context) {
        this.sm = context.getStatusManager();
    }

    public static boolean contextHasStatusListener(Context context) {
        List<StatusListener> listeners;
        StatusManager sm = context.getStatusManager();
        if (sm == null || (listeners = sm.getCopyOfStatusListenerList()) == null || listeners.size() == 0) {
            return false;
        }
        return true;
    }

    public static List<Status> filterStatusListByTimeThreshold(List<Status> rawList, long threshold) {
        List<Status> filteredList = new ArrayList<>();
        for (Status s : rawList) {
            if (s.getTimestamp() >= threshold) {
                filteredList.add(s);
            }
        }
        return filteredList;
    }

    public void addStatus(Status status) {
        if (this.sm != null) {
            this.sm.add(status);
        }
    }

    public void addInfo(Object caller, String msg) {
        addStatus(new InfoStatus(msg, caller));
    }

    public void addWarn(Object caller, String msg) {
        addStatus(new WarnStatus(msg, caller));
    }

    public void addError(Object caller, String msg, Throwable t) {
        addStatus(new ErrorStatus(msg, caller, t));
    }

    public boolean hasXMLParsingErrors(long threshold) {
        return containsMatch(threshold, 2, CoreConstants.XML_PARSING);
    }

    public boolean noXMLParsingErrorsOccurred(long threshold) {
        return !hasXMLParsingErrors(threshold);
    }

    public int getHighestLevel(long threshold) {
        List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
        int maxLevel = 0;
        for (Status s : filteredList) {
            if (s.getLevel() > maxLevel) {
                maxLevel = s.getLevel();
            }
        }
        return maxLevel;
    }

    public boolean isErrorFree(long threshold) {
        return getHighestLevel(threshold) < 2;
    }

    public boolean isWarningOrErrorFree(long threshold) {
        return 1 > getHighestLevel(threshold);
    }

    public boolean containsMatch(long threshold, int level, String regex) {
        List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
        Pattern p = Pattern.compile(regex);
        for (Status status : filteredList) {
            if (level == status.getLevel()) {
                String msg = status.getMessage();
                Matcher matcher = p.matcher(msg);
                if (matcher.lookingAt()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsMatch(int level, String regex) {
        return containsMatch(0L, level, regex);
    }

    public boolean containsMatch(String regex) {
        Pattern p = Pattern.compile(regex);
        for (Status status : this.sm.getCopyOfStatusList()) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    public int levelCount(int level, long threshold) {
        List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
        int count = 0;
        for (Status status : filteredList) {
            if (status.getLevel() == level) {
                count++;
            }
        }
        return count;
    }

    public int matchCount(String regex) {
        int count = 0;
        Pattern p = Pattern.compile(regex);
        for (Status status : this.sm.getCopyOfStatusList()) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                count++;
            }
        }
        return count;
    }

    public boolean containsException(Class<?> exceptionType) {
        return containsException(exceptionType, null);
    }

    public boolean containsException(Class<?> exceptionType, String msgRegex) {
        for (Status status : this.sm.getCopyOfStatusList()) {
            Throwable throwable = status.getThrowable();
            while (true) {
                Throwable t = throwable;
                if (t != null) {
                    if (t.getClass().getName().equals(exceptionType.getName()) && (msgRegex == null || checkRegexMatch(t.getMessage(), msgRegex))) {
                        return true;
                    }
                    throwable = t.getCause();
                }
            }
        }
        return false;
    }

    private boolean checkRegexMatch(String message, String msgRegex) {
        Pattern p = Pattern.compile(msgRegex);
        Matcher matcher = p.matcher(message);
        return matcher.lookingAt();
    }

    public long timeOfLastReset() {
        List<Status> statusList = this.sm.getCopyOfStatusList();
        if (statusList == null) {
            return -1L;
        }
        int len = statusList.size();
        for (int i = len - 1; i >= 0; i--) {
            Status s = statusList.get(i);
            if (CoreConstants.RESET_MSG_PREFIX.equals(s.getMessage())) {
                return s.getTimestamp();
            }
        }
        return -1L;
    }

    public static String diff(Status left, Status right) {
        StringBuilder sb = new StringBuilder();
        if (left.getLevel() != right.getLevel()) {
            sb.append(" left.level ").append(left.getLevel()).append(" != right.level ").append(right.getLevel());
        }
        if (left.getTimestamp() != right.getTimestamp()) {
            sb.append(" left.timestamp ").append(left.getTimestamp()).append(" != right.timestamp ").append(right.getTimestamp());
        }
        if (!Objects.equals(left.getMessage(), right.getMessage())) {
            sb.append(" left.message ").append(left.getMessage()).append(" != right.message ").append(right.getMessage());
        }
        return sb.toString();
    }
}
