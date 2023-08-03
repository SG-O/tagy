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

package de.sg_o.test.tagy.util;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.Directory;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.util.Export;
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

class ExportTest {
    Project p0;

    StructureDefinition def0;

    DataManager manager0;

    final ArrayList<TagDefinition> tags0 = new ArrayList<>();

    @BeforeEach
    void setUp() throws URISyntaxException {
        DB.closeDb();
        new TestDb();

        TagDefinition td0 = new TagDefinition("tag0", Type.LONG);
        TagDefinition td1 = new TagDefinition("tag1", Type.DOUBLE);

        tags0.add(td0);
        tags0.add(td1);

        p0 = Project.openOrCreate("testProject0", User.getLocalUser());
        assertTrue(p0.save());

        def0 = new StructureDefinition(p0);
        def0.setTagDefinitions(tags0);
        def0.save();

        URL sampleMediaFolder = this.getClass().getResource("/sampleFiles/media");
        assertNotNull(sampleMediaFolder);
        File sampleMediaFile = new File(sampleMediaFolder.toURI());

        Directory dir = new Directory(sampleMediaFile, true);
        dir.setFileExtensions(".jpg, png, .mp4, wmv, .m4a, .mp3");
        List<Directory> directories0 = new ArrayList<>();
        directories0.add(dir);

        manager0 = p0.resolveDataManager();
        manager0.setSourceDirectories(directories0);
        manager0.save();
        assertTrue(manager0.clear());

        assertTrue(manager0.ingest());

        FileInfo file0 = manager0.getNextFile();
        MetaData metaData0 = new MetaData(file0, p0);
        metaData0.addTag(new TagLong(td0, 50L));
        metaData0.addTag(new TagDouble(td1, 50.0));
        metaData0.save();

        FileInfo file1 = manager0.getNextFile();
        MetaData metaData1 = new MetaData(file1, p0);
        metaData1.addTag(new TagLong(td0, 199));
        metaData1.addTag(new TagDouble(td1, -0.001));
        metaData1.save();
    }

    @Test
    void asString() {
        String exported = Export.asString(p0, Export.Format.JSON);
        assertEquals(81, exported.split("\r\n|\r|\n").length);
        exported = Export.asString(p0, Export.Format.XML);
        assertEquals(99, exported.split("\r\n|\r|\n").length);
        exported = Export.asString(p0, Export.Format.YAML);
        assertEquals(65, exported.split("\r\n|\r|\n").length);
    }
}