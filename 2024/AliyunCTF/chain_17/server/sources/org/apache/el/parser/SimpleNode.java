package org.apache.el.parser;

import jakarta.el.ELException;
import jakarta.el.MethodInfo;
import jakarta.el.MethodReference;
import jakarta.el.PropertyNotWritableException;
import jakarta.el.ValueReference;
import java.util.Arrays;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/SimpleNode.class */
public abstract class SimpleNode implements Node {
    protected SimpleNode parent;
    protected SimpleNode[] children;
    protected final int id;
    protected String image;

    public SimpleNode(int i) {
        this.id = i;
    }

    @Override // org.apache.el.parser.Node
    public void jjtOpen() {
    }

    @Override // org.apache.el.parser.Node
    public void jjtClose() {
    }

    @Override // org.apache.el.parser.Node
    public void jjtSetParent(Node n) {
        this.parent = (SimpleNode) n;
    }

    @Override // org.apache.el.parser.Node
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override // org.apache.el.parser.Node
    public void jjtAddChild(Node n, int i) {
        if (this.children == null) {
            this.children = new SimpleNode[i + 1];
        } else if (i >= this.children.length) {
            SimpleNode[] c = new SimpleNode[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = (SimpleNode) n;
    }

    @Override // org.apache.el.parser.Node
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override // org.apache.el.parser.Node
    public int jjtGetNumChildren() {
        if (this.children == null) {
            return 0;
        }
        return this.children.length;
    }

    public String toString() {
        if (this.image != null) {
            return ELParserTreeConstants.jjtNodeName[this.id] + "[" + this.image + "]";
        }
        return ELParserTreeConstants.jjtNodeName[this.id];
    }

    @Override // org.apache.el.parser.Node
    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override // org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.el.parser.Node
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        return true;
    }

    @Override // org.apache.el.parser.Node
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        throw new PropertyNotWritableException(MessageFactory.get("error.syntax.set"));
    }

    @Override // org.apache.el.parser.Node
    public void accept(NodeVisitor visitor) throws Exception {
        visitor.visit(this);
        if (this.children != null && this.children.length > 0) {
            for (Node child : this.children) {
                child.accept(visitor);
            }
        }
    }

    @Override // org.apache.el.parser.Node
    public Object invoke(EvaluationContext ctx, Class<?>[] paramTypes, Object[] paramValues) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.el.parser.Node
    public MethodInfo getMethodInfo(EvaluationContext ctx, Class<?>[] paramTypes) throws ELException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.el.parser.Node
    public MethodReference getMethodReference(EvaluationContext ctx) {
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        int result = (31 * 1) + Arrays.hashCode(this.children);
        return (31 * ((31 * result) + this.id)) + (this.image == null ? 0 : this.image.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SimpleNode)) {
            return false;
        }
        SimpleNode other = (SimpleNode) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.image == null) {
            if (other.image != null) {
                return false;
            }
        } else if (!this.image.equals(other.image)) {
            return false;
        }
        if (!Arrays.equals(this.children, other.children)) {
            return false;
        }
        return true;
    }

    @Override // org.apache.el.parser.Node
    public ValueReference getValueReference(EvaluationContext ctx) {
        return null;
    }

    @Override // org.apache.el.parser.Node
    public boolean isParametersProvided() {
        return false;
    }
}
