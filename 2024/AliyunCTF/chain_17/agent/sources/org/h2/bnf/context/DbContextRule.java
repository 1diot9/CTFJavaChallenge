package org.h2.bnf.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.bnf.Bnf;
import org.h2.bnf.BnfVisitor;
import org.h2.bnf.Rule;
import org.h2.bnf.RuleElement;
import org.h2.bnf.RuleHead;
import org.h2.bnf.RuleList;
import org.h2.bnf.Sentence;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbContextRule.class */
public class DbContextRule implements Rule {
    public static final int COLUMN = 0;
    public static final int TABLE = 1;
    public static final int TABLE_ALIAS = 2;
    public static final int NEW_TABLE_ALIAS = 3;
    public static final int COLUMN_ALIAS = 4;
    public static final int SCHEMA = 5;
    public static final int PROCEDURE = 6;
    private final DbContents contents;
    private final int type;
    private String columnType;

    public DbContextRule(DbContents dbContents, int i) {
        this.contents = dbContents;
        this.type = i;
    }

    public void setColumnType(String str) {
        this.columnType = str;
    }

    @Override // org.h2.bnf.Rule
    public void setLinks(HashMap<String, RuleHead> hashMap) {
    }

    @Override // org.h2.bnf.Rule
    public void accept(BnfVisitor bnfVisitor) {
    }

