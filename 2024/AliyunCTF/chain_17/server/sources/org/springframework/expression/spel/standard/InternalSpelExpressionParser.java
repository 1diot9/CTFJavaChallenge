package org.springframework.expression.spel.standard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.Identifier;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.InlineMap;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorInstanceof;
import org.springframework.expression.spel.ast.OperatorMatches;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/InternalSpelExpressionParser.class */
class InternalSpelExpressionParser extends TemplateAwareExpressionParser {
    private static final Pattern VALID_QUALIFIED_ID_PATTERN = Pattern.compile("[\\p{L}\\p{N}_$]+");
    private final SpelParserConfiguration configuration;
    private final Deque<SpelNodeImpl> constructedNodes = new ArrayDeque();
    private final ConcurrentMap<String, Pattern> patternCache = new ConcurrentHashMap();
    private String expressionString = "";
    private List<Token> tokenStream = Collections.emptyList();
    private int tokenStreamLength;
    private int tokenStreamPointer;

    public InternalSpelExpressionParser(SpelParserConfiguration configuration) {
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.common.TemplateAwareExpressionParser
    public SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        checkExpressionLength(expressionString);
        try {
            this.expressionString = expressionString;
            Tokenizer tokenizer = new Tokenizer(expressionString);
            this.tokenStream = tokenizer.process();
            this.tokenStreamLength = this.tokenStream.size();
            this.tokenStreamPointer = 0;
            this.constructedNodes.clear();
            SpelNodeImpl ast = eatExpression();
            if (ast == null) {
                throw new SpelParseException(this.expressionString, 0, SpelMessage.OOD, new Object[0]);
            }
            Token t = peekToken();
            if (t != null) {
                throw new SpelParseException(this.expressionString, t.startPos, SpelMessage.MORE_INPUT, toString(nextToken()));
            }
            return new SpelExpression(expressionString, ast, this.configuration);
        } catch (InternalParseException ex) {
            throw ex.getCause();
        }
    }

    private void checkExpressionLength(String string) {
        int maxLength = this.configuration.getMaximumExpressionLength();
        if (string.length() > maxLength) {
            throw new SpelEvaluationException(SpelMessage.MAX_EXPRESSION_LENGTH_EXCEEDED, Integer.valueOf(maxLength));
        }
    }

