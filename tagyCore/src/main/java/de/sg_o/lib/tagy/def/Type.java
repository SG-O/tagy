package de.sg_o.lib.tagy.def;

import java.util.HashMap;

public enum Type {
    LIST,
    LONG,
    DOUBLE,
    ENUM,
    STRING,
    DATE;

    private final int id;
    private static final HashMap<Integer, Type> types = new HashMap<>();

    static {
        for (Type type : Type.values()) {
            types.put(type.getId(), type);
        }
    }

    Type() {
        id = this.ordinal();
    }

    public int getId() {
        return id;
    }

    public static Type getType(int id) {
        return types.get(id);
    }


}
