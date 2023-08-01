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

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class User implements Serializable {
    @Id
    Long id;
    @Convert(converter = UserType.UserTypeConverter.class, dbType = Integer.class)
    private final UserType userType;
    @NotNull
    private final String name;

    public static User getLocalUser() {
        User localUser = DB.queryFirst(User.class, qb -> qb.equal(User_.userType, UserType.LOCAL.getId()));
        if (localUser != null) return localUser;
        return new User("Local", UserType.LOCAL);
    }

    public User(Long id, UserType userType, @NotNull String name) {
        this.id = id;
        this.userType = userType;
        this.name = name;
    }

    public User(@NotNull String name, UserType userType) {
        this.userType = userType;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonProperty(value = "user", index = 0)
    public UserType getUserType() {
        return userType;
    }

    @JsonProperty(value = "name", index = 1)
    public @NotNull String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": " + id
                + ", \"name\": \"" + name + "\""
                + "}";
    }
}
