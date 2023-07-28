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

package de.sg_o.lib.tagy.db;

import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Scope;
import org.jetbrains.annotations.NotNull;

public class DbScope {
    @NotNull
    private final Scope scope;
    @NotNull
    private final Database db;

    public DbScope(@NotNull Scope scope, @NotNull Database db) {
        this.scope = scope;
        this.db = db;
    }

    @SuppressWarnings("unused")
    public @NotNull Scope getScope() {
        return scope;
    }

    @SuppressWarnings("unused")
    public @NotNull Database getDb() {
        return db;
    }

    public Collection getCollection(@NotNull String name) {
        try {
            Collection collection = scope.getCollection(name);
            if (collection == null) {
                collection = db.createCollection(name, scope.getName());
            }
            return collection;
        } catch (CouchbaseLiteException e) {
            return null;
        }
    }
}
