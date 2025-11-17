package cn.hutool.db;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.DSFactory;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/DaoTemplate.class */
public class DaoTemplate {
    protected String tableName;
    protected String primaryKeyField;
    protected Db db;

    public DaoTemplate(String tableName) {
        this(tableName, (String) null);
    }

    public DaoTemplate(String tableName, String primaryKeyField) {
        this(tableName, primaryKeyField, DSFactory.get());
    }

    public DaoTemplate(String tableName, DataSource ds) {
        this(tableName, (String) null, ds);
    }

    public DaoTemplate(String tableName, String primaryKeyField, DataSource ds) {
        this(tableName, primaryKeyField, Db.use(ds));
    }

    public DaoTemplate(String tableName, String primaryKeyField, Db db) {
        this.primaryKeyField = "id";
        this.tableName = tableName;
        if (StrUtil.isNotBlank(primaryKeyField)) {
            this.primaryKeyField = primaryKeyField;
        }
        this.db = db;
    }

    public int add(Entity entity) throws SQLException {
        return this.db.insert(fixEntity(entity));
    }

    public List<Object> addForGeneratedKeys(Entity entity) throws SQLException {
        return this.db.insertForGeneratedKeys(fixEntity(entity));
    }

    public Long addForGeneratedKey(Entity entity) throws SQLException {
        return this.db.insertForGeneratedKey(fixEntity(entity));
    }

    public <T> int del(T pk) throws SQLException {
        if (pk == null) {
            return 0;
        }
        return del(Entity.create(this.tableName).set(this.primaryKeyField, (Object) pk));
    }

    public <T> int del(String field, T value) throws SQLException {
        if (StrUtil.isBlank(field)) {
            return 0;
        }
        return del(Entity.create(this.tableName).set(field, (Object) value));
    }

    public <T> int del(Entity where) throws SQLException {
        if (MapUtil.isEmpty(where)) {
            return 0;
        }
        return this.db.del(fixEntity(where));
    }

    public int update(Entity record, Entity where) throws SQLException {
        if (MapUtil.isEmpty(record)) {
            return 0;
        }
        return this.db.update(fixEntity(record), where);
    }

    public int update(Entity entity) throws SQLException {
        if (MapUtil.isEmpty(entity)) {
            return 0;
        }
        Entity entity2 = fixEntity(entity);
        Object pk = entity2.get(this.primaryKeyField);
        if (null == pk) {
            throw new SQLException(StrUtil.format("Please determine `{}` for update", this.primaryKeyField));
        }
        Entity where = Entity.create(this.tableName).set(this.primaryKeyField, pk);
        Entity record = entity2.clone();
        record.remove(this.primaryKeyField);
        return this.db.update(record, where);
    }

    public int addOrUpdate(Entity entity) throws SQLException {
        return null == entity.get(this.primaryKeyField) ? add(entity) : update(entity);
    }

    public <T> Entity get(T pk) throws SQLException {
        return get(this.primaryKeyField, pk);
    }

    public <T> Entity get(String field, T value) throws SQLException {
        return get(Entity.create(this.tableName).set(field, (Object) value));
    }

    public Entity get(Entity where) throws SQLException {
        return this.db.get(fixEntity(where));
    }

    public <T> List<Entity> find(String field, T value) throws SQLException {
        return find(Entity.create(this.tableName).set(field, (Object) value));
    }

    public List<Entity> findAll() throws SQLException {
        return find(Entity.create(this.tableName));
    }

    public List<Entity> find(Entity where) throws SQLException {
        return this.db.find((Collection<String>) null, fixEntity(where));
    }

    public List<Entity> findBySql(String sql, Object... params) throws SQLException {
        String selectKeyword = StrUtil.subPre(sql.trim(), 6).toLowerCase();
        if (false == "select".equals(selectKeyword)) {
            sql = "SELECT * FROM " + this.tableName + CharSequenceUtil.SPACE + sql;
        }
        return this.db.query(sql, params);
    }

    public PageResult<Entity> page(Entity where, Page page, String... selectFields) throws SQLException {
        return this.db.page(Arrays.asList(selectFields), fixEntity(where), page);
    }

    public PageResult<Entity> page(Entity where, Page page) throws SQLException {
        return this.db.page(fixEntity(where), page);
    }

    public long count(Entity where) throws SQLException {
        return this.db.count(fixEntity(where));
    }

    public boolean exist(Entity where) throws SQLException {
        return count(where) > 0;
    }

    private Entity fixEntity(Entity entity) {
        if (null == entity) {
            entity = Entity.create(this.tableName);
        } else if (StrUtil.isBlank(entity.getTableName())) {
            entity.setTableName(this.tableName);
        }
        return entity;
    }
}
