package org.apache.el.parser;

import jakarta.el.ELException;
import jakarta.el.MethodInfo;
import jakarta.el.MethodReference;
import jakarta.el.ValueReference;
import org.apache.el.lang.EvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/Node.class */
public interface Node {
    void jjtOpen();

    void jjtClose();

    void jjtSetParent(Node node);

    Node jjtGetParent();

    void jjtAddChild(Node node, int i);

    Node jjtGetChild(int i);

    int jjtGetNumChildren();

    String getImage();

    Object getValue(EvaluationContext evaluationContext) throws ELException;

    void setValue(EvaluationContext evaluationContext, Object obj) throws ELException;

    Class<?> getType(EvaluationContext evaluationContext) throws ELException;

    boolean isReadOnly(EvaluationContext evaluationContext) throws ELException;

    void accept(NodeVisitor nodeVisitor) throws Exception;

    MethodInfo getMethodInfo(EvaluationContext evaluationContext, Class<?>[] clsArr) throws ELException;

    Object invoke(EvaluationContext evaluationContext, Class<?>[] clsArr, Object[] objArr) throws ELException;

    ValueReference getValueReference(EvaluationContext evaluationContext);

    boolean isParametersProvided();

    MethodReference getMethodReference(EvaluationContext evaluationContext);
}
