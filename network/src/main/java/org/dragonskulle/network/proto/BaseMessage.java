/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class BaseMessage extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static BaseMessage getRootAsBaseMessage(ByteBuffer _bb) {
        return getRootAsBaseMessage(_bb, new BaseMessage());
    }

    public static BaseMessage getRootAsBaseMessage(ByteBuffer _bb, BaseMessage obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public BaseMessage __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public int code() {
        int o = __offset(4);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public byte dataType() {
        int o = __offset(6);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public Table data(Table obj) {
        int o = __offset(8);
        return o != 0 ? __union(obj, o + bb_pos) : null;
    }

    public static int createBaseMessage(
            FlatBufferBuilder builder, int code, byte data_type, int dataOffset) {
        builder.startTable(3);
        BaseMessage.addData(builder, dataOffset);
        BaseMessage.addCode(builder, code);
        BaseMessage.addDataType(builder, data_type);
        return BaseMessage.endBaseMessage(builder);
    }

    public static void startBaseMessage(FlatBufferBuilder builder) {
        builder.startTable(3);
    }

    public static void addCode(FlatBufferBuilder builder, int code) {
        builder.addInt(0, code, 0);
    }

    public static void addDataType(FlatBufferBuilder builder, byte dataType) {
        builder.addByte(1, dataType, 0);
    }

    public static void addData(FlatBufferBuilder builder, int dataOffset) {
        builder.addOffset(2, dataOffset, 0);
    }

    public static int endBaseMessage(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public BaseMessage get(int j) {
            return get(new BaseMessage(), j);
        }

        public BaseMessage get(BaseMessage obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
