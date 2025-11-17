package cn.hutool.db.sql;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.tomcat.util.net.Constants;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlFormatter.class */
public class SqlFormatter {
    private static final Set<String> BEGIN_CLAUSES = new HashSet();
    private static final Set<String> END_CLAUSES = new HashSet();
    private static final Set<String> LOGICAL = new HashSet();
    private static final Set<String> QUANTIFIERS = new HashSet();
    private static final Set<String> DML = new HashSet();
    private static final Set<String> MISC = new HashSet();
    private static final String indentString = "    ";
    private static final String initial = "\n    ";

    static {
        BEGIN_CLAUSES.add("left");
        BEGIN_CLAUSES.add("right");
        BEGIN_CLAUSES.add("inner");
        BEGIN_CLAUSES.add("outer");
        BEGIN_CLAUSES.add("group");
        BEGIN_CLAUSES.add(AbstractBeanDefinition.ORDER_ATTRIBUTE);
        END_CLAUSES.add("where");
        END_CLAUSES.add("set");
        END_CLAUSES.add("having");
        END_CLAUSES.add("join");
        END_CLAUSES.add("from");
        END_CLAUSES.add("by");
        END_CLAUSES.add("into");
        END_CLAUSES.add("union");
        LOGICAL.add("and");
        LOGICAL.add("or");
        LOGICAL.add("when");
        LOGICAL.add("else");
        LOGICAL.add("end");
        QUANTIFIERS.add("in");
        QUANTIFIERS.add(Constants.SSL_PROTO_ALL);
        QUANTIFIERS.add("exists");
        QUANTIFIERS.add("some");
        QUANTIFIERS.add("any");
        DML.add("insert");
        DML.add("update");
        DML.add("delete");
        MISC.add("select");
        MISC.add(CustomBooleanEditor.VALUE_ON);
    }

    public static String format(String source) {
        return new FormatProcess(source).perform().trim();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlFormatter$FormatProcess.class */
    private static class FormatProcess {
        boolean beginLine = true;
        boolean afterBeginBeforeEnd = false;
        boolean afterByOrSetOrFromOrSelect = false;
        boolean afterOn = false;
        boolean afterBetween = false;
        boolean afterInsert = false;
        int inFunction = 0;
        int parensSinceSelect = 0;
        private final LinkedList<Integer> parenCounts = new LinkedList<>();
        private final LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<>();
        int indent = 1;
        StringBuffer result = new StringBuffer();
        StringTokenizer tokens;
        String lastToken;
        String token;
        String lcToken;

        public FormatProcess(String sql) {
            this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
        }

        /* JADX WARN: Code restructure failed: missing block: B:67:0x0076, code lost:            if ("\"".equals(r4.token) != false) goto L14;     */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x0079, code lost:            r0 = r4.tokens.nextToken();        r4.token += r0;     */
        /* JADX WARN: Code restructure failed: missing block: B:69:0x00a0, code lost:            if ("\"".equals(r0) == false) goto L76;     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.String perform() {
            /*
                Method dump skipped, instructions count: 480
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.hutool.db.sql.SqlFormatter.FormatProcess.perform():java.lang.String");
        }

        private void commaAfterOn() {
            out();
            this.indent--;
            newline();
            this.afterOn = false;
            this.afterByOrSetOrFromOrSelect = true;
        }

        private void commaAfterByOrFromOrSelect() {
            out();
            newline();
        }

        private void logical() {
            if ("end".equals(this.lcToken)) {
                this.indent--;
            }
            newline();
            out();
            this.beginLine = false;
        }

        private void on() {
            this.indent++;
            this.afterOn = true;
            newline();
            out();
            this.beginLine = false;
        }

        private void misc() {
            out();
            if ("between".equals(this.lcToken)) {
                this.afterBetween = true;
            }
            if (this.afterInsert) {
                newline();
                this.afterInsert = false;
            } else {
                this.beginLine = false;
                if ("case".equals(this.lcToken)) {
                    this.indent++;
                }
            }
        }

        private void white() {
            if (!this.beginLine) {
                this.result.append(CharSequenceUtil.SPACE);
            }
        }

        private void updateOrInsertOrDelete() {
            out();
            this.indent++;
            this.beginLine = false;
            if ("update".equals(this.lcToken)) {
                newline();
            }
            if ("insert".equals(this.lcToken)) {
                this.afterInsert = true;
            }
        }

        private void select() {
            out();
            this.indent++;
            newline();
            this.parenCounts.addLast(Integer.valueOf(this.parensSinceSelect));
            this.afterByOrFromOrSelects.addLast(Boolean.valueOf(this.afterByOrSetOrFromOrSelect));
            this.parensSinceSelect = 0;
            this.afterByOrSetOrFromOrSelect = true;
        }

        private void out() {
            this.result.append(this.token);
        }

        private void endNewClause() {
            if (!this.afterBeginBeforeEnd) {
                this.indent--;
                if (this.afterOn) {
                    this.indent--;
                    this.afterOn = false;
                }
                newline();
            }
            out();
            if (!"union".equals(this.lcToken)) {
                this.indent++;
            }
            newline();
            this.afterBeginBeforeEnd = false;
            this.afterByOrSetOrFromOrSelect = "by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken);
        }

        private void beginNewClause() {
            if (!this.afterBeginBeforeEnd) {
                if (this.afterOn) {
                    this.indent--;
                    this.afterOn = false;
                }
                this.indent--;
                newline();
            }
            out();
            this.beginLine = false;
            this.afterBeginBeforeEnd = true;
        }

        private void values() {
            this.indent--;
            newline();
            out();
            this.indent++;
            newline();
        }

        private void closeParen() {
            this.parensSinceSelect--;
            if (this.parensSinceSelect < 0) {
                this.indent--;
                this.parensSinceSelect = this.parenCounts.removeLast().intValue();
                this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast().booleanValue();
            }
            if (this.inFunction > 0) {
                this.inFunction--;
            } else if (!this.afterByOrSetOrFromOrSelect) {
                this.indent--;
                newline();
            }
            out();
            this.beginLine = false;
        }

        private void openParen() {
            if (isFunctionName(this.lastToken) || this.inFunction > 0) {
                this.inFunction++;
            }
            this.beginLine = false;
            if (this.inFunction > 0) {
                out();
            } else {
                out();
                if (!this.afterByOrSetOrFromOrSelect) {
                    this.indent++;
                    newline();
                    this.beginLine = true;
                }
            }
            this.parensSinceSelect++;
        }

        private static boolean isFunctionName(String tok) {
            if (StrUtil.isEmpty(tok)) {
                return true;
            }
            char begin = tok.charAt(0);
            boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '\"' == begin;
            return (!isIdentifier || SqlFormatter.LOGICAL.contains(tok) || SqlFormatter.END_CLAUSES.contains(tok) || SqlFormatter.QUANTIFIERS.contains(tok) || SqlFormatter.DML.contains(tok) || SqlFormatter.MISC.contains(tok)) ? false : true;
        }

        private static boolean isWhitespace(String token) {
            return " \n\r\f\t".contains(token);
        }

        private void newline() {
            this.result.append(StrPool.LF);
            for (int i = 0; i < this.indent; i++) {
                this.result.append(SqlFormatter.indentString);
            }
            this.beginLine = true;
        }
    }
}