    @Override // org.h2.bnf.Rule
    public boolean autoComplete(Sentence sentence) {
        char charAt;
        String query = sentence.getQuery();
        String str = query;
        String queryUpper = sentence.getQueryUpper();
        switch (this.type) {
            case 0:
                HashSet<DbTableOrView> tables = sentence.getTables();
                String str2 = null;
                DbTableOrView lastMatchedTable = sentence.getLastMatchedTable();
                if (lastMatchedTable != null && lastMatchedTable.getColumns() != null) {
                    for (DbColumn dbColumn : lastMatchedTable.getColumns()) {
                        String str3 = queryUpper;
                        String upperEnglish = StringUtils.toUpperEnglish(dbColumn.getName());
                        if (dbColumn.getQuotedName().length() > upperEnglish.length()) {
                            upperEnglish = dbColumn.getQuotedName();
                            str3 = query;
                        }
                        if (str3.startsWith(upperEnglish) && testColumnType(dbColumn)) {
                            String substring = str.substring(upperEnglish.length());
                            if (str2 == null || substring.length() < str2.length()) {
                                str2 = substring;
                            } else if ((str.length() == 0 || upperEnglish.startsWith(str3)) && str.length() < upperEnglish.length()) {
                                sentence.add(dbColumn.getName(), dbColumn.getName().substring(str.length()), 0);
                            }
                        }
                    }
                }
                for (DbSchema dbSchema : this.contents.getSchemas()) {
                    for (DbTableOrView dbTableOrView : dbSchema.getTables()) {
                        if ((dbTableOrView == lastMatchedTable || tables == null || tables.contains(dbTableOrView)) && dbTableOrView != null && dbTableOrView.getColumns() != null) {
                            for (DbColumn dbColumn2 : dbTableOrView.getColumns()) {
                                String upperEnglish2 = StringUtils.toUpperEnglish(dbColumn2.getName());
                                if (testColumnType(dbColumn2)) {
                                    if (queryUpper.startsWith(upperEnglish2)) {
                                        String substring2 = str.substring(upperEnglish2.length());
                                        if (str2 == null || substring2.length() < str2.length()) {
                                            str2 = substring2;
                                        }
                                    } else if ((str.length() == 0 || upperEnglish2.startsWith(queryUpper)) && str.length() < upperEnglish2.length()) {
                                        sentence.add(dbColumn2.getName(), dbColumn2.getName().substring(str.length()), 0);
                                    }
                                }
                            }
                        }
                    }
                }
                if (str2 != null) {
                    str = str2;
                    break;
                }
                break;
            case 1:
                DbSchema lastMatchedSchema = sentence.getLastMatchedSchema();
                if (lastMatchedSchema == null) {
                    lastMatchedSchema = this.contents.getDefaultSchema();
                }
                String str4 = null;
                DbTableOrView dbTableOrView2 = null;
                for (DbTableOrView dbTableOrView3 : lastMatchedSchema.getTables()) {
                    String str5 = queryUpper;
                    String upperEnglish3 = StringUtils.toUpperEnglish(dbTableOrView3.getName());
                    if (dbTableOrView3.getQuotedName().length() > upperEnglish3.length()) {
                        upperEnglish3 = dbTableOrView3.getQuotedName();
                        str5 = query;
                    }
                    if (str5.startsWith(upperEnglish3)) {
                        if (str4 == null || upperEnglish3.length() > str4.length()) {
                            str4 = upperEnglish3;
                            dbTableOrView2 = dbTableOrView3;
                        }
                    } else if ((str.length() == 0 || upperEnglish3.startsWith(str5)) && str.length() < upperEnglish3.length()) {
                        sentence.add(dbTableOrView3.getQuotedName(), dbTableOrView3.getQuotedName().substring(str.length()), 0);
                    }
                }
                if (str4 != null) {
                    sentence.setLastMatchedTable(dbTableOrView2);
                    sentence.addTable(dbTableOrView2);
                    str = str.substring(str4.length());
                    break;
                }
                break;
            case 2:
                str = autoCompleteTableAlias(sentence, false);
                break;
            case 3:
                str = autoCompleteTableAlias(sentence, true);
                break;
            case 4:
                int i = 0;
                if (query.indexOf(32) >= 0) {
                    while (i < queryUpper.length() && ((charAt = queryUpper.charAt(i)) == '_' || Character.isLetterOrDigit(charAt))) {
                        i++;
                    }
                    if (i != 0) {
                        String substring3 = queryUpper.substring(0, i);
                        if (!ParserUtil.isKeyword(substring3, false)) {
                            str = str.substring(substring3.length());
                            break;
                        }
                    }
                }
                break;
            case 5:
                String str6 = null;
                DbSchema dbSchema2 = null;
                for (DbSchema dbSchema3 : this.contents.getSchemas()) {
                    String upperEnglish4 = StringUtils.toUpperEnglish(dbSchema3.name);
                    if (queryUpper.startsWith(upperEnglish4)) {
                        if (str6 == null || upperEnglish4.length() > str6.length()) {
                            str6 = upperEnglish4;
                            dbSchema2 = dbSchema3;
                        }
                    } else if ((str.length() == 0 || upperEnglish4.startsWith(queryUpper)) && str.length() < upperEnglish4.length()) {
                        sentence.add(upperEnglish4, upperEnglish4.substring(str.length()), this.type);
                        sentence.add(dbSchema3.quotedName + ".", dbSchema3.quotedName.substring(str.length()) + ".", 0);
                    }
                }
                if (str6 != null) {
                    sentence.setLastMatchedSchema(dbSchema2);
                    str = str.substring(str6.length());
                    break;
                }
                break;
            case 6:
                autoCompleteProcedure(sentence);
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        if (!str.equals(query)) {
            while (Bnf.startWithSpace(str)) {
                str = str.substring(1);
            }
            sentence.setQuery(str);
            return true;
        }
        return false;
    }

    private boolean testColumnType(DbColumn dbColumn) {
        if (this.columnType == null) {
            return true;
        }
        String dataType = dbColumn.getDataType();
        if (this.columnType.contains("CHAR") || this.columnType.contains("CLOB")) {
            return dataType.contains("CHAR") || dataType.contains("CLOB");
        }
        if (this.columnType.contains("BINARY") || this.columnType.contains("BLOB")) {
            return dataType.contains("BINARY") || dataType.contains("BLOB");
        }
        return dataType.contains(this.columnType);
    }

    private void autoCompleteProcedure(Sentence sentence) {
        DbSchema lastMatchedSchema = sentence.getLastMatchedSchema();
        if (lastMatchedSchema == null) {
            lastMatchedSchema = this.contents.getDefaultSchema();
        }
        String queryUpper = sentence.getQueryUpper();
        String str = queryUpper;
        int indexOf = queryUpper.indexOf(40);
        if (indexOf != -1) {
            str = StringUtils.trimSubstring(queryUpper, 0, indexOf);
        }
        RuleElement ruleElement = new RuleElement("(", "Function");
        RuleElement ruleElement2 = new RuleElement(")", "Function");
        RuleElement ruleElement3 = new RuleElement(",", "Function");
        for (DbProcedure dbProcedure : lastMatchedSchema.getProcedures()) {
            String name = dbProcedure.getName();
            if (name.startsWith(str)) {
                RuleList ruleList = new RuleList(new RuleElement(name, "Function"), ruleElement, false);
                if (queryUpper.contains("(")) {
                    for (DbColumn dbColumn : dbProcedure.getParameters()) {
                        if (dbColumn.getPosition() > 1) {
                            ruleList = new RuleList(ruleList, ruleElement3, false);
                        }
                        DbContextRule dbContextRule = new DbContextRule(this.contents, 0);
                        String dataType = dbColumn.getDataType();
                        if (dataType.contains("(")) {
                            dataType = dataType.substring(0, dataType.indexOf(40));
                        }
                        dbContextRule.setColumnType(dataType);
                        ruleList = new RuleList(ruleList, dbContextRule, false);
                    }
                    ruleList = new RuleList(ruleList, ruleElement2, false);
                }
                ruleList.autoComplete(sentence);
            }
        }
    }

    private static String autoCompleteTableAlias(Sentence sentence, boolean z) {
        char charAt;
        String query = sentence.getQuery();
        String queryUpper = sentence.getQueryUpper();
        int i = 0;
        while (i < queryUpper.length() && ((charAt = queryUpper.charAt(i)) == '_' || Character.isLetterOrDigit(charAt))) {
            i++;
        }
        if (i == 0) {
            return query;
        }
        String substring = queryUpper.substring(0, i);
        if ("SET".equals(substring) || ParserUtil.isKeyword(substring, false)) {
            return query;
        }
        if (z) {
            sentence.addAlias(substring, sentence.getLastTable());
        }
        HashMap<String, DbTableOrView> aliases = sentence.getAliases();
        if ((aliases != null && aliases.containsKey(substring)) || sentence.getLastTable() == null) {
            if (z && query.length() == substring.length()) {
                return query;
            }
            String substring2 = query.substring(substring.length());
            if (substring2.length() == 0) {
                sentence.add(substring + ".", ".", 0);
            }
            return substring2;
        }
        HashSet<DbTableOrView> tables = sentence.getTables();
        if (tables != null) {
            String str = null;
            Iterator<DbTableOrView> it = tables.iterator();
            while (it.hasNext()) {
                DbTableOrView next = it.next();
                String upperEnglish = StringUtils.toUpperEnglish(next.getName());
                if (substring.startsWith(upperEnglish) && (str == null || upperEnglish.length() > str.length())) {
                    sentence.setLastMatchedTable(next);
                    str = upperEnglish;
                } else if (query.length() == 0 || upperEnglish.startsWith(substring)) {
                    sentence.add(upperEnglish + ".", upperEnglish.substring(query.length()) + ".", 0);
                }
            }
            if (str != null) {
                String substring3 = query.substring(str.length());
                if (substring3.length() == 0) {
                    sentence.add(substring + ".", ".", 0);
                }
                return substring3;
            }
        }
        return query;
    }
}
