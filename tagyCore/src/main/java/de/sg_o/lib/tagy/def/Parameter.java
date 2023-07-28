package de.sg_o.lib.tagy.def;

import java.util.HashMap;

public enum Parameter {
    /**
     * No parameter is used.
     */
    NONE,
    /**
     * Point where the interesting content starts in video or audio in seconds
     */
    IN,
    /**
     * Point where the interesting content ends in video or audio in seconds
     */
    OUT,
    /**
     * Total length of the video or audio in seconds
     */
    LENGTH;

    private final int id;
    private static final HashMap<Integer, Parameter> parameters = new HashMap<>();

    static {
        for (Parameter parameter : Parameter.values()) {
            parameters.put(parameter.getId(), parameter);
        }
    }

    Parameter() {
        id = this.ordinal();
    }

    public int getId() {
        return id;
    }

    public static Parameter getParameter(int id) {
        return parameters.get(id);
    }
}
