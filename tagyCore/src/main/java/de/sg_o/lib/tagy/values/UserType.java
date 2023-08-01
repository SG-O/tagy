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

package de.sg_o.lib.tagy.values;

import io.objectbox.converter.PropertyConverter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public enum UserType {
    UNKNOWN,
    LOCAL,
    NETWORKED;

    private final int id;
    private static final HashMap<Integer, UserType> userTypes = new HashMap<>();

    static {
        for (UserType userType : UserType.values()) {
            userTypes.put(userType.getId(), userType);
        }
    }

    UserType() {
        id = this.ordinal();
    }

    public int getId() {
        return id;
    }

    public static UserType getUserType(int id) {
        return userTypes.get(id);
    }

    public static class UserTypeConverter implements PropertyConverter<UserType, Integer> {
        @Override
        @NotNull
        public UserType convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return UserType.UNKNOWN;
            }
            for (UserType role : UserType.values()) {
                if (role.id == databaseValue) {
                    return role;
                }
            }
            return UserType.UNKNOWN;
        }

        @Override
        public Integer convertToDatabaseValue(UserType entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }
}