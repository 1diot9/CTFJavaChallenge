package org.h2.command.ddl;

import cn.hutool.core.text.CharSequenceUtil;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateFunctionAlias.class */
public class CreateFunctionAlias extends SchemaCommand {
    private String aliasName;
    private String javaClassMethod;
    private boolean deterministic;
    private boolean ifNotExists;
    private boolean force;
    private String source;

    public CreateFunctionAlias(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        FunctionAlias newInstanceFromSource;
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        Schema schema = getSchema();
        if (schema.findFunctionOrAggregate(this.aliasName) != null) {
            if (!this.ifNotExists) {
                throw DbException.get(ErrorCode.FUNCTION_ALIAS_ALREADY_EXISTS_1, this.aliasName);
            }
            return 0L;
        }
        int objectId = getObjectId();
        if (this.javaClassMethod != null) {
            newInstanceFromSource = FunctionAlias.newInstance(schema, objectId, this.aliasName, this.javaClassMethod, this.force);
        } else {
            newInstanceFromSource = FunctionAlias.newInstanceFromSource(schema, objectId, this.aliasName, this.source, this.force);
        }
        newInstanceFromSource.setDeterministic(this.deterministic);
        database.addSchemaObject(this.session, newInstanceFromSource);
        return 0L;
    }

    public void setAliasName(String str) {
        this.aliasName = str;
    }

    public void setJavaClassMethod(String str) {
        this.javaClassMethod = StringUtils.replaceAll(str, CharSequenceUtil.SPACE, "");
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public void setDeterministic(boolean z) {
        this.deterministic = z;
    }

    public void setSource(String str) {
        this.source = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 24;
    }
}
