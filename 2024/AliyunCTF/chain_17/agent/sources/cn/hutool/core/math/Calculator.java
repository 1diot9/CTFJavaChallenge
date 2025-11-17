package cn.hutool.core.math;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Stack;
import org.h2.engine.Constants;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/math/Calculator.class */
public class Calculator {
    private final Stack<String> postfixStack = new Stack<>();
    private final int[] operatPriority = {0, 3, 2, 1, -1, 1, 0, 2};

    public static double conversion(String expression) {
        return new Calculator().calculate(expression);
    }

    public double calculate(String expression) {
        prepare(transform(expression));
        Stack<String> resultStack = new Stack<>();
        Collections.reverse(this.postfixStack);
        while (false == this.postfixStack.isEmpty()) {
            String currentOp = this.postfixStack.pop();
            if (false == isOperator(currentOp.charAt(0))) {
                resultStack.push(currentOp.replace(Constants.SERVER_PROPERTIES_DIR, "-"));
            } else {
                String secondValue = resultStack.pop();
                String firstValue = resultStack.pop();
                BigDecimal tempResult = calculate(firstValue.replace(Constants.SERVER_PROPERTIES_DIR, "-"), secondValue.replace(Constants.SERVER_PROPERTIES_DIR, "-"), currentOp.charAt(0));
                resultStack.push(tempResult.toString());
            }
        }
        return NumberUtil.mul((String[]) resultStack.toArray(new String[0])).doubleValue();
    }

    private void prepare(String expression) {
        Stack<Character> opStack = new Stack<>();
        opStack.push(',');
        char[] arr = expression.toCharArray();
        int currentIndex = 0;
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            char currentOp = arr[i];
            if (isOperator(currentOp)) {
                if (count > 0) {
                    this.postfixStack.push(new String(arr, currentIndex, count));
                }
                if (currentOp == ')') {
                    while (opStack.peek().charValue() != '(') {
                        this.postfixStack.push(String.valueOf(opStack.pop()));
                    }
                    opStack.pop();
                } else {
                    for (char peekOp = opStack.peek().charValue(); currentOp != '(' && peekOp != ',' && compare(currentOp, peekOp); peekOp = opStack.peek().charValue()) {
                        this.postfixStack.push(String.valueOf(opStack.pop()));
                    }
                    opStack.push(Character.valueOf(currentOp));
                }
                count = 0;
                currentIndex = i + 1;
            } else {
                count++;
            }
        }
        if (count > 1 || (count == 1 && !isOperator(arr[currentIndex]))) {
            this.postfixStack.push(new String(arr, currentIndex, count));
        }
        while (opStack.peek().charValue() != ',') {
            this.postfixStack.push(String.valueOf(opStack.pop()));
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%';
    }

    private boolean compare(char cur, char peek) {
        if (cur == '%') {
            cur = '/';
        }
        if (peek == '%') {
            peek = '/';
        }
        return this.operatPriority[peek - '('] >= this.operatPriority[cur - '('];
    }

    private BigDecimal calculate(String firstValue, String secondValue, char currentOp) {
        BigDecimal result;
        switch (currentOp) {
            case '%':
                result = NumberUtil.toBigDecimal(firstValue).remainder(NumberUtil.toBigDecimal(secondValue));
                break;
            case '&':
            case '\'':
            case '(':
            case ')':
            case ',':
            case '.':
            default:
                throw new IllegalStateException("Unexpected value: " + currentOp);
            case '*':
                result = NumberUtil.mul(firstValue, secondValue);
                break;
            case '+':
                result = NumberUtil.add(firstValue, secondValue);
                break;
            case '-':
                result = NumberUtil.sub(firstValue, secondValue);
                break;
            case '/':
                result = NumberUtil.div(firstValue, secondValue);
                break;
        }
        return result;
    }

    private static String transform(String expression) {
        char[] arr = StrUtil.removeSuffix(StrUtil.cleanBlank(expression), "=").toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '-') {
                if (i == 0) {
                    arr[i] = '~';
                } else {
                    char c = arr[i - 1];
                    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == 'E' || c == 'e') {
                        arr[i] = '~';
                    }
                }
            }
        }
        if (arr[0] == '~' && arr.length > 1 && arr[1] == '(') {
            arr[0] = '-';
            return CustomBooleanEditor.VALUE_0 + new String(arr);
        }
        return new String(arr);
    }
}
