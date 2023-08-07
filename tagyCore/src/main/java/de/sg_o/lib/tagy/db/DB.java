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

import de.sg_o.lib.tagy.MyObjectBox;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.query.Query;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private static BoxStore database;
    private static @NotNull String name = "";

    private static boolean debug = false;


    public static void initDb(File file, boolean allowCreate) {
        if (database != null) return;
        if (file == null) return;
        if (!allowCreate) {
            if (!file.isDirectory()) return;
            if (!file.exists()) return;
        }
        name = file.getName();
        BoxStoreBuilder dbBuilder = MyObjectBox.builder();
        if (debug) {
            dbBuilder = dbBuilder.debugFlags(511);
        }
        database = dbBuilder.directory(file).build();
        Runtime.getRuntime().addShutdownHook(new Thread(DB::cleanup));
    }

    public static BoxStore getDb() {
        return database;
    }

    @SuppressWarnings("unused")
    public static void setDebug(boolean debug) {
        DB.debug = debug;
    }

    public static <T> List<T> query(Class<T> entityClass, QueryBoxSpec<T> queryBoxSpec, int length, int offset) {
        List<T> results = new ArrayList<>();
        BoxStore db = getDb();
        if (db == null) return results;
        Box<T> box = db.boxFor(entityClass);
        if (box == null) return results;
        try (Query<T> query = queryBoxSpec.buildQuery(box.query()).build()) {
            if (length > 0 && offset > -1) {
                results = query.find(offset, length);
            } else {
                results = query.findLazyCached();
            }
        }
        db.closeThreadResources();
        return results;
    }

    public static <T> T queryFirst(Class<T> entityClass,  QueryBoxSpec<T> queryBoxSpec) {
        BoxStore db = getDb();
        if (db == null) return null;
        Box<T> box = db.boxFor(entityClass);
        if (box == null) return null;
        T data;
        try (Query<T> query = queryBoxSpec.buildQuery(box.query()).build()) {
            data = query.findFirst();
        }
        db.closeThreadResources();
        return data;
    }

    public static @NotNull String getName() {
        return name;
    }

    public static <T> boolean delete(Class<T> entityClass, QueryBoxSpec<T> queryBoxSpec) {
        BoxStore db = getDb();
        if (db == null) return false;
        Box<T> box = db.boxFor(entityClass);
        if (box == null) return false;
        try (Query<T> query = queryBoxSpec.buildQuery(box.query()).build()) {
            query.remove();
        }
        db.closeThreadResources();
        return true;
    }

    public static void closeDb() {
        if (database == null) return;
        database.close();
        database = null;
    }

    public static void cleanup() {
        if (database == null) return;
        database.close();
    }
}
