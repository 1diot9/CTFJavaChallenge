package org.springframework.beans.factory.parsing;

import java.util.ArrayDeque;
import java.util.Iterator;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ParseState.class */
public final class ParseState {
    private final ArrayDeque<Entry> state;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ParseState$Entry.class */
    public interface Entry {
    }

    public ParseState() {
        this.state = new ArrayDeque<>();
    }

    private ParseState(ParseState other) {
        this.state = other.state.clone();
    }

    public void push(Entry entry) {
        this.state.push(entry);
    }

    public void pop() {
        this.state.pop();
    }

    @Nullable
    public Entry peek() {
        return this.state.peek();
    }

    public ParseState snapshot() {
        return new ParseState(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        int i = 0;
        Iterator<Entry> it = this.state.iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            if (i > 0) {
                sb.append('\n');
                sb.append("\t".repeat(i));
                sb.append("-> ");
            }
            sb.append(entry);
            i++;
        }
        return sb.toString();
    }
}
