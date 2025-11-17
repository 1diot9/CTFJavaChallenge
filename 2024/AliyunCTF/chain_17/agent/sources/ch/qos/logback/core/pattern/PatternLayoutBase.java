package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/PatternLayoutBase.class */
public abstract class PatternLayoutBase<E> extends LayoutBase<E> {
    static final int INTIAL_STRING_BUILDER_SIZE = 256;
    Converter<E> head;
    String pattern;
    protected PostCompileProcessor<E> postCompileProcessor;
    Map<String, String> instanceConverterMap = new HashMap();
    protected boolean outputPatternAsHeader = false;

    public abstract Map<String, String> getDefaultConverterMap();

    public Map<String, String> getEffectiveConverterMap() {
        Map<String, String> contextMap;
        Map<String, String> effectiveMap = new HashMap<>();
        Map<String, String> defaultMap = getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }
        Context context = getContext();
        if (context != null && (contextMap = (Map) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY)) != null) {
            effectiveMap.putAll(contextMap);
        }
        effectiveMap.putAll(this.instanceConverterMap);
        return effectiveMap;
    }

    @Override // ch.qos.logback.core.LayoutBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.pattern == null || this.pattern.length() == 0) {
            addError("Empty or null pattern.");
            return;
        }
        try {
            Parser<E> p = new Parser<>(this.pattern);
            if (getContext() != null) {
                p.setContext(getContext());
            }
            Node t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            if (this.postCompileProcessor != null) {
                this.postCompileProcessor.process(this.context, this.head);
            }
            ConverterUtil.setContextForConverters(getContext(), this.head);
            ConverterUtil.startConverters(this.head);
            super.start();
        } catch (ScanException sce) {
            StatusManager sm = getContext().getStatusManager();
            sm.add(new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, sce));
        }
    }

    public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor) {
        this.postCompileProcessor = postCompileProcessor;
    }

    protected void setContextForConverters(Converter<E> head) {
        ConverterUtil.setContextForConverters(getContext(), head);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String writeLoopOnConverters(E event) {
        StringBuilder strBuilder = new StringBuilder(256);
        Converter<E> converter = this.head;
        while (true) {
            Converter<E> c = converter;
            if (c != null) {
                c.write(strBuilder, event);
                converter = c.getNext();
            } else {
                return strBuilder.toString();
            }
        }
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        return getClass().getName() + "(\"" + getPattern() + "\")";
    }

    public Map<String, String> getInstanceConverterMap() {
        return this.instanceConverterMap;
    }

    protected String getPresentationHeaderPrefix() {
        return "";
    }

    public boolean isOutputPatternAsHeader() {
        return this.outputPatternAsHeader;
    }

    public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
        this.outputPatternAsHeader = outputPatternAsHeader;
    }

    @Override // ch.qos.logback.core.LayoutBase, ch.qos.logback.core.Layout
    public String getPresentationHeader() {
        if (this.outputPatternAsHeader) {
            return getPresentationHeaderPrefix() + this.pattern;
        }
        return super.getPresentationHeader();
    }
}
