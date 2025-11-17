package org.apache.el.parser;

import jakarta.el.ELException;
import jakarta.el.LambdaExpression;
import java.util.ArrayList;
import java.util.List;
import org.apache.el.ValueExpressionImpl;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.lang.LambdaExpressionNestedState;
import org.apache.el.util.MessageFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstLambdaExpression.class */
public class AstLambdaExpression extends SimpleNode {
    public AstLambdaExpression(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object result;
        LambdaExpressionNestedState state = ctx.getLambdaExpressionNestedState();
        if (state == null) {
            state = new LambdaExpressionNestedState();
            populateNestedState(state);
            ctx.setLambdaExpressionNestedState(state);
        }
        int methodParameterSetCount = jjtGetNumChildren() - 2;
        if (methodParameterSetCount > state.getNestingCount()) {
            throw new ELException(MessageFactory.get("error.lambda.tooManyMethodParameterSets"));
        }
        AstLambdaParameters formalParametersNode = (AstLambdaParameters) this.children[0];
        Node[] formalParamNodes = formalParametersNode.children;
        ValueExpressionImpl ve = new ValueExpressionImpl("", this.children[1], ctx.getFunctionMapper(), ctx.getVariableMapper(), null);
        List<String> formalParameters = new ArrayList<>();
        if (formalParamNodes != null) {
            for (Node formalParamNode : formalParamNodes) {
                formalParameters.add(formalParamNode.getImage());
            }
        }
        LambdaExpression le = new LambdaExpression(formalParameters, ve);
        le.setELContext(ctx);
        if (jjtGetNumChildren() == 2) {
            if (state.getHasFormalParameters()) {
                return le;
            }
            return le.invoke(ctx, (Object[]) null);
        }
        int methodParameterIndex = 2;
        Object invoke = le.invoke(((AstMethodParameters) this.children[2]).getParameters(ctx));
        while (true) {
            result = invoke;
            methodParameterIndex++;
            if (!(result instanceof LambdaExpression) || methodParameterIndex >= jjtGetNumChildren()) {
                break;
            }
            invoke = ((LambdaExpression) result).invoke(((AstMethodParameters) this.children[methodParameterIndex]).getParameters(ctx));
        }
        return result;
    }

    private void populateNestedState(LambdaExpressionNestedState lambdaExpressionNestedState) {
        lambdaExpressionNestedState.incrementNestingCount();
        if (jjtGetNumChildren() > 1) {
            Node firstChild = jjtGetChild(0);
            if (firstChild instanceof AstLambdaParameters) {
                if (firstChild.jjtGetNumChildren() > 0) {
                    lambdaExpressionNestedState.setHasFormalParameters();
                }
                Node secondChild = jjtGetChild(1);
                if (secondChild instanceof AstLambdaExpression) {
                    ((AstLambdaExpression) secondChild).populateNestedState(lambdaExpressionNestedState);
                }
            }
        }
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Node n : this.children) {
            result.append(n.toString());
        }
        return result.toString();
    }
}
