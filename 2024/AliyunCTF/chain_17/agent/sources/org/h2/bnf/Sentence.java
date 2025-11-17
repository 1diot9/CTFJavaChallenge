package org.h2.bnf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/Sentence.class */
public class Sentence {
    public static final int CONTEXT = 0;
    public static final int KEYWORD = 1;
    public static final int FUNCTION = 2;
    private static final int MAX_PROCESSING_TIME = 100;
    private final HashMap<String, String> next = new HashMap<>();
    private String query;
    private String queryUpper;
    private long stopAtNs;
    private DbSchema lastMatchedSchema;
    private DbTableOrView lastMatchedTable;
    private DbTableOrView lastTable;
    private HashSet<DbTableOrView> tables;
    private HashMap<String, DbTableOrView> aliases;

    public void start() {
        this.stopAtNs = System.nanoTime() + 100000000;
    }

    public void stopIfRequired() {
        if (System.nanoTime() - this.stopAtNs > 0) {
            throw new IllegalStateException();
        }
    }

    public void add(String str, String str2, int i) {
        this.next.put(i + "#" + str, str2);
    }

    public void addAlias(String str, DbTableOrView dbTableOrView) {
        if (this.aliases == null) {
            this.aliases = new HashMap<>();
        }
        this.aliases.put(str, dbTableOrView);
    }

    public void addTable(DbTableOrView dbTableOrView) {
        this.lastTable = dbTableOrView;
        if (this.tables == null) {
            this.tables = new HashSet<>();
        }
        this.tables.add(dbTableOrView);
    }

    public HashSet<DbTableOrView> getTables() {
        return this.tables;
    }

    public HashMap<String, DbTableOrView> getAliases() {
        return this.aliases;
    }

    public DbTableOrView getLastTable() {
        return this.lastTable;
    }

    public DbSchema getLastMatchedSchema() {
        return this.lastMatchedSchema;
    }

    public void setLastMatchedSchema(DbSchema dbSchema) {
        this.lastMatchedSchema = dbSchema;
    }

    public void setLastMatchedTable(DbTableOrView dbTableOrView) {
        this.lastMatchedTable = dbTableOrView;
    }

    public DbTableOrView getLastMatchedTable() {
        return this.lastMatchedTable;
    }

    public void setQuery(String str) {
        if (!Objects.equals(this.query, str)) {
            this.query = str;
            this.queryUpper = StringUtils.toUpperEnglish(str);
        }
    }

    public String getQuery() {
        return this.query;
    }

    public String getQueryUpper() {
        return this.queryUpper;
    }

    public HashMap<String, String> getNext() {
        return this.next;
    }
}
