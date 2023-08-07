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

package de.sg_o.test.tagy.data;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.Directory;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {
    DataManager manager0;
    DataManager manager1;
    DataManager manager2;

    final List<Directory> directories0 = new ArrayList<>();
    final List<Directory> directories1 = new ArrayList<>();
    final List<Directory> directories2 = new ArrayList<>();

    Project project0;
    Project project1;
    Project project2;

    @BeforeEach
    void setUp() throws URISyntaxException {
        URL sampleMediaFolder = this.getClass().getResource("/sampleFiles/media");
        assertNotNull(sampleMediaFolder);
        File sampleMediaFile = new File(sampleMediaFolder.toURI());
        URL sampleMixedFolder = this.getClass().getResource("/sampleFiles/mixed");
        assertNotNull(sampleMixedFolder);
        File sampleMixedFile = new File(sampleMixedFolder.toURI());
        URL sampleUrlList = this.getClass().getResource("/sampleFiles/urlList.txt");
        assertNotNull(sampleUrlList);
        File sampleUrlListFile = new File(sampleUrlList.toURI());

        Directory dir = new Directory(sampleMediaFile, true);
        dir.setFileExtensions(".jpg, png, .mp4, wmv, .m4a, .mp3");

        directories0.add(dir);
        directories1.add(new Directory(sampleMixedFile, false));
        directories2.add(new Directory(sampleUrlListFile, false));

        DB.closeDb();
        new TestDb();

        project0 = Project.openOrCreate("Test_Project_1", User.getLocalUser());
        project1 = Project.openOrCreate("Test_Project_2", User.getLocalUser());
        project2 = Project.openOrCreate("Test_Project_3", User.getLocalUser());

        assertTrue(project0.save());
        assertTrue(project1.save());
        assertTrue(project2.save());

        manager0 = project0.resolveDataManager();
        manager1 = project1.resolveDataManager();
        manager2 = project2.resolveDataManager();

        manager0.setSourceDirectories(null);
        manager1.setSourceDirectories(null);
        manager2.setSourceDirectories(null);
    }

    @Test
    void getDirectories() {
        assertEquals(0, manager0.getSourceDirectories().size());
        assertEquals(0, manager1.getSourceDirectories().size());
        assertEquals(0, manager2.getSourceDirectories().size());

        manager0.setSourceDirectories(directories0);
        manager1.setSourceDirectories(directories1);
        manager2.setSourceDirectories(directories2);

        assertEquals(1, manager0.getSourceDirectories().size());
        assertEquals(1, manager1.getSourceDirectories().size());
        assertEquals(1, manager2.getSourceDirectories().size());
    }

    @Test
    void getNextFile() {
        assertTrue(manager0.clear());
        assertTrue(manager1.clear());
        assertTrue(manager2.clear());

        assertNull(manager0.getNextFile());
        assertNull(manager1.getNextFile());
        assertNull(manager2.getNextFile());

        manager0.setSourceDirectories(directories0);
        manager1.setSourceDirectories(directories1);
        manager2.setSourceDirectories(directories2);

        assertTrue(manager0.ingest());
        assertTrue(manager1.ingest());
        assertTrue(manager2.ingest());

        assertNotNull(manager0.getNextFile());
        assertNotNull(manager1.getNextFile());
        assertNotNull(manager2.getNextFile());
        //.jpg, png, .mp4, wmv, .m4a, .mp3
        assertTrue(manager0.getNextFile().getUrlAsString().matches(".*\\.jpg|.*\\.png|.*\\.mp4|.*\\.wmv|.*\\.m4a|.*\\.mp3"));
        assertFalse(manager0.getNextFile().isAnnotated());
        assertTrue(manager1.getNextFile().getUrlAsString().matches(".*\\..*"));
        assertFalse(manager1.getNextFile().isAnnotated());
        assertFalse(manager2.getNextFile().isAnnotated());
    }

    @Test
    void listFiles() {
        assertTrue(manager0.clear());
        assertTrue(manager1.clear());
        assertTrue(manager2.clear());

        assertEquals(0, manager0.getFiles(false, 100, 0).size());
        assertEquals(0, manager1.getFiles(false, 100, 0).size());
        assertEquals(0, manager2.getFiles(false, 100, 0).size());

        manager0.setSourceDirectories(directories0);
        manager1.setSourceDirectories(directories1);
        manager2.setSourceDirectories(directories2);

        assertTrue(manager0.ingest());
        assertTrue(manager1.ingest());
        assertTrue(manager2.ingest());

        assertEquals(8, manager0.getFiles(false, 100, 0).size());
        assertEquals(10, manager1.getFiles(false, 100, 0).size());
        assertEquals(3, manager2.getFiles(false, 100, 0).size());
    }

    @Test
    void saveConfig() {
        assertTrue(manager0.clear());
        assertTrue(manager1.clear());
        assertTrue(manager2.clear());

        assertEquals(0, manager0.getFiles(false, 100, 0).size());
        assertEquals(0, manager1.getFiles(false, 100, 0).size());
        assertEquals(0, manager2.getFiles(false, 100, 0).size());

        manager0.setSourceDirectories(directories0);
        manager1.setSourceDirectories(directories1);
        manager2.setSourceDirectories(directories2);

        assertTrue(manager0.save());
        assertTrue(manager1.save());
        assertTrue(manager2.save());

        DataManager manager3 = project0.resolveDataManager();
        DataManager manager4 = project1.resolveDataManager();
        DataManager manager5 = project2.resolveDataManager();


        assertTrue(manager3.ingest());
        assertTrue(manager4.ingest());
        assertTrue(manager5.ingest());

        assertEquals(8, manager3.getFiles(false, 100, 0).size());
        assertEquals(10, manager4.getFiles(false, 100, 0).size());
        assertEquals(3, manager5.getFiles(false, 100, 0).size());

        assertTrue(manager0.clear());
        assertTrue(manager1.clear());
        assertTrue(manager2.clear());

        assertEquals(0, manager0.getFiles(false, 100, 0).size());
        assertEquals(0, manager1.getFiles(false, 100, 0).size());
        assertEquals(0, manager2.getFiles(false, 100, 0).size());

        assertTrue(manager0.ingest());
        assertTrue(manager1.ingest());
        assertTrue(manager2.ingest());

        assertEquals(8, manager0.getFiles(false, 100, 0).size());
        assertEquals(10, manager1.getFiles(false, 100, 0).size());
        assertEquals(3, manager2.getFiles(false, 100, 0).size());
    }

    @Test
    void checkOutFiles() {
        assertTrue(manager0.clear());
        assertTrue(manager1.clear());
        assertTrue(manager2.clear());

        assertEquals(0, manager0.getFiles(false, 100, 0).size());
        assertEquals(0, manager1.getFiles(false, 100, 0).size());
        assertEquals(0, manager2.getFiles(false, 100, 0).size());

        manager0.setSourceDirectories(directories0);
        manager1.setSourceDirectories(directories1);
        manager2.setSourceDirectories(directories2);

        assertTrue(manager0.save());
        assertTrue(manager1.save());
        assertTrue(manager2.save());

        DataManager manager3 = project0.resolveDataManager();
        DataManager manager4 = project1.resolveDataManager();
        DataManager manager5 = project2.resolveDataManager();


        assertTrue(manager3.ingest());
        assertTrue(manager4.ingest());
        assertTrue(manager5.ingest());

        assertEquals(8, manager3.getFiles(false, 100, 0).size());
        assertEquals(10, manager4.getFiles(false, 100, 0).size());
        assertEquals(3, manager5.getFiles(false, 100, 0).size());

        List<FileInfo> checked0 = manager0.checkOutFiles(true, 100, 60000);
        List<FileInfo> checked1 = manager1.checkOutFiles(false, 100, 60000);
        List<FileInfo> checked2 = manager2.checkOutFiles(false, 100, 60000);

        assertEquals(8, checked0.size());
        assertEquals(10, checked1.size());
        assertEquals(3, checked2.size());

        checked0 = manager0.checkOutFiles(true, 100, 60000);
        checked1 = manager1.checkOutFiles(false, 100, 60000);
        checked2 = manager2.checkOutFiles(false, 100, 60000);

        assertEquals(0, checked0.size());
        assertEquals(0, checked1.size());
        assertEquals(0, checked2.size());
    }
}