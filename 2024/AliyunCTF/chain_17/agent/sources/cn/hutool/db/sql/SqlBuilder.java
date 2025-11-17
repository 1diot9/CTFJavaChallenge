package cn.hutool.db.sql;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.Entity;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.dialect.impl.OracleDialect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlBuilder.class */
public class SqlBuilder implements Builder<String> {
    private static final long serialVersionUID = 1;
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> paramValues = new ArrayList();
    private Wrapper wrapper;

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/SqlBuilder$Join.class */
    public enum Join {
        INNER,
        LEFT,
        RIGHT,
        FULL
    }

    public static SqlBuilder create() {
        return new SqlBuilder();
    }

    public static SqlBuilder create(Wrapper wrapper) {
        return new SqlBuilder(wrapper);
    }

    public static SqlBuilder of(CharSequence sql) {
        return create().append(sql);
    }

    public static void validateEntity(Entity entity) throws DbRuntimeException {
        if (null == entity) {
            throw new DbRuntimeException("Entity is null !");
        }
        if (StrUtil.isBlank(entity.getTableName())) {
            throw new DbRuntimeException("Entity`s table name is null !");
        }
        if (entity.isEmpty()) {
            throw new DbRuntimeException("No filed and value in this entity !");
        }
    }

    public SqlBuilder() {
    }

    public SqlBuilder(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    public SqlBuilder insert(Entity entity) {
        return insert(entity, DialectName.ANSI);
    }

    public SqlBuilder insert(Entity entity, DialectName dialectName) {
        return insert(entity, dialectName.name());
    }

    public SqlBuilder insert(Entity entity, String dialectName) {
        validateEntity(entity);
        boolean isOracle = DialectName.ORACLE.match(dialectName);
        StringBuilder fieldsPart = new StringBuilder();
        StringBuilder placeHolder = new StringBuilder();
        entity.forEach((field, value) -> {
            if (StrUtil.isNotBlank(field)) {
                if (fieldsPart.length() > 0) {
                    fieldsPart.append(", ");
                    placeHolder.append(", ");
                }
                fieldsPart.append(null != this.wrapper ? this.wrapper.wrap(field) : field);
                if (isOracle && OracleDialect.isNextVal(value)) {
                    placeHolder.append(value);
                } else {
                    placeHolder.append(CoreConstants.NA);
                    this.paramValues.add(value);
                }
            }
        });
        if (DialectName.PHOENIX.match(dialectName)) {
            this.sql.append("UPSERT INTO ");
        } else {
            this.sql.append("INSERT INTO ");
        }
        String tableName = entity.getTableName();
        if (null != this.wrapper) {
            tableName = this.wrapper.wrap(tableName);
        }
        this.sql.append(tableName).append(" (").append((CharSequence) fieldsPart).append(") VALUES (").append((CharSequence) placeHolder).append(")");
        return this;
    }

    public SqlBuilder delete(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            throw new DbRuntimeException("Table name is blank !");
        }
        if (null != this.wrapper) {
            tableName = this.wrapper.wrap(tableName);
        }
        this.sql.append("DELETE FROM ").append(tableName);
        return this;
    }

    public SqlBuilder update(Entity entity) {
        validateEntity(entity);
        String tableName = entity.getTableName();
        if (null != this.wrapper) {
            tableName = this.wrapper.wrap(tableName);
        }
        this.sql.append("UPDATE ").append(tableName).append(" SET ");
        entity.forEach((field, value) -> {
            if (StrUtil.isNotBlank(field)) {
                if (this.paramValues.size() > 0) {
                    this.sql.append(", ");
                }
                this.sql.append(null != this.wrapper ? this.wrapper.wrap(field) : field).append(" = ? ");
                this.paramValues.add(value);
            }
        });
        return this;
    }

    public SqlBuilder select(boolean isDistinct, String... fields) {
        return select(isDistinct, Arrays.asList(fields));
    }

    public SqlBuilder select(boolean isDistinct, Collection<String> fields) {
        this.sql.append("SELECT ");
        if (isDistinct) {
            this.sql.append("DISTINCT ");
        }
        if (CollectionUtil.isEmpty((Collection<?>) fields)) {
            this.sql.append("*");
        } else {
            if (null != this.wrapper) {
                fields = this.wrapper.wrap(fields);
            }
            this.sql.append(CollectionUtil.join(fields, ","));
        }
        return this;
    }

    public SqlBuilder select(String... fields) {
        return select(false, fields);
    }

    public SqlBuilder select(Collection<String> fields) {
        return select(false, fields);
    }

