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

package de.sg_o.lib.tagy;

import com.couchbase.lite.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.DbConstants;
import de.sg_o.lib.tagy.db.DbScope;
import de.sg_o.lib.tagy.db.QuerySpec;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.util.MetaDataIterator;
import de.sg_o.lib.tagy.values.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    @NotNull
    private final String projectName;
    @NotNull
    private final StructureDefinition structureDefinition;
    @NotNull
    final DataManager dataManager;
    private transient final Document oldDoc;
    @NotNull
    private transient final DbScope scope;
    @NotNull
    private final User user;

    public Project(@NotNull String projectName, @NotNull User user) {
        DbScope scope = DB.getScope(projectName);
        if (scope == null) {
            throw new NullPointerException("scope");
        }
        this.scope = scope;
        if (projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("projectName");
        }
        this.projectName = projectName.trim();
        this.structureDefinition = new StructureDefinition(this);
        this.dataManager = new DataManager(this);
        this.oldDoc = null;
        this.user = user;
    }

    public @NotNull String getProjectName() {
        return projectName;
    }

    public @NotNull StructureDefinition getStructureDefinition() {
        return structureDefinition;
    }

    @SuppressWarnings("unused")
    public @NotNull DbScope getScope() {
        return scope;
    }

    public @NotNull User getUser() {
        return user;
    }

    @SuppressWarnings("unused")
    public MutableDocument getEncoded() {
        MutableDocument encoded;
        if (oldDoc != null) {
            encoded = oldDoc.toMutable();
        } else {
            encoded = new MutableDocument(projectName);
        }
        return encoded;
    }

    public ArrayList<String> queryData(@NotNull String collection, QuerySpec queryBuilder, int count) {
        if (count < 32) count = 32;
        Collection scopeCollection = scope.getCollection(collection);
        if (scopeCollection == null) return null;
        From from = QueryBuilder
                .select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(scopeCollection));

        Query query = from;
        if (queryBuilder != null) {
            query = queryBuilder.buildQuery(from);
        }

        try (ResultSet resultSet = query.execute()) {
            ArrayList<String> ids = new ArrayList<>(count);
            for (Result result: resultSet) {
                String id = result.getString(DbConstants.ID_KEY);
                if (id == null) continue;
                ids.add(id);
            }
            return ids;
        } catch (CouchbaseLiteException e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public int countEntries(@NotNull String collection) {
        Collection scopeCollection = scope.getCollection(collection);
        if (scopeCollection == null) return 0;
        Query query = QueryBuilder
                .select(SelectResult.expression(Function.count(Expression.string("*"))).as("count"))
                .from(DataSource.collection(scopeCollection));
        int count = 0;
        try (ResultSet resultSet = query.execute()) {
            for (Result result: resultSet) {
                count += result.getInt("count");
            }
            return count;
        } catch (CouchbaseLiteException e) {
            return 0;
        }
    }

    public Document getData(@NotNull String collection, @NotNull String id) {
        Collection scopeCollection = scope.getCollection(collection);
        if (scopeCollection == null) return null;
        try {
            return scopeCollection.getDocument(id);
        } catch (CouchbaseLiteException e) {
            return null;
        }
    }

    public boolean saveData(@NotNull String collection, @NotNull List<MutableDocument> documents) {
        Collection scopeCollection = scope.getCollection(collection);
        if (scopeCollection == null) return false;
        for (MutableDocument document : documents) {
            try {
                scopeCollection.save(document);
            } catch (CouchbaseLiteException ignored) {
            }
        }
        return true;
    }

    public boolean saveData(@NotNull String collection, @NotNull MutableDocument document) {
        ArrayList<MutableDocument> documents = new ArrayList<>(1);
        documents.add(document);
        return saveData(collection, documents);
    }

    public boolean clearCollection(@NotNull String collection) {
        Collection scopeCollection = scope.getCollection(collection);
        if (scopeCollection == null) return false;
        try {
            Database db = DB.getDb();
            if (db == null) return false;
            db.deleteCollection(scopeCollection.getName(), scopeCollection.getScope().getName());
        } catch (CouchbaseLiteException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean save() {
        if (!structureDefinition.saveConfig()) return false;
        return dataManager.saveConfig();
    }

    @SuppressWarnings("unused")
    @JsonProperty("annotated")
    private MetaDataIterator getMetaDataIterator() {
        return new MetaDataIterator(this);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    @SuppressWarnings("unused")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        throw new IOException("Deserialization not supported");
    }

    @Override
    public String toString() {
        return "{" +
                "\"_id\": \"" + projectName + '\"' +
                ", \"structureDefinition\": \"" + structureDefinition + '\"' +
                '}';
    }
}
