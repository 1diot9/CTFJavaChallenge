package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.status.ErrorStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/CallerDataConverter.class */
public class CallerDataConverter extends ClassicConverter {
    public static final String DEFAULT_CALLER_LINE_PREFIX = "Caller+";
    public static final String DEFAULT_RANGE_DELIMITER = "..";
    private int depthStart = 0;
    private int depthEnd = 5;
    List<EventEvaluator<ILoggingEvent>> evaluatorList = null;
    final int MAX_ERROR_COUNT = 4;
    int errorCount = 0;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String depthStr = getFirstOption();
        if (depthStr == null) {
            return;
        }
        try {
            if (isRange(depthStr)) {
                String[] numbers = splitRange(depthStr);
                if (numbers.length == 2) {
                    this.depthStart = Integer.parseInt(numbers[0]);
                    this.depthEnd = Integer.parseInt(numbers[1]);
                    checkRange();
                } else {
                    addError("Failed to parse depth option as range [" + depthStr + "]");
                }
            } else {
                this.depthEnd = Integer.parseInt(depthStr);
            }
        } catch (NumberFormatException nfe) {
            addError("Failed to parse depth option [" + depthStr + "]", nfe);
        }
        List<String> optionList = getOptionList();
        if (optionList != null && optionList.size() > 1) {
            int optionListSize = optionList.size();
            for (int i = 1; i < optionListSize; i++) {
                String evaluatorStr = optionList.get(i);
                Context context = getContext();
                if (context != null) {
                    Map<String, EventEvaluator<?>> evaluatorMap = (Map) context.getObject(CoreConstants.EVALUATOR_MAP);
                    EventEvaluator<?> eventEvaluator = evaluatorMap.get(evaluatorStr);
                    if (eventEvaluator != null) {
                        addEvaluator(eventEvaluator);
                    }
                }
            }
        }
    }

    private boolean isRange(String depthStr) {
        return depthStr.contains(getDefaultRangeDelimiter());
    }

    private String[] splitRange(String depthStr) {
        return depthStr.split(Pattern.quote(getDefaultRangeDelimiter()), 2);
    }

    private void checkRange() {
        if (this.depthStart < 0 || this.depthEnd < 0) {
            addError("Invalid depthStart/depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (negative values are not allowed)");
        } else if (this.depthStart >= this.depthEnd) {
            addError("Invalid depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (start greater or equal to end)");
        }
    }

    private void addEvaluator(EventEvaluator<ILoggingEvent> ee) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList();
        }
        this.evaluatorList.add(ee);
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        StringBuilder buf = new StringBuilder();
        if (this.evaluatorList != null) {
            boolean printCallerData = false;
            int i = 0;
            while (true) {
                if (i >= this.evaluatorList.size()) {
                    break;
                }
                EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
                try {
                } catch (EvaluationException eex) {
                    this.errorCount++;
                    if (this.errorCount < 4) {
                        addError("Exception thrown for evaluator named [" + ee.getName() + "]", eex);
                    } else if (this.errorCount == 4) {
                        ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + ee.getName() + "].", this, eex);
                        errorStatus.add(new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
                        addStatus(errorStatus);
                    }
                }
                if (!ee.evaluate(le)) {
                    i++;
                } else {
                    printCallerData = true;
                    break;
                }
            }
            if (!printCallerData) {
                return "";
            }
        }
        StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > this.depthStart) {
            int limit = this.depthEnd < cda.length ? this.depthEnd : cda.length;
            for (int i2 = this.depthStart; i2 < limit; i2++) {
                buf.append(getCallerLinePrefix());
                buf.append(i2);
                buf.append("\t at ");
                buf.append(cda[i2]);
                buf.append(CoreConstants.LINE_SEPARATOR);
            }
            return buf.toString();
        }
        return CallerData.CALLER_DATA_NA;
    }

    protected String getCallerLinePrefix() {
        return DEFAULT_CALLER_LINE_PREFIX;
    }

    protected String getDefaultRangeDelimiter() {
        return "..";
    }
}
