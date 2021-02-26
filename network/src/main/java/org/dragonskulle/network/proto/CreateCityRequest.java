/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class CreateCityRequest extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static CreateCityRequest getRootAsCreateCityRequest(ByteBuffer _bb) {
        return getRootAsCreateCityRequest(_bb, new CreateCityRequest());
    }

    public static CreateCityRequest getRootAsCreateCityRequest(
            ByteBuffer _bb, CreateCityRequest obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public CreateCityRequest __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public int owner() {
        int o = __offset(4);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public static int createCreateCityRequest(FlatBufferBuilder builder, int owner) {
        builder.startTable(1);
        CreateCityRequest.addOwner(builder, owner);
        return CreateCityRequest.endCreateCityRequest(builder);
    }

    public static void startCreateCityRequest(FlatBufferBuilder builder) {
        builder.startTable(1);
    }

    public static void addOwner(FlatBufferBuilder builder, int owner) {
        builder.addInt(0, owner, 0);
    }

    public static int endCreateCityRequest(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public CreateCityRequest get(int j) {
            return get(new CreateCityRequest(), j);
        }

        public CreateCityRequest get(CreateCityRequest obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
