package org.yaml.snakeyaml.emitter;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.h2.engine.Constants;
import org.slf4j.Marker;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentEventsCollector;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.util.ArrayStack;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter.class */
public final class Emitter implements Emitable {
    public static final int MIN_INDENT = 1;
    public static final int MAX_INDENT = 10;
    private static final char[] SPACE = {' '};
    private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
    private static final Set<Character> INVALID_ANCHOR = new HashSet();
    private static final Map<Character, String> ESCAPE_REPLACEMENTS;
    private static final Map<String, String> DEFAULT_TAG_PREFIXES;
    private final Writer stream;
    private final ArrayStack<EmitterState> states;
    private EmitterState state;
    private final Queue<Event> events;
    private Event event;
    private final ArrayStack<Integer> indents;
    private Integer indent;
    private int flowLevel;
    private boolean rootContext;
    private boolean mappingContext;
    private boolean simpleKeyContext;
    private int column;
    private boolean whitespace;
    private boolean indention;
    private boolean openEnded;
    private final Boolean canonical;
    private final Boolean prettyFlow;
    private final boolean allowUnicode;
    private int bestIndent;
    private final int indicatorIndent;
    private final boolean indentWithIndicator;
    private int bestWidth;
    private final char[] bestLineBreak;
    private final boolean splitLines;
    private final int maxSimpleKeyLength;
    private final boolean emitComments;
    private Map<String, String> tagPrefixes;
    private String preparedAnchor;
    private String preparedTag;
    private ScalarAnalysis analysis;
    private DumperOptions.ScalarStyle style;
    private final CommentEventsCollector blockCommentsCollector;
    private final CommentEventsCollector inlineCommentsCollector;
    private static final Pattern HANDLE_FORMAT;

    static /* synthetic */ int access$2210(Emitter x0) {
        int i = x0.flowLevel;
        x0.flowLevel = i - 1;
        return i;
    }

    static {
        INVALID_ANCHOR.add('[');
        INVALID_ANCHOR.add(']');
        INVALID_ANCHOR.add('{');
        INVALID_ANCHOR.add('}');
        INVALID_ANCHOR.add(',');
        INVALID_ANCHOR.add('*');
        INVALID_ANCHOR.add('&');
        ESCAPE_REPLACEMENTS = new HashMap();
        ESCAPE_REPLACEMENTS.put((char) 0, CustomBooleanEditor.VALUE_0);
        ESCAPE_REPLACEMENTS.put((char) 7, "a");
        ESCAPE_REPLACEMENTS.put('\b', "b");
        ESCAPE_REPLACEMENTS.put('\t', "t");
        ESCAPE_REPLACEMENTS.put('\n', "n");
        ESCAPE_REPLACEMENTS.put((char) 11, "v");
        ESCAPE_REPLACEMENTS.put('\f', "f");
        ESCAPE_REPLACEMENTS.put('\r', "r");
        ESCAPE_REPLACEMENTS.put((char) 27, "e");
        ESCAPE_REPLACEMENTS.put('\"', "\"");
        ESCAPE_REPLACEMENTS.put('\\', StrPool.BACKSLASH);
        ESCAPE_REPLACEMENTS.put((char) 133, "N");
        ESCAPE_REPLACEMENTS.put((char) 160, "_");
        ESCAPE_REPLACEMENTS.put((char) 8232, "L");
        ESCAPE_REPLACEMENTS.put((char) 8233, "P");
        DEFAULT_TAG_PREFIXES = new LinkedHashMap();
        DEFAULT_TAG_PREFIXES.put("!", "!");
        DEFAULT_TAG_PREFIXES.put(Tag.PREFIX, "!!");
        HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
    }

    public Emitter(Writer stream, DumperOptions opts) {
        if (stream == null) {
            throw new NullPointerException("Writer must be provided.");
        }
        if (opts == null) {
            throw new NullPointerException("DumperOptions must be provided.");
        }
        this.stream = stream;
        this.states = new ArrayStack<>(100);
        this.state = new ExpectStreamStart();
        this.events = new ArrayDeque(100);
        this.event = null;
        this.indents = new ArrayStack<>(10);
        this.indent = null;
        this.flowLevel = 0;
        this.mappingContext = false;
        this.simpleKeyContext = false;
        this.column = 0;
        this.whitespace = true;
        this.indention = true;
        this.openEnded = false;
        this.canonical = Boolean.valueOf(opts.isCanonical());
        this.prettyFlow = Boolean.valueOf(opts.isPrettyFlow());
        this.allowUnicode = opts.isAllowUnicode();
        this.bestIndent = 2;
        if (opts.getIndent() > 1 && opts.getIndent() < 10) {
            this.bestIndent = opts.getIndent();
        }
        this.indicatorIndent = opts.getIndicatorIndent();
        this.indentWithIndicator = opts.getIndentWithIndicator();
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
        this.splitLines = opts.getSplitLines();
        this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
        this.emitComments = opts.isProcessComments();
        this.tagPrefixes = new LinkedHashMap();
        this.preparedAnchor = null;
        this.preparedTag = null;
        this.analysis = null;
        this.style = null;
        this.blockCommentsCollector = new CommentEventsCollector(this.events, CommentType.BLANK_LINE, CommentType.BLOCK);
        this.inlineCommentsCollector = new CommentEventsCollector(this.events, CommentType.IN_LINE);
    }

