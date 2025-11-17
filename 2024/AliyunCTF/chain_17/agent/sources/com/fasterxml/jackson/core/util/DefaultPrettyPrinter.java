package com.fasterxml.jackson.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import java.io.IOException;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/DefaultPrettyPrinter.class */
public class DefaultPrettyPrinter implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable {
    private static final long serialVersionUID = 1;
    public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(CharSequenceUtil.SPACE);
    protected Indenter _arrayIndenter;
    protected Indenter _objectIndenter;
    protected final SerializableString _rootSeparator;
    protected boolean _spacesInObjectEntries;
    protected transient int _nesting;
    protected Separators _separators;
    protected String _objectFieldValueSeparatorWithSpaces;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/DefaultPrettyPrinter$Indenter.class */
    public interface Indenter {
        void writeIndentation(JsonGenerator jsonGenerator, int i) throws IOException;

        boolean isInline();
    }

    public DefaultPrettyPrinter() {
        this(DEFAULT_ROOT_VALUE_SEPARATOR);
    }

    public DefaultPrettyPrinter(String rootSeparator) {
        this(rootSeparator == null ? null : new SerializedString(rootSeparator));
    }

    public DefaultPrettyPrinter(SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        this._spacesInObjectEntries = true;
        this._rootSeparator = rootSeparator;
        withSeparators(DEFAULT_SEPARATORS);
    }

    public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
        this(base, base._rootSeparator);
    }

    public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        this._spacesInObjectEntries = true;
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
        this._separators = base._separators;
        this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
        this._rootSeparator = rootSeparator;
    }

    public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
        if (this._rootSeparator == rootSeparator || (rootSeparator != null && rootSeparator.equals(this._rootSeparator))) {
            return this;
        }
        return new DefaultPrettyPrinter(this, rootSeparator);
    }

    public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
        return withRootSeparator(rootSeparator == null ? null : new SerializedString(rootSeparator));
    }

    public void indentArraysWith(Indenter i) {
        this._arrayIndenter = i == null ? NopIndenter.instance : i;
    }

    public void indentObjectsWith(Indenter i) {
        this._objectIndenter = i == null ? NopIndenter.instance : i;
    }

    public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._arrayIndenter == i) {
            return this;
        }
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._arrayIndenter = i;
        return pp;
    }

    public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._objectIndenter == i) {
            return this;
        }
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._objectIndenter = i;
        return pp;
    }

    public DefaultPrettyPrinter withSpacesInObjectEntries() {
        return _withSpaces(true);
    }

    public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
        return _withSpaces(false);
    }

    protected DefaultPrettyPrinter _withSpaces(boolean state) {
        if (this._spacesInObjectEntries == state) {
            return this;
        }
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._spacesInObjectEntries = state;
        return pp;
    }

    public DefaultPrettyPrinter withSeparators(Separators separators) {
        this._separators = separators;
        this._objectFieldValueSeparatorWithSpaces = CharSequenceUtil.SPACE + separators.getObjectFieldValueSeparator() + CharSequenceUtil.SPACE;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.core.util.Instantiatable
    public DefaultPrettyPrinter createInstance() {
        if (getClass() != DefaultPrettyPrinter.class) {
            throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName() + " does not override method; it has to");
        }
        return new DefaultPrettyPrinter(this);
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeRootValueSeparator(JsonGenerator g) throws IOException {
        if (this._rootSeparator != null) {
            g.writeRaw(this._rootSeparator);
        }
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeStartObject(JsonGenerator g) throws IOException {
        g.writeRaw('{');
        if (!this._objectIndenter.isInline()) {
            this._nesting++;
        }
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void beforeObjectEntries(JsonGenerator g) throws IOException {
        this._objectIndenter.writeIndentation(g, this._nesting);
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        if (this._spacesInObjectEntries) {
            g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
        } else {
            g.writeRaw(this._separators.getObjectFieldValueSeparator());
        }
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._separators.getObjectEntrySeparator());
        this._objectIndenter.writeIndentation(g, this._nesting);
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        if (!this._objectIndenter.isInline()) {
            this._nesting--;
        }
        if (nrOfEntries > 0) {
            this._objectIndenter.writeIndentation(g, this._nesting);
        } else {
            g.writeRaw(' ');
        }
        g.writeRaw('}');
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeStartArray(JsonGenerator g) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            this._nesting++;
        }
        g.writeRaw('[');
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void beforeArrayValues(JsonGenerator g) throws IOException {
        this._arrayIndenter.writeIndentation(g, this._nesting);
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._separators.getArrayValueSeparator());
        this._arrayIndenter.writeIndentation(g, this._nesting);
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            this._nesting--;
        }
        if (nrOfValues > 0) {
            this._arrayIndenter.writeIndentation(g, this._nesting);
        } else {
            g.writeRaw(' ');
        }
        g.writeRaw(']');
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/DefaultPrettyPrinter$NopIndenter.class */
    public static class NopIndenter implements Indenter, Serializable {
        public static final NopIndenter instance = new NopIndenter();

        @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
        public void writeIndentation(JsonGenerator g, int level) throws IOException {
        }

        @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
        public boolean isInline() {
            return true;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/DefaultPrettyPrinter$FixedSpaceIndenter.class */
    public static class FixedSpaceIndenter extends NopIndenter {
        public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();

        @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter, com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
        public void writeIndentation(JsonGenerator g, int level) throws IOException {
            g.writeRaw(' ');
        }

        @Override // com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter, com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter
        public boolean isInline() {
            return true;
        }
    }
}
