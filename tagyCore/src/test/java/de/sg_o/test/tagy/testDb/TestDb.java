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

package de.sg_o.test.tagy.testDb;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.values.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDb {
    private static final Path tempDirWithPrefix;

    static {
        try {
            tempDirWithPrefix = Files.createTempDirectory("tagy");
            Runtime.getRuntime().addShutdownHook(new Thread(TestDb::cleanup));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    File tempDir;
    File db;

    public TestDb() {
        tempDir = tempDirWithPrefix.toFile();
        //noinspection ResultOfMethodCallIgnored
        tempDir.mkdirs();
        assertTrue(tempDir.exists());
        assertTrue(tempDir.isDirectory());
        db = new File(tempDir.getAbsolutePath() + File.separator + "test");
        DB.initDb(db, true);
        Project.openOrCreate("Demo_Project_0", User.getLocalUser()).save();
        Project.openOrCreate("Demo_Project_1", User.getLocalUser()).save();
    }

    @SuppressWarnings("unused")
    public File getTempDir() {
        return tempDir;
    }

    @SuppressWarnings("unused")
    public File getDb() {
        return db;
    }

    public static void cleanup() {
        try {
            DB.closeDb();
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