    @Override // org.yaml.snakeyaml.emitter.Emitable
    public void emit(Event event) throws IOException {
        this.events.add(event);
        while (!needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }

    private boolean needMoreEvents() {
        if (this.events.isEmpty()) {
            return true;
        }
        Iterator<Event> iter = this.events.iterator();
        Event next = iter.next();
        while (true) {
            Event event = next;
            if (event instanceof CommentEvent) {
                if (!iter.hasNext()) {
                    return true;
                }
                next = iter.next();
            } else {
                if (event instanceof DocumentStartEvent) {
                    return needEvents(iter, 1);
                }
                if (event instanceof SequenceStartEvent) {
                    return needEvents(iter, 2);
                }
                if (event instanceof MappingStartEvent) {
                    return needEvents(iter, 3);
                }
                if (event instanceof StreamStartEvent) {
                    return needEvents(iter, 2);
                }
                if (!(event instanceof StreamEndEvent) && this.emitComments) {
                    return needEvents(iter, 1);
                }
                return false;
            }
        }
    }

    private boolean needEvents(Iterator<Event> iter, int count) {
        int level = 0;
        int actualCount = 0;
        while (iter.hasNext()) {
            Event event = iter.next();
            if (!(event instanceof CommentEvent)) {
                actualCount++;
                if ((event instanceof DocumentStartEvent) || (event instanceof CollectionStartEvent)) {
                    level++;
                } else if ((event instanceof DocumentEndEvent) || (event instanceof CollectionEndEvent)) {
                    level--;
                } else if (event instanceof StreamEndEvent) {
                    level = -1;
                }
                if (level < 0) {
                    return false;
                }
            }
        }
        return actualCount < count;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void increaseIndent(boolean flow, boolean indentless) {
        this.indents.push(this.indent);
        if (this.indent == null) {
            if (flow) {
                this.indent = Integer.valueOf(this.bestIndent);
                return;
            } else {
                this.indent = 0;
                return;
            }
        }
        if (!indentless) {
            this.indent = Integer.valueOf(this.indent.intValue() + this.bestIndent);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectStreamStart.class */
    private class ExpectStreamStart implements EmitterState {
        private ExpectStreamStart() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (Emitter.this.event instanceof StreamStartEvent) {
                Emitter.this.writeStreamStart();
                Emitter.this.state = new ExpectFirstDocumentStart();
                return;
            }
            throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectNothing.class */
    public class ExpectNothing implements EmitterState {
        private ExpectNothing() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFirstDocumentStart.class */
    private class ExpectFirstDocumentStart implements EmitterState {
        private ExpectFirstDocumentStart() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            new ExpectDocumentStart(true).expect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectDocumentStart.class */
    public class ExpectDocumentStart implements EmitterState {
        private final boolean first;

        public ExpectDocumentStart(boolean first) {
            this.first = first;
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (Emitter.this.event instanceof DocumentStartEvent) {
                DocumentStartEvent ev = (DocumentStartEvent) Emitter.this.event;
                if ((ev.getVersion() != null || ev.getTags() != null) && Emitter.this.openEnded) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
                if (ev.getVersion() != null) {
                    String versionText = Emitter.this.prepareVersion(ev.getVersion());
                    Emitter.this.writeVersionDirective(versionText);
                }
                Emitter.this.tagPrefixes = new LinkedHashMap(Emitter.DEFAULT_TAG_PREFIXES);
                if (ev.getTags() != null) {
                    Set<String> handles = new TreeSet<>(ev.getTags().keySet());
                    for (String handle : handles) {
                        String prefix = ev.getTags().get(handle);
                        Emitter.this.tagPrefixes.put(prefix, handle);
                        String handleText = Emitter.this.prepareTagHandle(handle);
                        String prefixText = Emitter.this.prepareTagPrefix(prefix);
                        Emitter.this.writeTagDirective(handleText, prefixText);
                    }
                }
                boolean implicit = this.first && !ev.getExplicit() && !Emitter.this.canonical.booleanValue() && ev.getVersion() == null && (ev.getTags() == null || ev.getTags().isEmpty()) && !Emitter.this.checkEmptyDocument();
                if (!implicit) {
                    Emitter.this.writeIndent();
                    Emitter.this.writeIndicator("---", true, false, false);
                    if (Emitter.this.canonical.booleanValue()) {
                        Emitter.this.writeIndent();
                    }
                }
                Emitter.this.state = new ExpectDocumentRoot();
                return;
            }
            if (!(Emitter.this.event instanceof StreamEndEvent)) {
                if (Emitter.this.event instanceof CommentEvent) {
                    Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                    Emitter.this.writeBlockComment();
                    return;
                }
                throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
            }
            Emitter.this.writeStreamEnd();
            Emitter.this.state = new ExpectNothing();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectDocumentEnd.class */
    private class ExpectDocumentEnd implements EmitterState {
        private ExpectDocumentEnd() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.event instanceof DocumentEndEvent) {
                Emitter.this.writeIndent();
                if (((DocumentEndEvent) Emitter.this.event).getExplicit()) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
                Emitter.this.flushStream();
                Emitter.this.state = new ExpectDocumentStart(false);
                return;
            }
            throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectDocumentRoot.class */
    public class ExpectDocumentRoot implements EmitterState {
        private ExpectDocumentRoot() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                Emitter.this.writeBlockComment();
                if (Emitter.this.event instanceof DocumentEndEvent) {
                    new ExpectDocumentEnd().expect();
                    return;
                }
            }
            Emitter.this.states.push(new ExpectDocumentEnd());
            Emitter.this.expectNode(true, false, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void expectNode(boolean root, boolean mapping, boolean simpleKey) throws IOException {
        this.rootContext = root;
        this.mappingContext = mapping;
        this.simpleKeyContext = simpleKey;
        if (this.event instanceof AliasEvent) {
            expectAlias();
            return;
        }
        if ((this.event instanceof ScalarEvent) || (this.event instanceof CollectionStartEvent)) {
            processAnchor(BeanFactory.FACTORY_BEAN_PREFIX);
            processTag();
            if (this.event instanceof ScalarEvent) {
                expectScalar();
                return;
            }
            if (this.event instanceof SequenceStartEvent) {
                if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent) this.event).isFlow() || checkEmptySequence()) {
                    expectFlowSequence();
                    return;
                } else {
                    expectBlockSequence();
                    return;
                }
            }
            if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent) this.event).isFlow() || checkEmptyMapping()) {
                expectFlowMapping();
                return;
            } else {
                expectBlockMapping();
                return;
            }
        }
        throw new EmitterException("expected NodeEvent, but got " + this.event);
    }

    private void expectAlias() throws IOException {
        if (!(this.event instanceof AliasEvent)) {
            throw new EmitterException("Alias must be provided");
        }
        processAnchor("*");
        this.state = this.states.pop();
    }

    private void expectScalar() throws IOException {
        increaseIndent(true, false);
        processScalar();
        this.indent = this.indents.pop();
        this.state = this.states.pop();
    }

    private void expectFlowSequence() throws IOException {
        writeIndicator("[", true, true, false);
        this.flowLevel++;
        increaseIndent(true, false);
        if (this.prettyFlow.booleanValue()) {
            writeIndent();
        }
        this.state = new ExpectFirstFlowSequenceItem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFirstFlowSequenceItem.class */
    public class ExpectFirstFlowSequenceItem implements EmitterState {
        private ExpectFirstFlowSequenceItem() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (!(Emitter.this.event instanceof SequenceEndEvent)) {
                if (Emitter.this.event instanceof CommentEvent) {
                    Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                    Emitter.this.writeBlockComment();
                    return;
                }
                if (Emitter.this.canonical.booleanValue() || ((Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
                Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeInlineComments();
                return;
            }
            Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            Emitter.access$2210(Emitter.this);
            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = (EmitterState) Emitter.this.states.pop();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFlowSequenceItem.class */
    private class ExpectFlowSequenceItem implements EmitterState {
        private ExpectFlowSequenceItem() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (!(Emitter.this.event instanceof SequenceEndEvent)) {
                if (Emitter.this.event instanceof CommentEvent) {
                    Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                    return;
                }
                Emitter.this.writeIndicator(",", false, false, false);
                Emitter.this.writeBlockComment();
                if (Emitter.this.canonical.booleanValue() || ((Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
                Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeInlineComments();
                return;
            }
            Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            Emitter.access$2210(Emitter.this);
            if (!Emitter.this.canonical.booleanValue()) {
                if (Emitter.this.prettyFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
            } else {
                Emitter.this.writeIndicator(",", false, false, false);
                Emitter.this.writeIndent();
            }
            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            if (Emitter.this.prettyFlow.booleanValue()) {
                Emitter.this.writeIndent();
            }
            Emitter.this.state = (EmitterState) Emitter.this.states.pop();
        }
    }

    private void expectFlowMapping() throws IOException {
        writeIndicator(StrPool.DELIM_START, true, true, false);
        this.flowLevel++;
        increaseIndent(true, false);
        if (this.prettyFlow.booleanValue()) {
            writeIndent();
        }
        this.state = new ExpectFirstFlowMappingKey();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFirstFlowMappingKey.class */
    public class ExpectFirstFlowMappingKey implements EmitterState {
        private ExpectFirstFlowMappingKey() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (!(Emitter.this.event instanceof MappingEndEvent)) {
                if (Emitter.this.canonical.booleanValue() || ((Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                    return;
                } else {
                    Emitter.this.writeIndicator(CoreConstants.NA, true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, true, false);
                    return;
                }
            }
            Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            Emitter.access$2210(Emitter.this);
            Emitter.this.writeIndicator("}", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = (EmitterState) Emitter.this.states.pop();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFlowMappingKey.class */
    private class ExpectFlowMappingKey implements EmitterState {
        private ExpectFlowMappingKey() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (Emitter.this.event instanceof MappingEndEvent) {
                Emitter.this.indent = (Integer) Emitter.this.indents.pop();
                Emitter.access$2210(Emitter.this);
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                if (Emitter.this.prettyFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                Emitter.this.state = (EmitterState) Emitter.this.states.pop();
                return;
            }
            Emitter.this.writeIndicator(",", false, false, false);
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.canonical.booleanValue() || ((Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())) {
                Emitter.this.writeIndent();
            }
            if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                Emitter.this.expectNode(false, true, true);
            } else {
                Emitter.this.writeIndicator(CoreConstants.NA, true, false, false);
                Emitter.this.states.push(new ExpectFlowMappingValue());
                Emitter.this.expectNode(false, true, false);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFlowMappingSimpleValue.class */
    private class ExpectFlowMappingSimpleValue implements EmitterState {
        private ExpectFlowMappingSimpleValue() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFlowMappingValue.class */
    private class ExpectFlowMappingValue implements EmitterState {
        private ExpectFlowMappingValue() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow.booleanValue()) {
                Emitter.this.writeIndent();
            }
            Emitter.this.writeIndicator(":", true, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    private void expectBlockSequence() throws IOException {
        boolean indentless = this.mappingContext && !this.indention;
        increaseIndent(false, indentless);
        this.state = new ExpectFirstBlockSequenceItem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFirstBlockSequenceItem.class */
    public class ExpectFirstBlockSequenceItem implements EmitterState {
        private ExpectFirstBlockSequenceItem() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            new ExpectBlockSequenceItem(true).expect();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectBlockSequenceItem.class */
    private class ExpectBlockSequenceItem implements EmitterState {
        private final boolean first;

        public ExpectBlockSequenceItem(boolean first) {
            this.first = first;
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            if (this.first || !(Emitter.this.event instanceof SequenceEndEvent)) {
                if (Emitter.this.event instanceof CommentEvent) {
                    Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                    return;
                }
                Emitter.this.writeIndent();
                if (!Emitter.this.indentWithIndicator || this.first) {
                    Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
                }
                Emitter.this.writeIndicator("-", true, false, true);
                if (Emitter.this.indentWithIndicator && this.first) {
                    Emitter.this.indent = Integer.valueOf(Emitter.this.indent.intValue() + Emitter.this.indicatorIndent);
                }
                if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                    Emitter.this.increaseIndent(false, false);
                    Emitter.this.writeBlockComment();
                    if (Emitter.this.event instanceof ScalarEvent) {
                        Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent) Emitter.this.event).getValue());
                        if (!Emitter.this.analysis.isEmpty()) {
                            Emitter.this.writeIndent();
                        }
                    }
                    Emitter.this.indent = (Integer) Emitter.this.indents.pop();
                }
                Emitter.this.states.push(new ExpectBlockSequenceItem(false));
                Emitter.this.expectNode(false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                return;
            }
            Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            Emitter.this.state = (EmitterState) Emitter.this.states.pop();
        }
    }

    private void expectBlockMapping() throws IOException {
        increaseIndent(false, false);
        this.state = new ExpectFirstBlockMappingKey();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectFirstBlockMappingKey.class */
    public class ExpectFirstBlockMappingKey implements EmitterState {
        private ExpectFirstBlockMappingKey() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            new ExpectBlockMappingKey(true).expect();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectBlockMappingKey.class */
    private class ExpectBlockMappingKey implements EmitterState {
        private final boolean first;

        public ExpectBlockMappingKey(boolean first) {
            this.first = first;
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (!this.first && (Emitter.this.event instanceof MappingEndEvent)) {
                Emitter.this.indent = (Integer) Emitter.this.indents.pop();
                Emitter.this.state = (EmitterState) Emitter.this.states.pop();
                return;
            }
            Emitter.this.writeIndent();
            if (Emitter.this.checkSimpleKey()) {
                Emitter.this.states.push(new ExpectBlockMappingSimpleValue());
                Emitter.this.expectNode(false, true, true);
            } else {
                Emitter.this.writeIndicator(CoreConstants.NA, true, false, true);
                Emitter.this.states.push(new ExpectBlockMappingValue());
                Emitter.this.expectNode(false, true, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFoldedOrLiteral(Event event) {
        if (!event.is(Event.ID.Scalar)) {
            return false;
        }
        ScalarEvent scalarEvent = (ScalarEvent) event;
        DumperOptions.ScalarStyle style = scalarEvent.getScalarStyle();
        return style == DumperOptions.ScalarStyle.FOLDED || style == DumperOptions.ScalarStyle.LITERAL;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectBlockMappingSimpleValue.class */
    public class ExpectBlockMappingSimpleValue implements EmitterState {
        private ExpectBlockMappingSimpleValue() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && Emitter.this.writeInlineComments()) {
                Emitter.this.increaseIndent(true, false);
                Emitter.this.writeIndent();
                Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            }
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                Emitter.this.increaseIndent(true, false);
                Emitter.this.writeBlockComment();
                Emitter.this.writeIndent();
                Emitter.this.indent = (Integer) Emitter.this.indents.pop();
            }
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/emitter/Emitter$ExpectBlockMappingValue.class */
    public class ExpectBlockMappingValue implements EmitterState {
        private ExpectBlockMappingValue() {
        }

        @Override // org.yaml.snakeyaml.emitter.EmitterState
        public void expect() throws IOException {
            Emitter.this.writeIndent();
            Emitter.this.writeIndicator(":", true, false, true);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    private boolean checkEmptySequence() {
        return (this.event instanceof SequenceStartEvent) && !this.events.isEmpty() && (this.events.peek() instanceof SequenceEndEvent);
    }

    private boolean checkEmptyMapping() {
        return (this.event instanceof MappingStartEvent) && !this.events.isEmpty() && (this.events.peek() instanceof MappingEndEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkEmptyDocument() {
        if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty()) {
            return false;
        }
        Event event = this.events.peek();
        if (event instanceof ScalarEvent) {
            ScalarEvent e = (ScalarEvent) event;
            return e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e.getValue().length() == 0;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkSimpleKey() {
        int length = 0;
        if ((this.event instanceof NodeEvent) && ((NodeEvent) this.event).getAnchor() != null) {
            if (this.preparedAnchor == null) {
                this.preparedAnchor = prepareAnchor(((NodeEvent) this.event).getAnchor());
            }
            length = 0 + this.preparedAnchor.length();
        }
        String tag = null;
        if (this.event instanceof ScalarEvent) {
            tag = ((ScalarEvent) this.event).getTag();
        } else if (this.event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent) this.event).getTag();
        }
        if (tag != null) {
            if (this.preparedTag == null) {
                this.preparedTag = prepareTag(tag);
            }
            length += this.preparedTag.length();
        }
        if (this.event instanceof ScalarEvent) {
            if (this.analysis == null) {
                this.analysis = analyzeScalar(((ScalarEvent) this.event).getValue());
            }
            length += this.analysis.getScalar().length();
        }
        return length < this.maxSimpleKeyLength && ((this.event instanceof AliasEvent) || (!(!(this.event instanceof ScalarEvent) || this.analysis.isEmpty() || this.analysis.isMultiline()) || checkEmptySequence() || checkEmptyMapping()));
    }

    private void processAnchor(String indicator) throws IOException {
        NodeEvent ev = (NodeEvent) this.event;
        if (ev.getAnchor() == null) {
            this.preparedAnchor = null;
            return;
        }
        if (this.preparedAnchor == null) {
            this.preparedAnchor = prepareAnchor(ev.getAnchor());
        }
        writeIndicator(indicator + this.preparedAnchor, true, false, false);
        this.preparedAnchor = null;
    }

    private void processTag() throws IOException {
        String tag;
        if (this.event instanceof ScalarEvent) {
            ScalarEvent ev = (ScalarEvent) this.event;
            tag = ev.getTag();
            if (this.style == null) {
                this.style = chooseScalarStyle();
            }
            if ((!this.canonical.booleanValue() || tag == null) && ((this.style == null && ev.getImplicit().canOmitTagInPlainScalar()) || (this.style != null && ev.getImplicit().canOmitTagInNonPlainScalar()))) {
                this.preparedTag = null;
                return;
            } else if (ev.getImplicit().canOmitTagInPlainScalar() && tag == null) {
                tag = "!";
                this.preparedTag = null;
            }
        } else {
            CollectionStartEvent ev2 = (CollectionStartEvent) this.event;
            tag = ev2.getTag();
            if ((!this.canonical.booleanValue() || tag == null) && ev2.getImplicit()) {
                this.preparedTag = null;
                return;
            }
        }
        if (tag == null) {
            throw new EmitterException("tag is not specified");
        }
        if (this.preparedTag == null) {
            this.preparedTag = prepareTag(tag);
        }
        writeIndicator(this.preparedTag, true, false, false);
        this.preparedTag = null;
    }

    private DumperOptions.ScalarStyle chooseScalarStyle() {
        ScalarEvent ev = (ScalarEvent) this.event;
        if (this.analysis == null) {
            this.analysis = analyzeScalar(ev.getValue());
        }
        if ((!ev.isPlain() && ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED) || this.canonical.booleanValue()) {
            return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        }
        if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (!this.simpleKeyContext || (!this.analysis.isEmpty() && !this.analysis.isMultiline()))) {
            if (this.flowLevel == 0 || !this.analysis.isAllowFlowPlain()) {
                if (this.flowLevel == 0 && this.analysis.isAllowBlockPlain()) {
                    return null;
                }
            } else {
                return null;
            }
        }
        if (!ev.isPlain() && ((ev.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || ev.getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) && this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock())) {
            return ev.getScalarStyle();
        }
        if ((ev.isPlain() || ev.getScalarStyle() == DumperOptions.ScalarStyle.SINGLE_QUOTED) && this.analysis.isAllowSingleQuoted() && (!this.simpleKeyContext || !this.analysis.isMultiline())) {
            return DumperOptions.ScalarStyle.SINGLE_QUOTED;
        }
        return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
    }

    private void processScalar() throws IOException {
        ScalarEvent ev = (ScalarEvent) this.event;
        if (this.analysis == null) {
            this.analysis = analyzeScalar(ev.getValue());
        }
        if (this.style == null) {
            this.style = chooseScalarStyle();
        }
        boolean split = !this.simpleKeyContext && this.splitLines;
        if (this.style == null) {
            writePlain(this.analysis.getScalar(), split);
        } else {
            switch (this.style) {
                case DOUBLE_QUOTED:
                    writeDoubleQuoted(this.analysis.getScalar(), split);
                    break;
                case SINGLE_QUOTED:
                    writeSingleQuoted(this.analysis.getScalar(), split);
                    break;
                case FOLDED:
                    writeFolded(this.analysis.getScalar(), split);
                    break;
                case LITERAL:
                    writeLiteral(this.analysis.getScalar());
                    break;
                default:
                    throw new YAMLException("Unexpected style: " + this.style);
            }
        }
        this.analysis = null;
        this.style = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String prepareVersion(DumperOptions.Version version) {
        if (version.major() != 1) {
            throw new EmitterException("unsupported YAML version: " + version);
        }
        return version.getRepresentation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String prepareTagHandle(String handle) {
        if (handle.length() == 0) {
            throw new EmitterException("tag handle must not be empty");
        }
        if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!') {
            throw new EmitterException("tag handle must start and end with '!': " + handle);
        }
        if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + handle);
        }
        return handle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String prepareTagPrefix(String prefix) {
        if (prefix.length() == 0) {
            throw new EmitterException("tag prefix must not be empty");
        }
        StringBuilder chunks = new StringBuilder();
        int end = 0;
        if (prefix.charAt(0) == '!') {
            end = 1;
        }
        while (end < prefix.length()) {
            end++;
        }
        if (0 < end) {
            chunks.append((CharSequence) prefix, 0, end);
        }
        return chunks.toString();
    }

    private String prepareTag(String tag) {
        if (tag.length() == 0) {
            throw new EmitterException("tag must not be empty");
        }
        if ("!".equals(tag)) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (String prefix : this.tagPrefixes.keySet()) {
            if (tag.startsWith(prefix) && ("!".equals(prefix) || prefix.length() < tag.length())) {
                handle = prefix;
            }
        }
        if (handle != null) {
            suffix = tag.substring(handle.length());
            handle = this.tagPrefixes.get(handle);
        }
        int end = suffix.length();
        String suffixText = end > 0 ? suffix.substring(0, end) : "";
        if (handle != null) {
            return handle + suffixText;
        }
        return "!<" + suffixText + ">";
    }

    static String prepareAnchor(String anchor) {
        if (anchor.length() == 0) {
            throw new EmitterException("anchor must not be empty");
        }
        for (Character invalid : INVALID_ANCHOR) {
            if (anchor.indexOf(invalid.charValue()) > -1) {
                throw new EmitterException("Invalid character '" + invalid + "' in the anchor: " + anchor);
            }
        }
        Matcher matcher = SPACES_PATTERN.matcher(anchor);
        if (matcher.find()) {
            throw new EmitterException("Anchor may not contain spaces: " + anchor);
        }
        return anchor;
    }

    private static boolean hasLeadingZero(String scalar) {
        if (scalar.length() > 1 && scalar.charAt(0) == '0') {
            for (int i = 1; i < scalar.length(); i++) {
                char ch2 = scalar.charAt(i);
                boolean isDigitOrUnderscore = (ch2 >= '0' && ch2 <= '9') || ch2 == '_';
                if (!isDigitOrUnderscore) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ScalarAnalysis analyzeScalar(String scalar) {
        int nextIndex;
        if (scalar.length() == 0) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, false);
        }
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;
        boolean leadingZeroNumber = hasLeadingZero(scalar);
        boolean leadingSpace = false;
        boolean leadingBreak = false;
        boolean trailingSpace = false;
        boolean trailingBreak = false;
        boolean breakSpace = false;
        boolean spaceBreak = false;
        if (scalar.startsWith("---") || scalar.startsWith("...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        boolean preceededByWhitespace = true;
        boolean followedByWhitespace = scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has(scalar.codePointAt(1));
        boolean previousSpace = false;
        boolean previousBreak = false;
        int index = 0;
        while (index < scalar.length()) {
            int c = scalar.codePointAt(index);
            if (index == 0) {
                if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (c == 63 || c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 45 && followedByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            } else {
                if (",?[]{}".indexOf(c) != -1) {
                    flowIndicators = true;
                }
                if (c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 35 && preceededByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            boolean isLineBreak = Constant.LINEBR.has(c);
            if (isLineBreak) {
                lineBreaks = true;
            }
            if (c != 10 && (32 > c || c > 126)) {
                if (c == 133 || ((c >= 160 && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111)))) {
                    if (!this.allowUnicode) {
                        specialCharacters = true;
                    }
                } else {
                    specialCharacters = true;
                }
            }
            if (c == 32) {
                if (index == 0) {
                    leadingSpace = true;
                }
                if (index == scalar.length() - 1) {
                    trailingSpace = true;
                }
                if (previousBreak) {
                    breakSpace = true;
                }
                previousSpace = true;
                previousBreak = false;
            } else if (isLineBreak) {
                if (index == 0) {
                    leadingBreak = true;
                }
                if (index == scalar.length() - 1) {
                    trailingBreak = true;
                }
                if (previousSpace) {
                    spaceBreak = true;
                }
                previousSpace = false;
                previousBreak = true;
            } else {
                previousSpace = false;
                previousBreak = false;
            }
            index += Character.charCount(c);
            preceededByWhitespace = Constant.NULL_BL_T.has(c) || isLineBreak;
            followedByWhitespace = true;
            if (index + 1 < scalar.length() && (nextIndex = index + Character.charCount(scalar.codePointAt(index))) < scalar.length()) {
                followedByWhitespace = Constant.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak;
            }
        }
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        boolean allowBlock = true;
        if (leadingSpace || leadingBreak || trailingSpace || trailingBreak || leadingZeroNumber) {
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (trailingSpace) {
            allowBlock = false;
        }
        if (breakSpace) {
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (spaceBreak || specialCharacters) {
            allowBlock = false;
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (lineBreaks) {
            allowFlowPlain = false;
        }
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        if (blockIndicators) {
            allowBlockPlain = false;
        }
        return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
    }

    void flushStream() throws IOException {
        this.stream.flush();
    }

    void writeStreamStart() {
    }

    void writeStreamEnd() throws IOException {
        flushStream();
    }

    void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
        if (!this.whitespace && needWhitespace) {
            this.column++;
            this.stream.write(SPACE);
        }
        this.whitespace = whitespace;
        this.indention = this.indention && indentation;
        this.column += indicator.length();
        this.openEnded = false;
        this.stream.write(indicator);
    }

    void writeIndent() throws IOException {
        int indent;
        if (this.indent != null) {
            indent = this.indent.intValue();
        } else {
            indent = 0;
        }
        if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
            writeLineBreak(null);
        }
        writeWhitespace(indent - this.column);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeWhitespace(int length) throws IOException {
        if (length <= 0) {
            return;
        }
        this.whitespace = true;
        char[] data = new char[length];
        for (int i = 0; i < data.length; i++) {
            data[i] = ' ';
        }
        this.column += length;
        this.stream.write(data);
    }

    private void writeLineBreak(String data) throws IOException {
        this.whitespace = true;
        this.indention = true;
        this.column = 0;
        if (data == null) {
            this.stream.write(this.bestLineBreak);
        } else {
            this.stream.write(data);
        }
    }

    void writeVersionDirective(String versionText) throws IOException {
        this.stream.write("%YAML ");
        this.stream.write(versionText);
        writeLineBreak(null);
    }

    void writeTagDirective(String handleText, String prefixText) throws IOException {
        this.stream.write("%TAG ");
        this.stream.write(handleText);
        this.stream.write(SPACE);
        this.stream.write(prefixText);
        writeLineBreak(null);
    }

    private void writeSingleQuoted(String text, boolean split) throws IOException {
        writeIndicator("'", true, false, false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); end++) {
            char ch2 = 0;
            if (end < text.length()) {
                ch2 = text.charAt(end);
            }
            if (spaces) {
                if (ch2 == 0 || ch2 != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
                        writeIndent();
                    } else {
                        int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (ch2 == 0 || Constant.LINEBR.hasNo(ch2)) {
                    if (text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    writeIndent();
                    start = end;
                }
            } else if (Constant.LINEBR.has(ch2, "�� '") && start < end) {
                int len2 = end - start;
                this.column += len2;
                this.stream.write(text, start, len2);
                start = end;
            }
            if (ch2 == '\'') {
                this.column += 2;
                this.stream.write(Constants.CLUSTERING_DISABLED);
                start = end + 1;
            }
            if (ch2 != 0) {
                spaces = ch2 == ' ';
                breaks = Constant.LINEBR.has(ch2);
            }
        }
        writeIndicator("'", false, false, false);
    }

    private void writeDoubleQuoted(String text, boolean split) throws IOException {
        int codePoint;
        String data;
        String data2;
        writeIndicator("\"", true, false, false);
        int start = 0;
        int end = 0;
        while (end <= text.length()) {
            Character ch2 = null;
            if (end < text.length()) {
                ch2 = Character.valueOf(text.charAt(end));
            }
            if (ch2 == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(ch2.charValue()) != -1 || ' ' > ch2.charValue() || ch2.charValue() > '~') {
                if (start < end) {
                    int len = end - start;
                    this.column += len;
                    this.stream.write(text, start, len);
                    start = end;
                }
                if (ch2 != null) {
                    if (ESCAPE_REPLACEMENTS.containsKey(ch2)) {
                        data = StrPool.BACKSLASH + ESCAPE_REPLACEMENTS.get(ch2);
                    } else {
                        if (Character.isHighSurrogate(ch2.charValue()) && end + 1 < text.length()) {
                            char ch22 = text.charAt(end + 1);
                            codePoint = Character.toCodePoint(ch2.charValue(), ch22);
                        } else {
                            codePoint = ch2.charValue();
                        }
                        if (this.allowUnicode && StreamReader.isPrintable(codePoint)) {
                            data = String.valueOf(Character.toChars(codePoint));
                            if (Character.charCount(codePoint) == 2) {
                                end++;
                            }
                        } else if (ch2.charValue() <= 255) {
                            String s = CustomBooleanEditor.VALUE_0 + Integer.toString(ch2.charValue(), 16);
                            data = "\\x" + s.substring(s.length() - 2);
                        } else if (Character.charCount(codePoint) == 2) {
                            end++;
                            String s2 = "000" + Long.toHexString(codePoint);
                            data = "\\U" + s2.substring(s2.length() - 8);
                        } else {
                            String s3 = "000" + Integer.toString(ch2.charValue(), 16);
                            data = "\\u" + s3.substring(s3.length() - 4);
                        }
                    }
                    this.column += data.length();
                    this.stream.write(data);
                    start = end + 1;
                }
            }
            if (0 < end && end < text.length() - 1 && ((ch2.charValue() == ' ' || start >= end) && this.column + (end - start) > this.bestWidth && split)) {
                if (start >= end) {
                    data2 = StrPool.BACKSLASH;
                } else {
                    data2 = text.substring(start, end) + StrPool.BACKSLASH;
                }
                if (start < end) {
                    start = end;
                }
                this.column += data2.length();
                this.stream.write(data2);
                writeIndent();
                this.whitespace = false;
                this.indention = false;
                if (text.charAt(start) == ' ') {
                    this.column += StrPool.BACKSLASH.length();
                    this.stream.write(StrPool.BACKSLASH);
                }
            }
            end++;
        }
        writeIndicator("\"", false, false, false);
    }

    private boolean writeCommentLines(List<CommentLine> commentLines) throws IOException {
        boolean wroteComment = false;
        if (this.emitComments) {
            int indentColumns = 0;
            boolean firstComment = true;
            for (CommentLine commentLine : commentLines) {
                if (commentLine.getCommentType() != CommentType.BLANK_LINE) {
                    if (firstComment) {
                        firstComment = false;
                        writeIndicator("#", commentLine.getCommentType() == CommentType.IN_LINE, false, false);
                        indentColumns = this.column > 0 ? this.column - 1 : 0;
                    } else {
                        writeWhitespace(indentColumns);
                        writeIndicator("#", false, false, false);
                    }
                    this.stream.write(commentLine.getValue());
                    writeLineBreak(null);
                } else {
                    writeLineBreak(null);
                    writeIndent();
                }
                wroteComment = true;
            }
        }
        return wroteComment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeBlockComment() throws IOException {
        if (!this.blockCommentsCollector.isEmpty()) {
            writeIndent();
            writeCommentLines(this.blockCommentsCollector.consume());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean writeInlineComments() throws IOException {
        return writeCommentLines(this.inlineCommentsCollector.consume());
    }

    private String determineBlockHints(String text) {
        StringBuilder hints = new StringBuilder();
        if (Constant.LINEBR.has(text.charAt(0), CharSequenceUtil.SPACE)) {
            hints.append(this.bestIndent);
        }
        char ch1 = text.charAt(text.length() - 1);
        if (Constant.LINEBR.hasNo(ch1)) {
            hints.append("-");
        } else if (text.length() == 1 || Constant.LINEBR.has(text.charAt(text.length() - 2))) {
            hints.append(Marker.ANY_NON_NULL_MARKER);
        }
        return hints.toString();
    }

    void writeFolded(String text, boolean split) throws IOException {
        String hints = determineBlockHints(text);
        writeIndicator(">" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        if (!writeInlineComments()) {
            writeLineBreak(null);
        }
        boolean leadingSpace = true;
        boolean spaces = false;
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); end++) {
            char ch2 = 0;
            if (end < text.length()) {
                ch2 = text.charAt(end);
            }
            if (breaks) {
                if (ch2 == 0 || Constant.LINEBR.hasNo(ch2)) {
                    if (!leadingSpace && ch2 != 0 && ch2 != ' ' && text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    leadingSpace = ch2 == ' ';
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch2 != 0) {
                        writeIndent();
                    }
                    start = end;
                }
            } else if (spaces) {
                if (ch2 != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        writeIndent();
                    } else {
                        int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (Constant.LINEBR.has(ch2, "�� ")) {
                int len2 = end - start;
                this.column += len2;
                this.stream.write(text, start, len2);
                if (ch2 == 0) {
                    writeLineBreak(null);
                }
                start = end;
            }
            if (ch2 != 0) {
                breaks = Constant.LINEBR.has(ch2);
                spaces = ch2 == ' ';
            }
        }
    }

    void writeLiteral(String text) throws IOException {
        String hints = determineBlockHints(text);
        writeIndicator("|" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        if (!writeInlineComments()) {
            writeLineBreak(null);
        }
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); end++) {
            char ch2 = 0;
            if (end < text.length()) {
                ch2 = text.charAt(end);
            }
            if (breaks) {
                if (ch2 == 0 || Constant.LINEBR.hasNo(ch2)) {
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch2 != 0) {
                        writeIndent();
                    }
                    start = end;
                }
            } else if (ch2 == 0 || Constant.LINEBR.has(ch2)) {
                this.stream.write(text, start, end - start);
                if (ch2 == 0) {
                    writeLineBreak(null);
                }
                start = end;
            }
            if (ch2 != 0) {
                breaks = Constant.LINEBR.has(ch2);
            }
        }
    }

    void writePlain(String text, boolean split) throws IOException {
        if (this.rootContext) {
            this.openEnded = true;
        }
        if (text.length() == 0) {
            return;
        }
        if (!this.whitespace) {
            this.column++;
            this.stream.write(SPACE);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); end++) {
            char ch2 = 0;
            if (end < text.length()) {
                ch2 = text.charAt(end);
            }
            if (spaces) {
                if (ch2 != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        writeIndent();
                        this.whitespace = false;
                        this.indention = false;
                    } else {
                        int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (Constant.LINEBR.hasNo(ch2)) {
                    if (text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    writeIndent();
                    this.whitespace = false;
                    this.indention = false;
                    start = end;
                }
            } else if (Constant.LINEBR.has(ch2, "�� ")) {
                int len2 = end - start;
                this.column += len2;
                this.stream.write(text, start, len2);
                start = end;
            }
            if (ch2 != 0) {
                spaces = ch2 == ' ';
                breaks = Constant.LINEBR.has(ch2);
            }
        }
    }
}
