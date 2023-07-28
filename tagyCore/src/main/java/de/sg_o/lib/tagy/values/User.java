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

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.db.DbConstants;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class User implements Serializable {
    private final long id;
    @NotNull
    private final String name;

    public static User getLocalUser() {
        return new User(0, "Local");
    }

    public User(long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public User(@NotNull Dictionary dictionary) {
        if (!dictionary.contains(DbConstants.USER_ID_KEY)) {
            throw new IllegalArgumentException("Dictionary must contain id");
        }
        this.id = dictionary.getLong(DbConstants.USER_ID_KEY);
        String name = dictionary.getString(DbConstants.USER_NAME_KEY);
        if (name == null) {
            throw new IllegalArgumentException("Dictionary must contain name");
        }
        this.name = name;
    }

    public MutableDictionary getEncoded() {
        MutableDictionary dictionary = new MutableDictionary();
        dictionary.setLong(DbConstants.USER_ID_KEY, id);
        dictionary.setString(DbConstants.USER_NAME_KEY, name);
        return dictionary;
    }


    @Override
    public String toString() {
        return "{"
                + "\"id\": " + id
                + ", \"name\": \"" + name + "\""
                + "}";
    }
}
