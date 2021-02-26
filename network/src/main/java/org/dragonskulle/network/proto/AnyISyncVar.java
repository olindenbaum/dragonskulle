/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.proto;

public final class AnyISyncVar {
    private AnyISyncVar() {}

    public static final byte NONE = 0;
    public static final byte ISyncBool = 1;
    public static final byte ISyncString = 2;
    public static final byte ISyncLong = 3;
    public static final byte ISyncInt = 4;
    public static final byte ISyncFloat = 5;

    public static final String[] names = {
        "NONE", "ISyncBool", "ISyncString", "ISyncLong", "ISyncInt", "ISyncFloat",
    };

    public static String name(int e) {
        return names[e];
    }
}