    public SqlBuilder from(String... tableNames) {
        if (ArrayUtil.isEmpty((Object[]) tableNames) || StrUtil.hasBlank(tableNames)) {
            throw new DbRuntimeException("Table name is blank in table names !");
        }
        if (null != this.wrapper) {
            tableNames = this.wrapper.wrap(tableNames);
        }
        this.sql.append(" FROM ").append(ArrayUtil.join((Object[]) tableNames, (CharSequence) ","));
        return this;
    }

    public SqlBuilder where(Condition... conditions) {
        if (ArrayUtil.isNotEmpty((Object[]) conditions)) {
            where(buildCondition(conditions));
        }
        return this;
    }

    public SqlBuilder where(String where) {
        if (StrUtil.isNotBlank(where)) {
            this.sql.append(" WHERE ").append(where);
        }
        return this;
    }

    public <T> SqlBuilder in(String field, T... values) {
        this.sql.append(this.wrapper.wrap(field)).append(" IN ").append("(").append(ArrayUtil.join((Object[]) values, (CharSequence) ",")).append(")");
        return this;
    }

    public SqlBuilder groupBy(String... fields) {
        if (ArrayUtil.isNotEmpty((Object[]) fields)) {
            if (null != this.wrapper) {
                fields = this.wrapper.wrap(fields);
            }
            this.sql.append(" GROUP BY ").append(ArrayUtil.join((Object[]) fields, (CharSequence) ","));
        }
        return this;
    }

    public SqlBuilder having(Condition... conditions) {
        if (ArrayUtil.isNotEmpty((Object[]) conditions)) {
            having(buildCondition(conditions));
        }
        return this;
    }

    public SqlBuilder having(String having) {
        if (StrUtil.isNotBlank(having)) {
            this.sql.append(" HAVING ").append(having);
        }
        return this;
    }

    public SqlBuilder orderBy(Order... orders) {
        if (ArrayUtil.isEmpty((Object[]) orders)) {
            return this;
        }
        this.sql.append(" ORDER BY ");
        boolean isFirst = true;
        for (Order order : orders) {
            String field = order.getField();
            if (null != this.wrapper) {
                field = this.wrapper.wrap(field);
            }
            if (!StrUtil.isBlank(field)) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    this.sql.append(",");
                }
                this.sql.append(field);
                Direction direction = order.getDirection();
                if (null != direction) {
                    this.sql.append(CharSequenceUtil.SPACE).append(direction);
                }
            }
        }
        return this;
    }

    public SqlBuilder join(String tableName, Join join) {
        if (StrUtil.isBlank(tableName)) {
            throw new DbRuntimeException("Table name is blank !");
        }
        if (null != join) {
            this.sql.append(CharSequenceUtil.SPACE).append(join).append(" JOIN ");
            if (null != this.wrapper) {
                tableName = this.wrapper.wrap(tableName);
            }
            this.sql.append(tableName);
        }
        return this;
    }

    public SqlBuilder on(Condition... conditions) {
        if (ArrayUtil.isNotEmpty((Object[]) conditions)) {
            on(buildCondition(conditions));
        }
        return this;
    }

    public SqlBuilder on(String on) {
        if (StrUtil.isNotBlank(on)) {
            this.sql.append(" ON ").append(on);
        }
        return this;
    }

    public SqlBuilder insertPreFragment(Object sqlFragment) {
        if (null != sqlFragment) {
            this.sql.insert(0, sqlFragment);
        }
        return this;
    }

    public SqlBuilder append(Object sqlFragment) {
        if (null != sqlFragment) {
            this.sql.append(sqlFragment);
        }
        return this;
    }

    public SqlBuilder addParams(Object... params) {
        if (ArrayUtil.isNotEmpty(params)) {
            Collections.addAll(this.paramValues, params);
        }
        return this;
    }

    public SqlBuilder query(Query query) {
        return select(query.getFields()).from(query.getTableNames()).where(query.getWhere());
    }

    public List<Object> getParamValues() {
        return this.paramValues;
    }

    public Object[] getParamValueArray() {
        return this.paramValues.toArray(new Object[0]);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public String build() {
        return this.sql.toString();
    }

    public String toString() {
        return build();
    }

    private String buildCondition(Condition... conditions) {
        if (ArrayUtil.isEmpty((Object[]) conditions)) {
            return "";
        }
        if (null != this.wrapper) {
            conditions = this.wrapper.wrap(conditions);
        }
        return ConditionBuilder.of(conditions).build(this.paramValues);
    }
}
