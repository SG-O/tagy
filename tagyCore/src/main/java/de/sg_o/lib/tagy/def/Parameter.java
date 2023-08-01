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

import io.objectbox.converter.PropertyConverter;
import org.jetbrains.annotations.NotNull;

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

    public static class ParameterConverter implements PropertyConverter<Parameter, Integer> {
        @Override
        @NotNull
        public Parameter convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return Parameter.NONE;
            }
            for (Parameter role : Parameter.values()) {
                if (role.id == databaseValue) {
                    return role;
                }
            }
            return Parameter.NONE;
        }

        @Override
        public Integer convertToDatabaseValue(Parameter entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }
}
