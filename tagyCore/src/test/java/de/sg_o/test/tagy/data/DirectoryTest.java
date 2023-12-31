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

import de.sg_o.lib.tagy.data.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryTest {
    DataSource dir0;
    DataSource dir1;
    DataSource dir2;
    DataSource dir3;
    DataSource dir4;

    @BeforeEach
    void setUp() throws URISyntaxException {
        URL sampleMediaFolder = this.getClass().getResource("/sampleFiles/media");
        assertNotNull(sampleMediaFolder);
        File sampleMediaFile = new File(sampleMediaFolder.toURI());
        URL sampleMixedFolder = this.getClass().getResource("/sampleFiles/mixed");
        assertNotNull(sampleMixedFolder);
        File sampleMixedFile = new File(sampleMixedFolder.toURI());
        URL sampleTextFolder = this.getClass().getResource("/sampleFiles/text");
        assertNotNull(sampleTextFolder);
        File sampleTextFile = new File(sampleTextFolder.toURI());
        assertTrue(sampleMediaFile.exists());
        assertTrue(sampleMixedFile.exists());
        assertTrue(sampleTextFile.exists());
        assertTrue(sampleMediaFile.isDirectory());
        assertTrue(sampleMixedFile.isDirectory());
        assertTrue(sampleTextFile.isDirectory());

        LinkedList<String> extensions = new LinkedList<>();
        extensions.add(".jpg");
        extensions.add("txt");
        extensions.add(".m4a");

        dir0 = new DataSource(sampleMediaFile, true);
        dir0.setFileExtensions(".jpg, png, .mp4, wmv, .m4a, .mp3");
        dir1 = new DataSource(sampleMixedFile, false);
        dir1.setFileExtensions(extensions);
        dir2 = new DataSource(sampleTextFile, true);
        dir3 = new DataSource(sampleTextFile, false);
        dir4 = new DataSource(sampleMediaFile, true);
        dir4.setFileExtensions(".jpg, png, .mp4, wmv, .m4a, .mp3");
    }

    @Test
    void getRootDirectory() {
        assertTrue(dir0.resolveSource().getPath().endsWith("sampleFiles/media"));
        assertTrue(dir1.resolveSource().getPath().endsWith("sampleFiles/mixed"));
        assertTrue(dir2.resolveSource().getPath().endsWith("sampleFiles/text"));
        assertTrue(dir3.resolveSource().getPath().endsWith("sampleFiles/text"));
        assertTrue(dir4.resolveSource().getPath().endsWith("sampleFiles/media"));
    }

    @Test
    void isRecursive() {
        assertTrue(dir0.isRecursive());
        assertFalse(dir1.isRecursive());
        assertTrue(dir2.isRecursive());
        assertFalse(dir3.isRecursive());
        assertTrue(dir4.isRecursive());
    }

    @Test
    void getFileExtensions() {
        assertEquals(dir0.getFileExtensions().size(), 6);
        assertEquals(dir1.getFileExtensions().size(), 3);
        assertEquals(dir2.getFileExtensions().size(), 0);
        assertEquals(dir3.getFileExtensions().size(), 0);
        assertEquals(dir0.getFileExtensions(), dir4.getFileExtensions());

        assertTrue(dir0.getFileExtensions().contains("jpg"));
        assertTrue(dir0.getFileExtensions().contains("png"));
        assertTrue(dir0.getFileExtensions().contains("mp4"));
        assertTrue(dir0.getFileExtensions().contains("wmv"));
        assertTrue(dir0.getFileExtensions().contains("m4a"));
        assertTrue(dir0.getFileExtensions().contains("mp3"));

        assertTrue(dir1.getFileExtensions().contains("jpg"));
        assertTrue(dir1.getFileExtensions().contains("txt"));
        assertTrue(dir1.getFileExtensions().contains("m4a"));
    }

    @Test
    void getFiles() {
        assertEquals(8, dir0.getFiles().size());
        assertEquals(4, dir1.getFiles().size());
        assertEquals(5, dir2.getFiles().size());
        assertEquals(2, dir3.getFiles().size());
        assertEquals(8, dir4.getFiles().size());
    }

    @Test
    void testEquals() {
        assertEquals(dir0, dir4);
        assertNotEquals(dir0, dir1);
        assertNotEquals(dir0, dir2);
        assertNotEquals(dir0, dir3);
        assertEquals(dir0.hashCode(), dir4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("{\"source\": \"media\", \"recursive\": true, \"fileExtensions\":[jpg, png, mp4, wmv, m4a, mp3]}",
                dir0.toString());
        assertEquals("{\"source\": \"mixed\", \"recursive\": false, \"fileExtensions\":[jpg, txt, m4a]}",
                dir1.toString());
        assertEquals("{\"source\": \"text\", \"recursive\": true, \"fileExtensions\":[]}",
                dir2.toString());
        assertEquals("{\"source\": \"text\", \"recursive\": false, \"fileExtensions\":[]}",
                dir3.toString());
    }
}