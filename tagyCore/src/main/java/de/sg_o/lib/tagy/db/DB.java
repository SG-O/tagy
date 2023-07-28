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

import com.couchbase.lite.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class DB {

    private static final List<String> LIBRARIES = Arrays.asList("libicudata.so.71", "libicuuc.so.71", "libicui18n.so.71");

    private static Database database;
    private static Path tempDirWithPrefix;

    private static final ArrayList<File> tempLibraries = new ArrayList<>();


    public static void initDb(File file, boolean allowCreate) throws CouchbaseLiteException {
        if (database != null) return;
        if (file == null) return;
        if (!allowCreate) {
            if (!file.isDirectory()) return;
            if (!file.exists()) return;
        }
        file = file.getAbsoluteFile();
        String directory = file.getParent();
        if (directory == null) return;
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }

        try {
            loadLibraries();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CouchbaseLite.init();
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(directory);
        database = new Database(name, config);
        Runtime.getRuntime().addShutdownHook(new Thread(DB::cleanup));
    }

    public static Database getDb() {
        return database;
    }

    public static DbScope getScope(String scope) {
        Database db = getDb();
        if (db == null) return null;
        try {
            if (scope == null) {
                return new DbScope(db.getDefaultScope(), db);
            }
            Scope readScope = db.getScope(scope);
            if (readScope == null) {
                readScope = db.createCollection("init", scope).getScope();
            }
            return new DbScope(readScope, db);
        } catch (CouchbaseLiteException e) {
            return null;
        }
    }

    public static Set<Scope> listScopes() {
        Database db = getDb();
        if (db == null) return new HashSet<>();
        try {
            return db.getScopes();
        } catch (CouchbaseLiteException e) {
            return new HashSet<>();
        }
    }

    public static void closeDb() throws CouchbaseLiteException {
        if (database == null) return;
        database.close();
        database = null;
    }

    public static void cleanup() {
        if (database == null) return;
        try {
            database.close();
        } catch (CouchbaseLiteException ignore) {
        }
    }

    private static void loadLibraries() throws IOException {
        if (!tempLibraries.isEmpty()) return;
        tempDirWithPrefix = Files.createTempDirectory("tagy");
        Runtime.getRuntime().addShutdownHook(new Thread(DB::cleanupTmp));

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String prefix = "/libs/" + osName + "/" + osArch + "/";

        for (String library : LIBRARIES) {
            try(InputStream lib = DB.class.getResourceAsStream(prefix + library)) {
                if (lib == null) continue;
                File tempFile = new File(tempDirWithPrefix.toFile(), library);
                //noinspection ResultOfMethodCallIgnored
                tempFile.getParentFile().mkdirs();
                Files.copy(lib, tempFile.toPath());
                System.load(tempFile.getAbsolutePath());
                tempLibraries.add(tempFile);
            }
        }
    }

    public static void cleanupTmp() {
        try {
            Files.walkFileTree(tempDirWithPrefix,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException ignore) {
        }
    }
}
