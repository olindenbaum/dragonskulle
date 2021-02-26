/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class ISyncInt extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static ISyncInt getRootAsISyncInt(ByteBuffer _bb) {
        return getRootAsISyncInt(_bb, new ISyncInt());
    }

    public static ISyncInt getRootAsISyncInt(ByteBuffer _bb, ISyncInt obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public ISyncInt __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public String id() {
        int o = __offset(4);
        return o != 0 ? __string(o + bb_pos) : null;
    }

    public ByteBuffer idAsByteBuffer() {
        return __vector_as_bytebuffer(4, 1);
    }

    public ByteBuffer idInByteBuffer(ByteBuffer _bb) {
        return __vector_in_bytebuffer(_bb, 4, 1);
    }

    public int data() {
        int o = __offset(6);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public static int createISyncInt(FlatBufferBuilder builder, int idOffset, int data) {
        builder.startTable(2);
        ISyncInt.addData(builder, data);
        ISyncInt.addId(builder, idOffset);
        return ISyncInt.endISyncInt(builder);
    }

    public static void startISyncInt(FlatBufferBuilder builder) {
        builder.startTable(2);
    }

    public static void addId(FlatBufferBuilder builder, int idOffset) {
        builder.addOffset(0, idOffset, 0);
    }

    public static void addData(FlatBufferBuilder builder, int data) {
        builder.addInt(1, data, 0);
    }

    public static int endISyncInt(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public ISyncInt get(int j) {
            return get(new ISyncInt(), j);
        }

        public ISyncInt get(ISyncInt obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
