package de.sg_o.test.tagy.testDb;

import com.couchbase.lite.CouchbaseLiteException;
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

    public TestDb() throws CouchbaseLiteException {
        tempDir = tempDirWithPrefix.toFile();
        //noinspection ResultOfMethodCallIgnored
        tempDir.mkdirs();
        assertTrue(tempDir.exists());
        assertTrue(tempDir.isDirectory());
        db = new File(tempDir.getAbsolutePath() + File.separator + "test.cblite2");
        DB.initDb(db, true);
        Project p1 = new Project("Test_Project_1", User.getLocalUser());
        Project p2 = new Project("Test_Project_2", User.getLocalUser());

        p1.save();
        p2.save();
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
