package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonParser;
import java.util.Iterator;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/TreeNode.class */
public interface TreeNode {
    JsonToken asToken();

    JsonParser.NumberType numberType();

    int size();

    boolean isValueNode();

    boolean isContainerNode();

    boolean isMissingNode();

    boolean isArray();

    boolean isObject();

    TreeNode get(String str);

    TreeNode get(int i);

    TreeNode path(String str);

    TreeNode path(int i);

    Iterator<String> fieldNames();

    TreeNode at(JsonPointer jsonPointer);

    TreeNode at(String str) throws IllegalArgumentException;

    JsonParser traverse();

    JsonParser traverse(ObjectCodec objectCodec);
}
