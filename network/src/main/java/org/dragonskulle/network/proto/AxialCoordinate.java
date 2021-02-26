/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

import com.google.flatbuffers.*;
import java.nio.*;
import java.util.*;

@SuppressWarnings("unused")
public final class AxialCoordinate extends Table {
    public static void ValidateVersion() {
        Constants.FLATBUFFERS_1_12_0();
    }

    public static AxialCoordinate getRootAsAxialCoordinate(ByteBuffer _bb) {
        return getRootAsAxialCoordinate(_bb, new AxialCoordinate());
    }

    public static AxialCoordinate getRootAsAxialCoordinate(ByteBuffer _bb, AxialCoordinate obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public void __init(int _i, ByteBuffer _bb) {
        __reset(_i, _bb);
    }

    public AxialCoordinate __assign(int _i, ByteBuffer _bb) {
        __init(_i, _bb);
        return this;
    }

    public int rawQ() {
        int o = __offset(4);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public int rawR() {
        int o = __offset(6);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public int rawS() {
        int o = __offset(8);
        return o != 0 ? bb.getInt(o + bb_pos) : 0;
    }

    public static int createAxialCoordinate(
            FlatBufferBuilder builder, int rawQ, int rawR, int rawS) {
        builder.startTable(3);
        AxialCoordinate.addRawS(builder, rawS);
        AxialCoordinate.addRawR(builder, rawR);
        AxialCoordinate.addRawQ(builder, rawQ);
        return AxialCoordinate.endAxialCoordinate(builder);
    }

    public static void startAxialCoordinate(FlatBufferBuilder builder) {
        builder.startTable(3);
    }

    public static void addRawQ(FlatBufferBuilder builder, int rawQ) {
        builder.addInt(0, rawQ, 0);
    }

    public static void addRawR(FlatBufferBuilder builder, int rawR) {
        builder.addInt(1, rawR, 0);
    }

    public static void addRawS(FlatBufferBuilder builder, int rawS) {
        builder.addInt(2, rawS, 0);
    }

    public static int endAxialCoordinate(FlatBufferBuilder builder) {
        int o = builder.endTable();
        return o;
    }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
            __reset(_vector, _element_size, _bb);
            return this;
        }

        public AxialCoordinate get(int j) {
            return get(new AxialCoordinate(), j);
        }

        public AxialCoordinate get(AxialCoordinate obj, int j) {
            return obj.__assign(__indirect(__element(j), bb), bb);
        }
    }
}
