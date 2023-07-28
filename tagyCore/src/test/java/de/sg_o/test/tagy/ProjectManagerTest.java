package de.sg_o.test.tagy;

import com.couchbase.lite.CouchbaseLiteException;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.ProjectManager;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectManagerTest {
    ProjectManager pm;

    @BeforeEach
    void setUp() throws CouchbaseLiteException {
        pm = new ProjectManager();
        new TestDb();
    }

    @Test
    void listProjects() {
        List<String> projects = pm.listProjects();
        assertTrue(projects.contains("Test_Project_1"));
        assertTrue(projects.contains("Test_Project_2"));
    }

    @AfterEach
    void tearDown() throws CouchbaseLiteException {
        DB.closeDb();
    }
}