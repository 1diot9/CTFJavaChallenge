package org.springframework.aop.interceptor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/CustomizableTraceInterceptor.class */
public class CustomizableTraceInterceptor extends AbstractTraceInterceptor {
    private static final String DEFAULT_ENTER_MESSAGE = "Entering method '$[methodName]' of class [$[targetClassName]]";
    private static final String DEFAULT_EXIT_MESSAGE = "Exiting method '$[methodName]' of class [$[targetClassName]]";
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Exception thrown in method '$[methodName]' of class [$[targetClassName]]";
    private String enterMessage = DEFAULT_ENTER_MESSAGE;
    private String exitMessage = DEFAULT_EXIT_MESSAGE;
    private String exceptionMessage = DEFAULT_EXCEPTION_MESSAGE;
    private static final Pattern PATTERN = Pattern.compile("\\$\\[\\p{Alpha}+]");
    public static final String PLACEHOLDER_METHOD_NAME = "$[methodName]";
    public static final String PLACEHOLDER_TARGET_CLASS_NAME = "$[targetClassName]";
    public static final String PLACEHOLDER_TARGET_CLASS_SHORT_NAME = "$[targetClassShortName]";
    public static final String PLACEHOLDER_RETURN_VALUE = "$[returnValue]";
    public static final String PLACEHOLDER_ARGUMENT_TYPES = "$[argumentTypes]";
    public static final String PLACEHOLDER_ARGUMENTS = "$[arguments]";
    public static final String PLACEHOLDER_EXCEPTION = "$[exception]";
    public static final String PLACEHOLDER_INVOCATION_TIME = "$[invocationTime]";
    static final Set<String> ALLOWED_PLACEHOLDERS = Set.of(PLACEHOLDER_METHOD_NAME, PLACEHOLDER_TARGET_CLASS_NAME, PLACEHOLDER_TARGET_CLASS_SHORT_NAME, PLACEHOLDER_RETURN_VALUE, PLACEHOLDER_ARGUMENT_TYPES, PLACEHOLDER_ARGUMENTS, PLACEHOLDER_EXCEPTION, PLACEHOLDER_INVOCATION_TIME);

    public void setEnterMessage(String enterMessage) throws IllegalArgumentException {
        Assert.hasText(enterMessage, "enterMessage must not be empty");
        checkForInvalidPlaceholders(enterMessage);
        Assert.doesNotContain(enterMessage, PLACEHOLDER_RETURN_VALUE, "enterMessage cannot contain placeholder $[returnValue]");
        Assert.doesNotContain(enterMessage, PLACEHOLDER_EXCEPTION, "enterMessage cannot contain placeholder $[exception]");
        Assert.doesNotContain(enterMessage, PLACEHOLDER_INVOCATION_TIME, "enterMessage cannot contain placeholder $[invocationTime]");
        this.enterMessage = enterMessage;
    }

    public void setExitMessage(String exitMessage) {
        Assert.hasText(exitMessage, "exitMessage must not be empty");
        checkForInvalidPlaceholders(exitMessage);
        Assert.doesNotContain(exitMessage, PLACEHOLDER_EXCEPTION, "exitMessage cannot contain placeholder$[exception]");
        this.exitMessage = exitMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        Assert.hasText(exceptionMessage, "exceptionMessage must not be empty");
        checkForInvalidPlaceholders(exceptionMessage);
        Assert.doesNotContain(exceptionMessage, PLACEHOLDER_RETURN_VALUE, "exceptionMessage cannot contain placeholder $[returnValue]");
        this.exceptionMessage = exceptionMessage;
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = ClassUtils.getQualifiedMethodName(invocation.getMethod());
        StopWatch stopWatch = new StopWatch(name);
        Object returnValue = null;
        boolean exitThroughException = false;
        try {
            try {
                stopWatch.start(name);
                writeToLog(logger, replacePlaceholders(this.enterMessage, invocation, null, null, -1L));
                returnValue = invocation.proceed();
                if (0 == 0) {
                    if (stopWatch.isRunning()) {
                        stopWatch.stop();
                    }
                    writeToLog(logger, replacePlaceholders(this.exitMessage, invocation, returnValue, null, stopWatch.getTotalTimeMillis()));
                }
                return returnValue;
            } finally {
            }
        } catch (Throwable th) {
            if (!exitThroughException) {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                writeToLog(logger, replacePlaceholders(this.exitMessage, invocation, returnValue, null, stopWatch.getTotalTimeMillis()));
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0157 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0172 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x018d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01a4 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x01af A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x01bb A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01d3 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x01e3 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0140 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.String replacePlaceholders(java.lang.String r6, org.aopalliance.intercept.MethodInvocation r7, @org.springframework.lang.Nullable java.lang.Object r8, @org.springframework.lang.Nullable java.lang.Throwable r9, long r10) {
        /*
            Method dump skipped, instructions count: 515
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.aop.interceptor.CustomizableTraceInterceptor.replacePlaceholders(java.lang.String, org.aopalliance.intercept.MethodInvocation, java.lang.Object, java.lang.Throwable, long):java.lang.String");
    }

    private static void appendReturnValue(MethodInvocation methodInvocation, Matcher matcher, StringBuilder output, @Nullable Object returnValue) {
        if (methodInvocation.getMethod().getReturnType() == Void.TYPE) {
            matcher.appendReplacement(output, "void");
        } else if (returnValue == null) {
            matcher.appendReplacement(output, "null");
        } else {
            matcher.appendReplacement(output, Matcher.quoteReplacement(returnValue.toString()));
        }
    }

    private static void appendArgumentTypes(MethodInvocation methodInvocation, Matcher matcher, StringBuilder output) {
        Class<?>[] argumentTypes = methodInvocation.getMethod().getParameterTypes();
        String[] argumentTypeShortNames = new String[argumentTypes.length];
        for (int i = 0; i < argumentTypeShortNames.length; i++) {
            argumentTypeShortNames[i] = ClassUtils.getShortName(argumentTypes[i]);
        }
        matcher.appendReplacement(output, Matcher.quoteReplacement(StringUtils.arrayToCommaDelimitedString(argumentTypeShortNames)));
    }

    private static void checkForInvalidPlaceholders(String message) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(message);
        while (matcher.find()) {
            String match = matcher.group();
            if (!ALLOWED_PLACEHOLDERS.contains(match)) {
                throw new IllegalArgumentException("Placeholder [" + match + "] is not valid");
            }
        }
    }
}