    @Nullable
    private SpelNodeImpl eatExpression() {
        SpelNodeImpl expr = eatLogicalOrExpression();
        Token t = peekToken();
        if (t != null) {
            if (t.kind == TokenKind.ASSIGN) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
                }
                nextToken();
                SpelNodeImpl assignedValue = eatLogicalOrExpression();
                return new Assign(t.startPos, t.endPos, expr, assignedValue);
            }
            if (t.kind == TokenKind.ELVIS) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 2);
                }
                nextToken();
                SpelNodeImpl valueIfNull = eatExpression();
                if (valueIfNull == null) {
                    valueIfNull = new NullLiteral(t.startPos + 1, t.endPos + 1);
                }
                return new Elvis(t.startPos, t.endPos, expr, valueIfNull);
            }
            if (t.kind == TokenKind.QMARK) {
                if (expr == null) {
                    expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
                }
                nextToken();
                SpelNodeImpl ifTrueExprValue = eatExpression();
                eatToken(TokenKind.COLON);
                SpelNodeImpl ifFalseExprValue = eatExpression();
                return new Ternary(t.startPos, t.endPos, expr, ifTrueExprValue, ifFalseExprValue);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatLogicalOrExpression() {
        SpelNodeImpl eatLogicalAndExpression = eatLogicalAndExpression();
        while (true) {
            SpelNodeImpl expr = eatLogicalAndExpression;
            if (peekIdentifierToken("or") || peekToken(TokenKind.SYMBOLIC_OR)) {
                Token t = takeToken();
                SpelNodeImpl rhExpr = eatLogicalAndExpression();
                checkOperands(t, expr, rhExpr);
                eatLogicalAndExpression = new OpOr(t.startPos, t.endPos, expr, rhExpr);
            } else {
                return expr;
            }
        }
    }

    @Nullable
    private SpelNodeImpl eatLogicalAndExpression() {
        SpelNodeImpl eatRelationalExpression = eatRelationalExpression();
        while (true) {
            SpelNodeImpl expr = eatRelationalExpression;
            if (peekIdentifierToken("and") || peekToken(TokenKind.SYMBOLIC_AND)) {
                Token t = takeToken();
                SpelNodeImpl rhExpr = eatRelationalExpression();
                checkOperands(t, expr, rhExpr);
                eatRelationalExpression = new OpAnd(t.startPos, t.endPos, expr, rhExpr);
            } else {
                return expr;
            }
        }
    }

    @Nullable
    private SpelNodeImpl eatRelationalExpression() {
        SpelNodeImpl expr = eatSumExpression();
        Token relationalOperatorToken = maybeEatRelationalOperator();
        if (relationalOperatorToken != null) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatSumExpression();
            checkOperands(t, expr, rhExpr);
            TokenKind tk = relationalOperatorToken.kind;
            if (relationalOperatorToken.isNumericRelationalOperator()) {
                if (tk == TokenKind.GT) {
                    return new OpGT(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == TokenKind.LT) {
                    return new OpLT(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == TokenKind.LE) {
                    return new OpLE(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == TokenKind.GE) {
                    return new OpGE(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == TokenKind.EQ) {
                    return new OpEQ(t.startPos, t.endPos, expr, rhExpr);
                }
                if (tk == TokenKind.NE) {
                    return new OpNE(t.startPos, t.endPos, expr, rhExpr);
                }
            }
            if (tk == TokenKind.INSTANCEOF) {
                return new OperatorInstanceof(t.startPos, t.endPos, expr, rhExpr);
            }
            if (tk == TokenKind.MATCHES) {
                return new OperatorMatches(this.patternCache, t.startPos, t.endPos, expr, rhExpr);
            }
            if (tk == TokenKind.BETWEEN) {
                return new OperatorBetween(t.startPos, t.endPos, expr, rhExpr);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatSumExpression() {
        SpelNodeImpl expr = eatProductExpression();
        while (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatProductExpression();
            checkRightOperand(t, rhExpr);
            if (t.kind == TokenKind.PLUS) {
                expr = new OpPlus(t.startPos, t.endPos, expr, rhExpr);
            } else if (t.kind == TokenKind.MINUS) {
                expr = new OpMinus(t.startPos, t.endPos, expr, rhExpr);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatProductExpression() {
        SpelNodeImpl expr = eatPowerIncDecExpression();
        while (peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatPowerIncDecExpression();
            checkOperands(t, expr, rhExpr);
            if (t.kind == TokenKind.STAR) {
                expr = new OpMultiply(t.startPos, t.endPos, expr, rhExpr);
            } else if (t.kind == TokenKind.DIV) {
                expr = new OpDivide(t.startPos, t.endPos, expr, rhExpr);
            } else if (t.kind == TokenKind.MOD) {
                expr = new OpModulus(t.startPos, t.endPos, expr, rhExpr);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatPowerIncDecExpression() {
        SpelNodeImpl expr = eatUnaryExpression();
        if (peekToken(TokenKind.POWER)) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatUnaryExpression();
            checkRightOperand(t, rhExpr);
            return new OperatorPower(t.startPos, t.endPos, expr, rhExpr);
        }
        if (expr != null && peekToken(TokenKind.INC, TokenKind.DEC)) {
            Token t2 = takeToken();
            if (t2.getKind() == TokenKind.INC) {
                return new OpInc(t2.startPos, t2.endPos, true, expr);
            }
            return new OpDec(t2.startPos, t2.endPos, true, expr);
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatUnaryExpression() {
        if (peekToken(TokenKind.NOT, TokenKind.PLUS, TokenKind.MINUS)) {
            Token t = takeToken();
            SpelNodeImpl expr = eatUnaryExpression();
            if (expr == null) {
                throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
            }
            if (t.kind == TokenKind.NOT) {
                return new OperatorNot(t.startPos, t.endPos, expr);
            }
            if (t.kind == TokenKind.PLUS) {
                return new OpPlus(t.startPos, t.endPos, expr);
            }
            if (t.kind == TokenKind.MINUS) {
                return new OpMinus(t.startPos, t.endPos, expr);
            }
        }
        if (peekToken(TokenKind.INC, TokenKind.DEC)) {
            Token t2 = takeToken();
            SpelNodeImpl expr2 = eatUnaryExpression();
            if (t2.getKind() == TokenKind.INC) {
                return new OpInc(t2.startPos, t2.endPos, false, expr2);
            }
            if (t2.kind == TokenKind.DEC) {
                return new OpDec(t2.startPos, t2.endPos, false, expr2);
            }
        }
        return eatPrimaryExpression();
    }

    @Nullable
    private SpelNodeImpl eatPrimaryExpression() {
        SpelNodeImpl start = eatStartNode();
        List<SpelNodeImpl> nodes = null;
        SpelNodeImpl eatNode = eatNode();
        while (true) {
            SpelNodeImpl node = eatNode;
            if (node == null) {
                break;
            }
            if (nodes == null) {
                nodes = new ArrayList<>(4);
                nodes.add(start);
            }
            nodes.add(node);
            eatNode = eatNode();
        }
        if (start == null || nodes == null) {
            return start;
        }
        return new CompoundExpression(start.getStartPosition(), nodes.get(nodes.size() - 1).getEndPosition(), (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0]));
    }

    @Nullable
    private SpelNodeImpl eatNode() {
        return peekToken(TokenKind.DOT, TokenKind.SAFE_NAVI) ? eatDottedNode() : eatNonDottedNode();
    }

    @Nullable
    private SpelNodeImpl eatNonDottedNode() {
        if (peekToken(TokenKind.LSQUARE) && maybeEatIndexer()) {
            return pop();
        }
        return null;
    }

    private SpelNodeImpl eatDottedNode() {
        Token t = takeToken();
        boolean nullSafeNavigation = t.kind == TokenKind.SAFE_NAVI;
        if (maybeEatMethodOrProperty(nullSafeNavigation) || maybeEatFunctionOrVar() || maybeEatProjection(nullSafeNavigation) || maybeEatSelection(nullSafeNavigation)) {
            return pop();
        }
        if (peekToken() == null) {
            throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
        }
        throw internalException(t.startPos, SpelMessage.UNEXPECTED_DATA_AFTER_DOT, toString(peekToken()));
    }

    private boolean maybeEatFunctionOrVar() {
        if (!peekToken(TokenKind.HASH)) {
            return false;
        }
        Token t = takeToken();
        Token functionOrVariableName = eatToken(TokenKind.IDENTIFIER);
        SpelNodeImpl[] args = maybeEatMethodArgs();
        if (args == null) {
            push(new VariableReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos));
            return true;
        }
        push(new FunctionReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos, args));
        return true;
    }

    @Nullable
    private SpelNodeImpl[] maybeEatMethodArgs() {
        if (!peekToken(TokenKind.LPAREN)) {
            return null;
        }
        List<SpelNodeImpl> args = new ArrayList<>();
        consumeArguments(args);
        eatToken(TokenKind.RPAREN);
        return (SpelNodeImpl[]) args.toArray(new SpelNodeImpl[0]);
    }

    private void eatConstructorArgs(List<SpelNodeImpl> accumulatedArguments) {
        if (!peekToken(TokenKind.LPAREN)) {
            throw internalException(positionOf(peekToken()), SpelMessage.MISSING_CONSTRUCTOR_ARGS, new Object[0]);
        }
        consumeArguments(accumulatedArguments);
        eatToken(TokenKind.RPAREN);
    }

    private void consumeArguments(List<SpelNodeImpl> accumulatedArguments) {
        Token next;
        Token t = peekToken();
        if (t == null) {
            return;
        }
        int pos = t.startPos;
        do {
            nextToken();
            Token t2 = peekToken();
            if (t2 == null) {
                throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
            }
            if (t2.kind != TokenKind.RPAREN) {
                accumulatedArguments.add(eatExpression());
            }
            next = peekToken();
            if (next == null) {
                break;
            }
        } while (next.kind == TokenKind.COMMA);
        if (next == null) {
            throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
        }
    }

    private int positionOf(@Nullable Token t) {
        if (t == null) {
            return this.expressionString.length();
        }
        return t.startPos;
    }

    @Nullable
    private SpelNodeImpl eatStartNode() {
        if (maybeEatLiteral()) {
            return pop();
        }
        if (maybeEatParenExpression()) {
            return pop();
        }
        if (maybeEatTypeReference() || maybeEatNullReference() || maybeEatConstructorReference() || maybeEatMethodOrProperty(false) || maybeEatFunctionOrVar()) {
            return pop();
        }
        if (maybeEatBeanReference()) {
            return pop();
        }
        if (maybeEatProjection(false) || maybeEatSelection(false) || maybeEatIndexer()) {
            return pop();
        }
        if (maybeEatInlineListOrMap()) {
            return pop();
        }
        return null;
    }

    private boolean maybeEatBeanReference() {
        Token beanNameToken;
        String beanName;
        BeanReference beanReference;
        if (peekToken(TokenKind.BEAN_REF) || peekToken(TokenKind.FACTORY_BEAN_REF)) {
            Token beanRefToken = takeToken();
            if (peekToken(TokenKind.IDENTIFIER)) {
                beanNameToken = eatToken(TokenKind.IDENTIFIER);
                beanName = beanNameToken.stringValue();
            } else if (peekToken(TokenKind.LITERAL_STRING)) {
                beanNameToken = eatToken(TokenKind.LITERAL_STRING);
                String beanName2 = beanNameToken.stringValue();
                beanName = beanName2.substring(1, beanName2.length() - 1);
            } else {
                throw internalException(beanRefToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE, new Object[0]);
            }
            if (beanRefToken.getKind() == TokenKind.FACTORY_BEAN_REF) {
                String beanNameString = String.valueOf(TokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
                beanReference = new BeanReference(beanRefToken.startPos, beanNameToken.endPos, beanNameString);
            } else {
                beanReference = new BeanReference(beanNameToken.startPos, beanNameToken.endPos, beanName);
            }
            this.constructedNodes.push(beanReference);
            return true;
        }
        return false;
    }

    private boolean maybeEatTypeReference() {
        Token typeName;
        if (!peekToken(TokenKind.IDENTIFIER) || (typeName = peekToken()) == null || !"T".equals(typeName.stringValue())) {
            return false;
        }
        Token t = takeToken();
        if (peekToken(TokenKind.RSQUARE)) {
            push(new PropertyOrFieldReference(false, t.stringValue(), t.startPos, t.endPos));
            return true;
        }
        eatToken(TokenKind.LPAREN);
        SpelNodeImpl node = eatPossiblyQualifiedId();
        int dims = 0;
        while (peekToken(TokenKind.LSQUARE, true)) {
            eatToken(TokenKind.RSQUARE);
            dims++;
        }
        eatToken(TokenKind.RPAREN);
        this.constructedNodes.push(new TypeReference(typeName.startPos, typeName.endPos, node, dims));
        return true;
    }

    private boolean maybeEatNullReference() {
        Token nullToken;
        if (!peekToken(TokenKind.IDENTIFIER) || (nullToken = peekToken()) == null || !"null".equalsIgnoreCase(nullToken.stringValue())) {
            return false;
        }
        nextToken();
        this.constructedNodes.push(new NullLiteral(nullToken.startPos, nullToken.endPos));
        return true;
    }

    private boolean maybeEatProjection(boolean nullSafeNavigation) {
        Token t = peekToken();
        if (t == null || !peekToken(TokenKind.PROJECT, true)) {
            return false;
        }
        SpelNodeImpl expr = eatExpression();
        if (expr == null) {
            throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
        }
        eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Projection(nullSafeNavigation, t.startPos, t.endPos, expr));
        return true;
    }

    private boolean maybeEatInlineListOrMap() {
        SpelNodeImpl expr;
        Token t = peekToken();
        if (t == null || !peekToken(TokenKind.LCURLY, true)) {
            return false;
        }
        Token closingCurly = peekToken();
        if (closingCurly != null && peekToken(TokenKind.RCURLY, true)) {
            expr = new InlineList(t.startPos, closingCurly.endPos, new SpelNodeImpl[0]);
        } else if (peekToken(TokenKind.COLON, true)) {
            expr = new InlineMap(t.startPos, eatToken(TokenKind.RCURLY).endPos, new SpelNodeImpl[0]);
        } else {
            SpelNodeImpl firstExpression = eatExpression();
            if (peekToken(TokenKind.RCURLY)) {
                List<SpelNodeImpl> elements = new ArrayList<>();
                elements.add(firstExpression);
                expr = new InlineList(t.startPos, eatToken(TokenKind.RCURLY).endPos, (SpelNodeImpl[]) elements.toArray(new SpelNodeImpl[0]));
            } else if (peekToken(TokenKind.COMMA, true)) {
                List<SpelNodeImpl> elements2 = new ArrayList<>();
                elements2.add(firstExpression);
                do {
                    elements2.add(eatExpression());
                } while (peekToken(TokenKind.COMMA, true));
                expr = new InlineList(t.startPos, eatToken(TokenKind.RCURLY).endPos, (SpelNodeImpl[]) elements2.toArray(new SpelNodeImpl[0]));
            } else if (peekToken(TokenKind.COLON, true)) {
                List<SpelNodeImpl> elements3 = new ArrayList<>();
                elements3.add(firstExpression);
                elements3.add(eatExpression());
                while (peekToken(TokenKind.COMMA, true)) {
                    elements3.add(eatExpression());
                    eatToken(TokenKind.COLON);
                    elements3.add(eatExpression());
                }
                expr = new InlineMap(t.startPos, eatToken(TokenKind.RCURLY).endPos, (SpelNodeImpl[]) elements3.toArray(new SpelNodeImpl[0]));
            } else {
                throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
            }
        }
        this.constructedNodes.push(expr);
        return true;
    }

    private boolean maybeEatIndexer() {
        Token t = peekToken();
        if (t == null || !peekToken(TokenKind.LSQUARE, true)) {
            return false;
        }
        SpelNodeImpl expr = eatExpression();
        if (expr == null) {
            throw internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
        }
        eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Indexer(t.startPos, t.endPos, expr));
        return true;
    }

    private boolean maybeEatSelection(boolean nullSafeNavigation) {
        Token t = peekToken();
        if (t == null || !peekSelectToken()) {
            return false;
        }
        nextToken();
        SpelNodeImpl expr = eatExpression();
        if (expr == null) {
            throw internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
        }
        eatToken(TokenKind.RSQUARE);
        if (t.kind == TokenKind.SELECT_FIRST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 1, t.startPos, t.endPos, expr));
            return true;
        }
        if (t.kind == TokenKind.SELECT_LAST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 2, t.startPos, t.endPos, expr));
            return true;
        }
        this.constructedNodes.push(new Selection(nullSafeNavigation, 0, t.startPos, t.endPos, expr));
        return true;
    }

    private SpelNodeImpl eatPossiblyQualifiedId() {
        Token node;
        Deque<SpelNodeImpl> qualifiedIdPieces = new ArrayDeque<>();
        Token peekToken = peekToken();
        while (true) {
            node = peekToken;
            if (!isValidQualifiedId(node)) {
                break;
            }
            nextToken();
            if (node.kind != TokenKind.DOT) {
                qualifiedIdPieces.add(new Identifier(node.stringValue(), node.startPos, node.endPos));
            }
            peekToken = peekToken();
        }
        if (qualifiedIdPieces.isEmpty()) {
            if (node == null) {
                throw internalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
            }
            throw internalException(node.startPos, SpelMessage.NOT_EXPECTED_TOKEN, "qualified ID", node.getKind().toString().toLowerCase());
        }
        return new QualifiedIdentifier(qualifiedIdPieces.getFirst().getStartPosition(), qualifiedIdPieces.getLast().getEndPosition(), (SpelNodeImpl[]) qualifiedIdPieces.toArray(new SpelNodeImpl[0]));
    }

    private boolean isValidQualifiedId(@Nullable Token node) {
        if (node == null || node.kind == TokenKind.LITERAL_STRING) {
            return false;
        }
        if (node.kind == TokenKind.DOT || node.kind == TokenKind.IDENTIFIER) {
            return true;
        }
        String value = node.stringValue();
        return StringUtils.hasLength(value) && VALID_QUALIFIED_ID_PATTERN.matcher(value).matches();
    }

    private boolean maybeEatMethodOrProperty(boolean nullSafeNavigation) {
        if (peekToken(TokenKind.IDENTIFIER)) {
            Token methodOrPropertyName = takeToken();
            SpelNodeImpl[] args = maybeEatMethodArgs();
            if (args == null) {
                push(new PropertyOrFieldReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos));
                return true;
            }
            push(new MethodReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos, args));
            return true;
        }
        return false;
    }

    private boolean maybeEatConstructorReference() {
        if (peekIdentifierToken("new")) {
            Token newToken = takeToken();
            if (peekToken(TokenKind.RSQUARE)) {
                push(new PropertyOrFieldReference(false, newToken.stringValue(), newToken.startPos, newToken.endPos));
                return true;
            }
            SpelNodeImpl possiblyQualifiedConstructorName = eatPossiblyQualifiedId();
            List<SpelNodeImpl> nodes = new ArrayList<>();
            nodes.add(possiblyQualifiedConstructorName);
            if (peekToken(TokenKind.LSQUARE)) {
                List<SpelNodeImpl> dimensions = new ArrayList<>();
                while (peekToken(TokenKind.LSQUARE, true)) {
                    if (!peekToken(TokenKind.RSQUARE)) {
                        dimensions.add(eatExpression());
                    } else {
                        dimensions.add(null);
                    }
                    eatToken(TokenKind.RSQUARE);
                }
                if (maybeEatInlineListOrMap()) {
                    nodes.add(pop());
                }
                push(new ConstructorReference(newToken.startPos, newToken.endPos, (SpelNodeImpl[]) dimensions.toArray(new SpelNodeImpl[0]), (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0])));
                return true;
            }
            eatConstructorArgs(nodes);
            push(new ConstructorReference(newToken.startPos, newToken.endPos, (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0])));
            return true;
        }
        return false;
    }

    private void push(SpelNodeImpl newNode) {
        this.constructedNodes.push(newNode);
    }

    private SpelNodeImpl pop() {
        return this.constructedNodes.pop();
    }

    private boolean maybeEatLiteral() {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == TokenKind.LITERAL_INT) {
            push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 10));
        } else if (t.kind == TokenKind.LITERAL_LONG) {
            push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 10));
        } else if (t.kind == TokenKind.LITERAL_HEXINT) {
            push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 16));
        } else if (t.kind == TokenKind.LITERAL_HEXLONG) {
            push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 16));
        } else if (t.kind == TokenKind.LITERAL_REAL) {
            push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, false));
        } else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
            push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, true));
        } else if (peekIdentifierToken("true")) {
            push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, true));
        } else if (peekIdentifierToken("false")) {
            push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, false));
        } else if (t.kind == TokenKind.LITERAL_STRING) {
            push(new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
        } else {
            return false;
        }
        nextToken();
        return true;
    }

    private boolean maybeEatParenExpression() {
        Token t;
        if (!peekToken(TokenKind.LPAREN) || (t = nextToken()) == null) {
            return false;
        }
        SpelNodeImpl expr = eatExpression();
        if (expr == null) {
            throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
        }
        eatToken(TokenKind.RPAREN);
        push(expr);
        return true;
    }

    @Nullable
    private Token maybeEatRelationalOperator() {
        Token t = peekToken();
        if (t == null) {
            return null;
        }
        if (t.isNumericRelationalOperator()) {
            return t;
        }
        if (t.isIdentifier()) {
            String idString = t.stringValue();
            if (idString.equalsIgnoreCase("instanceof")) {
                return t.asInstanceOfToken();
            }
            if (idString.equalsIgnoreCase("matches")) {
                return t.asMatchesToken();
            }
            if (idString.equalsIgnoreCase("between")) {
                return t.asBetweenToken();
            }
            return null;
        }
        return null;
    }

    private Token eatToken(TokenKind expectedKind) {
        Token t = nextToken();
        if (t == null) {
            int pos = this.expressionString.length();
            throw internalException(pos, SpelMessage.OOD, new Object[0]);
        }
        if (t.kind != expectedKind) {
            throw internalException(t.startPos, SpelMessage.NOT_EXPECTED_TOKEN, expectedKind.toString().toLowerCase(), t.getKind().toString().toLowerCase());
        }
        return t;
    }

    private boolean peekToken(TokenKind desiredTokenKind) {
        return peekToken(desiredTokenKind, false);
    }

    private boolean peekToken(TokenKind desiredTokenKind, boolean consumeIfMatched) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == desiredTokenKind) {
            if (consumeIfMatched) {
                this.tokenStreamPointer++;
                return true;
            }
            return true;
        }
        if (desiredTokenKind == TokenKind.IDENTIFIER && t.kind.ordinal() >= TokenKind.DIV.ordinal() && t.kind.ordinal() <= TokenKind.NOT.ordinal() && t.data != null) {
            return true;
        }
        return false;
    }

    private boolean peekToken(TokenKind possible1, TokenKind possible2) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2;
    }

    private boolean peekToken(TokenKind possible1, TokenKind possible2, TokenKind possible3) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2 || t.kind == possible3;
    }

    private boolean peekIdentifierToken(String identifierString) {
        Token t = peekToken();
        return t != null && t.kind == TokenKind.IDENTIFIER && identifierString.equalsIgnoreCase(t.stringValue());
    }

    private boolean peekSelectToken() {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == TokenKind.SELECT || t.kind == TokenKind.SELECT_FIRST || t.kind == TokenKind.SELECT_LAST;
    }

    private Token takeToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            throw new IllegalStateException("No token");
        }
        List<Token> list = this.tokenStream;
        int i = this.tokenStreamPointer;
        this.tokenStreamPointer = i + 1;
        return list.get(i);
    }

    @Nullable
    private Token nextToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        List<Token> list = this.tokenStream;
        int i = this.tokenStreamPointer;
        this.tokenStreamPointer = i + 1;
        return list.get(i);
    }

    @Nullable
    private Token peekToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer);
    }

    public String toString(@Nullable Token t) {
        if (t == null) {
            return "";
        }
        if (t.getKind().hasPayload()) {
            return t.stringValue();
        }
        return t.kind.toString().toLowerCase();
    }

    private void checkOperands(Token token, @Nullable SpelNodeImpl left, @Nullable SpelNodeImpl right) {
        checkLeftOperand(token, left);
        checkRightOperand(token, right);
    }

    private void checkLeftOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.LEFT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private void checkRightOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.RIGHT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private InternalParseException internalException(int startPos, SpelMessage message, Object... inserts) {
        return new InternalParseException(new SpelParseException(this.expressionString, startPos, message, inserts));
    }
}
