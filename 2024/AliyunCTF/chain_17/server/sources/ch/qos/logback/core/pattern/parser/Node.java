package ch.qos.logback.core.pattern.parser;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/Node.class */
public class Node {
    static final int LITERAL = 0;
    static final int SIMPLE_KEYWORD = 1;
    static final int COMPOSITE_KEYWORD = 2;
    final int type;
    final Object value;
    Node next;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Node(int type) {
        this(type, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Node(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public Node getNext() {
        return this.next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node r = (Node) o;
        return this.type == r.type && (this.value == null ? r.value == null : this.value.equals(r.value)) && (this.next == null ? r.next == null : this.next.equals(r.next));
    }

    public int hashCode() {
        int result = this.type;
        return (31 * result) + (this.value != null ? this.value.hashCode() : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String printNext() {
        if (this.next != null) {
            return " -> " + String.valueOf(this.next);
        }
        return "";
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (this.type) {
            case 0:
                buf.append("LITERAL(" + String.valueOf(this.value) + ")");
                break;
            default:
                buf.append(super.toString());
                break;
        }
        buf.append(printNext());
        return buf.toString();
    }
}
