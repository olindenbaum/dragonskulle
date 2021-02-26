/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class ISyncVar extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static ISyncVar getRootAsISyncVar(ByteBuffer _bb) {
        return getRootAsISyncVar(_bb, new ISyncVar());
    }

    public static ISyncVar getRootAsISyncVar(ByteBuffer _bb, ISyncVar obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public ISyncVar __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public byte syncVarType() {
        int o = __offset(4);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public Table syncVar(Table obj) {
        int o = __offset(6);
        return o != 0 ? __union(obj, o + bb_pos) : null;
    }

    public static int createISyncVar(
            FlatBufferBuilder builder, byte sync_var_type, int sync_varOffset) {
        builder.startTable(2);
        ISyncVar.addSyncVar(builder, sync_varOffset);
        ISyncVar.addSyncVarType(builder, sync_var_type);
        return ISyncVar.endISyncVar(builder);
    }

    public static void startISyncVar(FlatBufferBuilder builder) {
        builder.startTable(2);
    }

    public static void addSyncVarType(FlatBufferBuilder builder, byte syncVarType) {
        builder.addByte(0, syncVarType, 0);
    }

    public static void addSyncVar(FlatBufferBuilder builder, int syncVarOffset) {
        builder.addOffset(1, syncVarOffset, 0);
    }

    public static int endISyncVar(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public ISyncVar get(int j) {
            return get(new ISyncVar(), j);
        }

        public ISyncVar get(ISyncVar obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
