/*
 *
 *      Copyright (C) 2023 Joerg Bayer (SG-O)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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