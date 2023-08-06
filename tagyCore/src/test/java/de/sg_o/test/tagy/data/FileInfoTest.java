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
import de.sg_o.lib.tagy.Project_;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.FileInfo_;
import de.sg_o.lib.tagy.data.FileType;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.util.UrlConverter;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.test.tagy.testDb.TestDb;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {
    Project p0;

    FileInfo fi0;
    FileInfo fi1;
    FileInfo fi2;
    FileInfo fi3;

    URL sampleMediaFile;
    URL sampleMixedFile;
    URL sampleTextFile;

    @BeforeEach
    void setUp() throws URISyntaxException {
        sampleMediaFile = this.getClass().getResource("/sampleFiles/media/video/sample04.mkv");
        assertNotNull(sampleMediaFile);
        sampleMixedFile = this.getClass().getResource("/sampleFiles/mixed/sample07.webp");
        assertNotNull(sampleMixedFile);
        sampleTextFile = this.getClass().getResource("/sampleFiles/text/sample0.txt");
        assertNotNull(sampleTextFile);

        p0 = Project.openOrCreate("testProject", User.getLocalUser());
        p0.save();

        fi0 = new FileInfo(sampleMediaFile, p0);
        fi1 = new FileInfo(sampleMixedFile, p0);
        fi2 = new FileInfo(sampleTextFile, p0);

        File file = new File(sampleMediaFile.toURI());
        UrlConverter  uc = new UrlConverter();
        URL converted = uc.convertToEntityProperty(file.getAbsolutePath());

        fi3 = new FileInfo(converted, p0);

        DB.closeDb();
        new TestDb();
    }

    @Test
    void getFile() {
        assertNotNull(fi0.getUrlAsString());
        assertNotNull(fi1.getUrlAsString());
        assertNotNull(fi2.getUrlAsString());
        assertNotNull(fi3.getUrlAsString());
    }

    @Test
    void getAbsolutePath() {
        assertNotNull(fi0.getAbsolutePath());
        assertNotNull(fi1.getAbsolutePath());
        assertNotNull(fi2.getAbsolutePath());
        assertNotNull(fi3.getAbsolutePath());
    }

    @Test
    void isAnnotated() {
        assertFalse(fi0.isAnnotated());
        assertFalse(fi1.isAnnotated());
        assertFalse(fi2.isAnnotated());
        assertFalse(fi3.isAnnotated());

        fi0.setAnnotated(true);
        fi1.setAnnotated(true);
        fi2.setAnnotated(true);
        fi3.setAnnotated(true);

        assertTrue(fi0.isAnnotated());
        assertTrue(fi1.isAnnotated());
        assertTrue(fi2.isAnnotated());
        assertTrue(fi3.isAnnotated());
    }

    @Test
    void getFileType() {
        assertEquals(FileType.MEDIA, fi0.getFileType());
        assertEquals(FileType.IMAGE, fi1.getFileType());
        assertEquals(FileType.TEXT, fi2.getFileType());
        assertEquals(FileType.MEDIA, fi3.getFileType());
    }

    @Test
    void testEquals() {
        assertEquals(fi0, fi3);
        assertNotEquals(fi0, fi1);
        assertNotEquals(fi0, fi2);

        BoxStore db = DB.getDb();
        assertNotNull(db);
        Box<FileInfo> box = db.boxFor(FileInfo.class);
        assertNotNull(box);
        box.removeAll();
        assertEquals(0L, box.count());

        assertTrue(fi0.save());
        assertTrue(fi1.save());
        assertTrue(fi2.save());
        assertTrue(fi3.save());

        assertEquals(4L, box.count());

        List<FileInfo> fileInfos = FileInfo.query(p0, false, 0,0);
        assertEquals(4, fileInfos.size());

        FileInfo qr0 = FileInfo.queryFirst(qb -> {
            qb = qb.endsWith(FileInfo_.absolutePath, "sample04.mkv", QueryBuilder.StringOrder.CASE_SENSITIVE);
            qb.link(FileInfo_.project).apply(Project_.projectName.equal("testProject"));
            return qb;
        });
        FileInfo qr1 = FileInfo.queryFirst(qb -> {
            qb = qb.endsWith(FileInfo_.absolutePath, "sample07.webp", QueryBuilder.StringOrder.CASE_SENSITIVE);
            qb.link(FileInfo_.project).apply(Project_.projectName.equal("testProject"));
            return qb;
        });
        FileInfo qr2 = FileInfo.queryFirst(qb -> {
            qb = qb.endsWith(FileInfo_.absolutePath, "sample0.txt", QueryBuilder.StringOrder.CASE_SENSITIVE);
            qb.link(FileInfo_.project).apply(Project_.projectName.equal("testProject"));
            return qb;
        });
        FileInfo qr3 = FileInfo.queryFirst(qb -> {
            qb = qb.endsWith(FileInfo_.absolutePath, "sample04.mkv", QueryBuilder.StringOrder.CASE_SENSITIVE);
            qb.link(FileInfo_.project).apply(Project_.projectName.equal("testProject"));
            return qb;
        });

        assertEquals(qr0, qr3);

        assertEquals(fi0, qr0);
        assertEquals(fi1, qr1);
        assertEquals(fi2, qr2);
        assertEquals(fi3, qr3);
    }

    @Test
    void testToString() {
        assertEquals("{\n" +
                "\t\"file\": \"" + sampleMediaFile.toString() + "\",\n" +
                "\t\"annotated\": false\n" +
                "}", fi0.toString());
        assertEquals("{\n" +
                "\t\"file\": \"" + sampleMixedFile.toString() + "\",\n" +
                "\t\"annotated\": false\n" +
                "}", fi1.toString());
        assertEquals("{\n" +
                "\t\"file\": \"" + sampleTextFile.toString() + "\",\n" +
                "\t\"annotated\": false\n" +
                "}", fi2.toString());
        assertEquals(fi0.toString(), fi3.toString());
    }
}