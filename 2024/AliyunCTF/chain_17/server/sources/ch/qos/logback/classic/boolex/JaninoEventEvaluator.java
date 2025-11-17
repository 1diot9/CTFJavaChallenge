package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.boolex.JaninoEventEvaluatorBase;
import ch.qos.logback.core.boolex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/boolex/JaninoEventEvaluator.class */
public class JaninoEventEvaluator extends JaninoEventEvaluatorBase<ILoggingEvent> {
    public static final String IMPORT_LEVEL = "import ch.qos.logback.classic.Level;\r\n";
    public static final List<String> DEFAULT_PARAM_NAME_LIST = new ArrayList();
    public static final List<Class<?>> DEFAULT_PARAM_TYPE_LIST = new ArrayList();

    static {
        DEFAULT_PARAM_NAME_LIST.add("DEBUG");
        DEFAULT_PARAM_NAME_LIST.add("INFO");
        DEFAULT_PARAM_NAME_LIST.add("WARN");
        DEFAULT_PARAM_NAME_LIST.add("ERROR");
        DEFAULT_PARAM_NAME_LIST.add("event");
        DEFAULT_PARAM_NAME_LIST.add(JsonEncoder.MESSAGE_ATTR_NAME);
        DEFAULT_PARAM_NAME_LIST.add("formattedMessage");
        DEFAULT_PARAM_NAME_LIST.add("logger");
        DEFAULT_PARAM_NAME_LIST.add("loggerContext");
        DEFAULT_PARAM_NAME_LIST.add("level");
        DEFAULT_PARAM_NAME_LIST.add("timeStamp");
        DEFAULT_PARAM_NAME_LIST.add(JsonEncoder.MDC_ATTR_NAME);
        DEFAULT_PARAM_NAME_LIST.add("throwableProxy");
        DEFAULT_PARAM_NAME_LIST.add(JsonEncoder.THROWABLE_ATTR_NAME);
        DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(ILoggingEvent.class);
        DEFAULT_PARAM_TYPE_LIST.add(String.class);
        DEFAULT_PARAM_TYPE_LIST.add(String.class);
        DEFAULT_PARAM_TYPE_LIST.add(String.class);
        DEFAULT_PARAM_TYPE_LIST.add(LoggerContextVO.class);
        DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(Long.TYPE);
        DEFAULT_PARAM_TYPE_LIST.add(Map.class);
        DEFAULT_PARAM_TYPE_LIST.add(IThrowableProxy.class);
        DEFAULT_PARAM_TYPE_LIST.add(Throwable.class);
    }

    @Override // ch.qos.logback.core.boolex.JaninoEventEvaluatorBase
    protected String getDecoratedExpression() {
        String expression = getExpression();
        if (!expression.contains("return")) {
            expression = "return " + expression + ";";
            addInfo("Adding [return] prefix and a semicolon suffix. Expression becomes [" + expression + "]");
            addInfo("See also http://logback.qos.ch/codes.html#block");
        }
        return "import ch.qos.logback.classic.Level;\r\n" + expression;
    }

    @Override // ch.qos.logback.core.boolex.JaninoEventEvaluatorBase
    protected String[] getParameterNames() {
        List<String> fullNameList = new ArrayList<>();
        fullNameList.addAll(DEFAULT_PARAM_NAME_LIST);
        for (int i = 0; i < this.matcherList.size(); i++) {
            Matcher m = this.matcherList.get(i);
            fullNameList.add(m.getName());
        }
        return (String[]) fullNameList.toArray(CoreConstants.EMPTY_STRING_ARRAY);
    }

    @Override // ch.qos.logback.core.boolex.JaninoEventEvaluatorBase
    protected Class<?>[] getParameterTypes() {
        List<Class<?>> fullTypeList = new ArrayList<>();
        fullTypeList.addAll(DEFAULT_PARAM_TYPE_LIST);
        for (int i = 0; i < this.matcherList.size(); i++) {
            fullTypeList.add(Matcher.class);
        }
        return (Class[]) fullTypeList.toArray(CoreConstants.EMPTY_CLASS_ARRAY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.boolex.JaninoEventEvaluatorBase
    public Object[] getParameterValues(ILoggingEvent loggingEvent) {
        int i;
        int matcherListSize = this.matcherList.size();
        Object[] values = new Object[DEFAULT_PARAM_NAME_LIST.size() + matcherListSize];
        int i2 = 0 + 1;
        values[0] = Level.DEBUG_INTEGER;
        int i3 = i2 + 1;
        values[i2] = Level.INFO_INTEGER;
        int i4 = i3 + 1;
        values[i3] = Level.WARN_INTEGER;
        int i5 = i4 + 1;
        values[i4] = Level.ERROR_INTEGER;
        int i6 = i5 + 1;
        values[i5] = loggingEvent;
        int i7 = i6 + 1;
        values[i6] = loggingEvent.getMessage();
        int i8 = i7 + 1;
        values[i7] = loggingEvent.getFormattedMessage();
        int i9 = i8 + 1;
        values[i8] = loggingEvent.getLoggerName();
        int i10 = i9 + 1;
        values[i9] = loggingEvent.getLoggerContextVO();
        int i11 = i10 + 1;
        values[i10] = loggingEvent.getLevel().toInteger();
        int i12 = i11 + 1;
        values[i11] = Long.valueOf(loggingEvent.getTimeStamp());
        int i13 = i12 + 1;
        values[i12] = loggingEvent.getMDCPropertyMap();
        IThrowableProxy iThrowableProxy = loggingEvent.getThrowableProxy();
        if (iThrowableProxy != null) {
            int i14 = i13 + 1;
            values[i13] = iThrowableProxy;
            if (iThrowableProxy instanceof ThrowableProxy) {
                i = i14 + 1;
                values[i14] = ((ThrowableProxy) iThrowableProxy).getThrowable();
            } else {
                i = i14 + 1;
                values[i14] = null;
            }
        } else {
            int i15 = i13 + 1;
            values[i13] = null;
            i = i15 + 1;
            values[i15] = null;
        }
        for (int j = 0; j < matcherListSize; j++) {
            int i16 = i;
            i++;
            values[i16] = this.matcherList.get(j);
        }
        return values;
    }
}
