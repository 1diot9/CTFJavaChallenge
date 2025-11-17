package ch.qos.logback.core.subst;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/subst/Node.class */
public class Node {
    Type type;
    Object payload;
    Object defaultPart;
    Node next;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/subst/Node$Type.class */
    public enum Type {
        LITERAL,
        VARIABLE
    }

    public Node(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Node(Type type, Object payload, Object defaultPart) {
        this.type = type;
        this.payload = payload;
        this.defaultPart = defaultPart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void append(Node newNode) {
        if (newNode == null) {
            return;
        }
        Node node = this;
        while (true) {
            Node n = node;
            if (n.next == null) {
                n.next = newNode;
                return;
            }
            node = n.next;
        }
    }

    public String toString() {
        switch (this.type) {
            case LITERAL:
                return "Node{type=" + String.valueOf(this.type) + ", payload='" + String.valueOf(this.payload) + "'}";
            case VARIABLE:
                StringBuilder payloadBuf = new StringBuilder();
                StringBuilder defaultPartBuf2 = new StringBuilder();
                if (this.defaultPart != null) {
                    recursive((Node) this.defaultPart, defaultPartBuf2);
                }
                recursive((Node) this.payload, payloadBuf);
                String r = "Node{type=" + String.valueOf(this.type) + ", payload='" + payloadBuf.toString() + "'";
                if (this.defaultPart != null) {
                    r = r + ", defaultPart=" + defaultPartBuf2.toString();
                }
                return r + "}";
            default:
                return null;
        }
    }

    public void dump() {
        System.out.print(toString());
        System.out.print(" -> ");
        if (this.next != null) {
            this.next.dump();
        } else {
            System.out.print(" null");
        }
    }

    void recursive(Node n, StringBuilder sb) {
        Node node = n;
        while (true) {
            Node c = node;
            if (c != null) {
                sb.append(c.toString()).append(" --> ");
                node = c.next;
            } else {
                sb.append("null ");
                return;
            }
        }
    }

    public void setNext(Node n) {
        this.next = n;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        if (this.type != node.type) {
            return false;
        }
        if (this.payload != null) {
            if (!this.payload.equals(node.payload)) {
                return false;
            }
        } else if (node.payload != null) {
            return false;
        }
        if (this.defaultPart != null) {
            if (!this.defaultPart.equals(node.defaultPart)) {
                return false;
            }
        } else if (node.defaultPart != null) {
            return false;
        }
        if (this.next != null) {
            if (!this.next.equals(node.next)) {
                return false;
            }
            return true;
        }
        if (node.next != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        return (31 * ((31 * ((31 * result) + (this.payload != null ? this.payload.hashCode() : 0))) + (this.defaultPart != null ? this.defaultPart.hashCode() : 0))) + (this.next != null ? this.next.hashCode() : 0);
    }
}
