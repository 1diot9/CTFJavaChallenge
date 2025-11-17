package ch.qos.logback.classic.html;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/html/HTMLLayout.class */
public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
    IThrowableRenderer<ILoggingEvent> throwableRenderer;

    /*  JADX ERROR: Failed to decode insn: 0x0014: MOVE_MULTI, method: ch.qos.logback.classic.html.HTMLLayout.doLayout(ch.qos.logback.classic.spi.ILoggingEvent):java.lang.String
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[8]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:118)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    @Override // ch.qos.logback.core.Layout
    public java.lang.String doLayout(ch.qos.logback.classic.spi.ILoggingEvent r9) {
        /*
            r8 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            r10 = r0
            r0 = r8
            r1 = r10
            r0.startNewTableIfLimitReached(r1)
            r0 = 1
            r11 = r0
            r0 = r8
            r1 = r0
            long r1 = r1.counter
            // decode failed: arraycopy: source index -1 out of bounds for object array[8]
            r2 = 1
            long r1 = r1 + r2
            r0.counter = r1
            r0 = 1
            long r-1 = r-1 & r0
            r0 = 0
            int r-1 = (r-1 > r0 ? 1 : (r-1 == r0 ? 0 : -1))
            if (r-1 != 0) goto L23
            r-1 = 0
            r11 = r-1
            r-1 = r9
            r-1.getLevel()
            r-1.toString()
            r-1.toLowerCase()
            r12 = r-1
            r-1 = r10
            java.lang.String r0 = ch.qos.logback.core.CoreConstants.LINE_SEPARATOR
            r-1.append(r0)
            r-1 = r10
            java.lang.String r0 = "<tr class=\""
            r-1.append(r0)
            r-1 = r10
            r0 = r12
            r-1.append(r0)
            r-1 = r11
            if (r-1 == 0) goto L55
            r-1 = r10
            java.lang.String r0 = " odd\">"
            r-1.append(r0)
            goto L5c
            r-1 = r10
            java.lang.String r0 = " even\">"
            r-1.append(r0)
            r-1 = r10
            java.lang.String r0 = ch.qos.logback.core.CoreConstants.LINE_SEPARATOR
            r-1.append(r0)
            r-1 = r8
            ch.qos.logback.core.pattern.Converter<E> r-1 = r-1.head
            r13 = r-1
            r-1 = r13
            if (r-1 == 0) goto L81
            r-1 = r8
            r0 = r10
            r1 = r13
            r2 = r9
            r-1.appendEventToBuffer(r0, r1, r2)
            r-1 = r13
            r-1.getNext()
            r13 = r-1
            goto L6a
            r-1 = r10
            java.lang.String r0 = "</tr>"
            r-1.append(r0)
            r-1 = r10
            java.lang.String r0 = ch.qos.logback.core.CoreConstants.LINE_SEPARATOR
            r-1.append(r0)
            r-1 = r9
            r-1.getThrowableProxy()
            if (r-1 == 0) goto La4
            r-1 = r8
            ch.qos.logback.core.html.IThrowableRenderer<ch.qos.logback.classic.spi.ILoggingEvent> r-1 = r-1.throwableRenderer
            r0 = r10
            r1 = r9
            r-1.render(r0, r1)
            r-1 = r10
            r-1.toString()
            return r-1
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.html.HTMLLayout.doLayout(ch.qos.logback.classic.spi.ILoggingEvent):java.lang.String");
    }

    public HTMLLayout() {
        this.pattern = DEFAULT_CONVERSION_PATTERN;
        this.throwableRenderer = new DefaultThrowableRenderer();
        this.cssBuilder = new DefaultCssBuilder();
    }

    @Override // ch.qos.logback.core.html.HTMLLayoutBase, ch.qos.logback.core.LayoutBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errorCount = 0;
        if (this.throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            errorCount = 0 + 1;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.html.HTMLLayoutBase
    protected Map<String, String> getDefaultConverterMap() {
        return PatternLayout.DEFAULT_CONVERTER_MAP;
    }

    private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td class=\"");
        buf.append(computeConverterName(c));
        buf.append("\">");
        buf.append(Transform.escapeTags(c.convert(event)));
        buf.append("</td>");
        buf.append(CoreConstants.LINE_SEPARATOR);
    }

    public IThrowableRenderer<ILoggingEvent> getThrowableRenderer() {
        return this.throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.html.HTMLLayoutBase
    public String computeConverterName(Converter<ILoggingEvent> c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter) c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            }
            return "MDC";
        }
        return super.computeConverterName(c);
    }
}
