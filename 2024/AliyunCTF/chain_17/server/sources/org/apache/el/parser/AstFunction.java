package org.apache.el.parser;

import jakarta.el.ELClass;
import jakarta.el.ELException;
import jakarta.el.FunctionMapper;
import jakarta.el.LambdaExpression;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;
import org.springframework.cglib.core.Constants;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/AstFunction.class */
public final class AstFunction extends SimpleNode {
    protected String localName;
    protected String prefix;

    public AstFunction(int id) {
        super(id);
        this.localName = "";
        this.prefix = "";
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getOutputName() {
        if (this.prefix == null) {
            return this.localName;
        }
        return this.prefix + ":" + this.localName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        FunctionMapper fnMapper = ctx.getFunctionMapper();
        if (fnMapper == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.null"));
        }
        Method m = fnMapper.resolveFunction(this.prefix, this.localName);
        if (m == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.method", getOutputName()));
        }
        return m.getReturnType();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        VariableMapper varMapper;
        FunctionMapper fnMapper = ctx.getFunctionMapper();
        if (fnMapper == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.null"));
        }
        Method m = fnMapper.resolveFunction(this.prefix, this.localName);
        if (m == null && this.prefix.length() == 0) {
            Object obj = null;
            if (ctx.isLambdaArgument(this.localName)) {
                obj = ctx.getLambdaArgument(this.localName);
            }
            if (obj == null && (varMapper = ctx.getVariableMapper()) != null) {
                obj = varMapper.resolveVariable(this.localName);
                if (obj instanceof ValueExpression) {
                    obj = ((ValueExpression) obj).getValue(ctx);
                }
            }
            if (obj == null) {
                obj = ctx.getELResolver().getValue(ctx, null, this.localName);
            }
            if (obj instanceof LambdaExpression) {
                int i = 0;
                while ((obj instanceof LambdaExpression) && i < jjtGetNumChildren()) {
                    Node args = jjtGetChild(i);
                    obj = ((LambdaExpression) obj).invoke(((AstMethodParameters) args).getParameters(ctx));
                    i++;
                }
                if (i < jjtGetNumChildren()) {
                    throw new ELException(MessageFactory.get("error.lambda.tooManyMethodParameterSets"));
                }
                return obj;
            }
            Object obj2 = ctx.getImportHandler().resolveClass(this.localName);
            if (obj2 != null) {
                return ctx.getELResolver().invoke(ctx, new ELClass((Class) obj2), Constants.CONSTRUCTOR_NAME, null, ((AstMethodParameters) this.children[0]).getParameters(ctx));
            }
            Object obj3 = ctx.getImportHandler().resolveStatic(this.localName);
            if (obj3 != null) {
                return ctx.getELResolver().invoke(ctx, new ELClass((Class) obj3), this.localName, null, ((AstMethodParameters) this.children[0]).getParameters(ctx));
            }
        }
        if (m == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.method", getOutputName()));
        }
        if (jjtGetNumChildren() != 1) {
            throw new ELException(MessageFactory.get("error.function.tooManyMethodParameterSets", getOutputName()));
        }
        Node parameters = jjtGetChild(0);
        Class<?>[] paramTypes = m.getParameterTypes();
        Object[] params = null;
        int inputParameterCount = parameters.jjtGetNumChildren();
        int methodParameterCount = paramTypes.length;
        if (inputParameterCount == 0 && methodParameterCount == 1 && m.isVarArgs()) {
            params = new Object[]{null};
        } else if (inputParameterCount > 0) {
            params = new Object[methodParameterCount];
            for (int i2 = 0; i2 < methodParameterCount; i2++) {
                try {
                    if (m.isVarArgs() && i2 == methodParameterCount - 1) {
                        if (inputParameterCount < methodParameterCount) {
                            Object[] objArr = new Object[1];
                            objArr[0] = null;
                            params[i2] = objArr;
                        } else if (inputParameterCount == methodParameterCount && isArray(parameters.jjtGetChild(i2).getValue(ctx))) {
                            params[i2] = parameters.jjtGetChild(i2).getValue(ctx);
                        } else {
                            Object[] varargs = new Object[(inputParameterCount - methodParameterCount) + 1];
                            Class<?> target = paramTypes[i2].getComponentType();
                            for (int j = i2; j < inputParameterCount; j++) {
                                varargs[j - i2] = parameters.jjtGetChild(j).getValue(ctx);
                                varargs[j - i2] = ELSupport.coerceToType(ctx, varargs[j - i2], target);
                            }
                            params[i2] = varargs;
                        }
                    } else {
                        params[i2] = parameters.jjtGetChild(i2).getValue(ctx);
                    }
                    params[i2] = ELSupport.coerceToType(ctx, params[i2], paramTypes[i2]);
                } catch (ELException ele) {
                    throw new ELException(MessageFactory.get("error.function", getOutputName()), ele);
                }
            }
        }
        try {
            Object result = m.invoke(null, params);
            return result;
        } catch (IllegalAccessException iae) {
            throw new ELException(MessageFactory.get("error.function", getOutputName()), iae);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof ThreadDeath) {
                throw ((ThreadDeath) cause);
            }
            if (cause instanceof VirtualMachineError) {
                throw ((VirtualMachineError) cause);
            }
            throw new ELException(MessageFactory.get("error.function", getOutputName()), cause);
        }
    }

    private boolean isArray(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass().isArray();
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        return ELParserTreeConstants.jjtNodeName[this.id] + "[" + getOutputName() + "]";
    }
}
