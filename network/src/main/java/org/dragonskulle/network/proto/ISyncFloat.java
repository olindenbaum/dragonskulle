/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class ISyncFloat extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static ISyncFloat getRootAsISyncFloat(ByteBuffer _bb) {
        return getRootAsISyncFloat(_bb, new ISyncFloat());
    }

    public static ISyncFloat getRootAsISyncFloat(ByteBuffer _bb, ISyncFloat obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public ISyncFloat __assign(int _i, ByteBuffer _bb) {
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

    public float data() {
        int o = __offset(6);
        return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f;
    }

    public static int createISyncFloat(FlatBufferBuilder builder, int idOffset, float data) {
        builder.startTable(2);
        ISyncFloat.addData(builder, data);
        ISyncFloat.addId(builder, idOffset);
        return ISyncFloat.endISyncFloat(builder);
    }

    public static void startISyncFloat(FlatBufferBuilder builder) {
        builder.startTable(2);
    }

    public static void addId(FlatBufferBuilder builder, int idOffset) {
        builder.addOffset(0, idOffset, 0);
    }

    public static void addData(FlatBufferBuilder builder, float data) {
        builder.addFloat(1, data, 0.0f);
    }

    public static int endISyncFloat(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public ISyncFloat get(int j) {
            return get(new ISyncFloat(), j);
        }

        public ISyncFloat get(ISyncFloat obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
