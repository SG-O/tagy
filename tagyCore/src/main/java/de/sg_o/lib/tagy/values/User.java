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
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.Project_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.proto.tagy.UserProto;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class User implements Serializable {
    private static final UserTypeConverter userTypeConverter = new UserTypeConverter();

    @Id
    Long id;
    @Convert(converter = UserTypeConverter.class, dbType = Integer.class)
    private final UserProto.UserType userType;
    @NotNull
    private final String name;

    public static User getLocalUser() {
        User localUser = DB.queryFirst(User.class,
                qb -> qb.equal(User_.userType, userTypeConverter.convertToDatabaseValue(UserProto.UserType.LOCAL)));
        if (localUser != null) return localUser;
        localUser = new User("Local", UserProto.UserType.LOCAL);
        localUser.save();
        return localUser;
    }

    public User(Long id, UserProto.UserType userType, @NotNull String name) {
        this.id = id;
        this.userType = userType;
        this.name = name;
    }

    public User(@NotNull String name, UserProto.UserType userType) {
        this.userType = userType;
        this.name = name;
    }

    public User (@NotNull UserProto.User proto) {
        this.name = proto.getName();
        this.userType = proto.getUserType();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonProperty(value = "user", index = 0)
    public UserProto.UserType getUserType() {
        return userType;
    }

    @JsonProperty(value = "name", index = 1)
    public @NotNull String getName() {
        return name;
    }

    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<User> box = db.boxFor(User.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @SuppressWarnings("unused")
    public boolean delete() {
        if (this.userType == UserProto.UserType.LOCAL) return false;
        QueryBoxSpec<Project> qbs = qb -> {
            qb = qb.equal(Project_.id, getId());
            return qb;
        };
        this.id = 0L;
        return DB.delete(Project.class, qbs);
    }

    public @NotNull UserProto.User getAsProto() {
        UserProto.User.Builder builder = UserProto.User.newBuilder();
        builder.setName(this.name);
        builder.setUserType(this.userType);
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getUserType() == user.getUserType() && Objects.equals(getName(), user.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserType(), getName());
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": " + id
                + ", \"name\": \"" + name + "\""
                + "}";
    }
}
