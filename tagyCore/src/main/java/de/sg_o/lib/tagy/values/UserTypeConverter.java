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

import de.sg_o.proto.tagy.UserProto;
import io.objectbox.converter.PropertyConverter;
import org.jetbrains.annotations.NotNull;

public class UserTypeConverter implements PropertyConverter<UserProto.UserType, Integer> {
    @Override
    @NotNull
    public UserProto.UserType convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return UserProto.UserType.INVALID;
        }
        UserProto.UserType userType = UserProto.UserType.forNumber(databaseValue);
        if (userType == null) userType = UserProto.UserType.INVALID;
        return userType;
    }

    @Override
    public Integer convertToDatabaseValue(UserProto.UserType userType) {
        return userType == null ? 0 : userType.getNumber();
    }
}
