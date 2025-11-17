package cn.hutool.db;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.MapUtil;
import java.sql.SQLException;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ActiveEntity.class */
public class ActiveEntity extends Entity {
    private static final long serialVersionUID = 6112321379601134750L;
    private final Db db;

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Entity parseBean(Object obj, boolean z, boolean z2) {
        return parseBean((ActiveEntity) obj, z, z2);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Entity parseBean(Object obj) {
        return parseBean((ActiveEntity) obj);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Entity setFields(Func0[] func0Arr) {
        return setFields((Func0<?>[]) func0Arr);
    }

    @Override // cn.hutool.db.Entity
    public /* bridge */ /* synthetic */ Entity setFieldNames(Collection collection) {
        return setFieldNames((Collection<String>) collection);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Dict setFields(Func0[] func0Arr) {
        return setFields((Func0<?>[]) func0Arr);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Dict parseBean(Object obj, boolean z, boolean z2) {
        return parseBean((ActiveEntity) obj, z, z2);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public /* bridge */ /* synthetic */ Dict parseBean(Object obj) {
        return parseBean((ActiveEntity) obj);
    }

    public static ActiveEntity create() {
        return new ActiveEntity();
    }

    public static ActiveEntity create(String tableName) {
        return new ActiveEntity(tableName);
    }

    public static <T> ActiveEntity parse(T bean) {
        return create((String) null).parseBean((ActiveEntity) bean);
    }

    public static <T> ActiveEntity parse(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        return create((String) null).parseBean((ActiveEntity) bean, isToUnderlineCase, ignoreNullValue);
    }

    public static <T> ActiveEntity parseWithUnderlineCase(T bean) {
        return create((String) null).parseBean((ActiveEntity) bean, true, true);
    }

    public ActiveEntity() {
        this(Db.use(), (String) null);
    }

    public ActiveEntity(String tableName) {
        this(Db.use(), tableName);
    }

    public ActiveEntity(Entity entity) {
        this(Db.use(), entity);
    }

    public ActiveEntity(Db db, String tableName) {
        super(tableName);
        this.db = db;
    }

    public ActiveEntity(Db db, Entity entity) {
        super(entity.getTableName());
        putAll(entity);
        this.db = db;
    }

    @Override // cn.hutool.db.Entity
    public ActiveEntity setTableName(String tableName) {
        return (ActiveEntity) super.setTableName(tableName);
    }

    @Override // cn.hutool.db.Entity
    public ActiveEntity setFieldNames(Collection<String> fieldNames) {
        return (ActiveEntity) super.setFieldNames(fieldNames);
    }

    @Override // cn.hutool.db.Entity
    public ActiveEntity setFieldNames(String... fieldNames) {
        return (ActiveEntity) super.setFieldNames(fieldNames);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public ActiveEntity setFields(Func0<?>... fields) {
        return (ActiveEntity) super.setFields(fields);
    }

    @Override // cn.hutool.db.Entity
    public ActiveEntity addFieldNames(String... fieldNames) {
        return (ActiveEntity) super.addFieldNames(fieldNames);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public <T> ActiveEntity parseBean(T bean) {
        return (ActiveEntity) super.parseBean((ActiveEntity) bean);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public <T> ActiveEntity parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        return (ActiveEntity) super.parseBean((ActiveEntity) bean, isToUnderlineCase, ignoreNullValue);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public ActiveEntity set(String field, Object value) {
        return (ActiveEntity) super.set(field, value);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict
    public ActiveEntity setIgnoreNull(String field, Object value) {
        return (ActiveEntity) super.setIgnoreNull(field, value);
    }

    @Override // cn.hutool.db.Entity, cn.hutool.core.lang.Dict, java.util.HashMap, java.util.AbstractMap
    public ActiveEntity clone() {
        return (ActiveEntity) super.clone();
    }

    public ActiveEntity add() {
        try {
            this.db.insert(this);
            return this;
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }

    public ActiveEntity load() {
        try {
            Entity result = this.db.get(this);
            if (MapUtil.isNotEmpty(result)) {
                putAll(result);
            }
            return this;
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }

    public ActiveEntity del() {
        try {
            this.db.del(this);
            return this;
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }

    public ActiveEntity update(String primaryKey) {
        try {
            this.db.update(this, Entity.create().set(primaryKey, get(primaryKey)));
            return this;
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }
}
