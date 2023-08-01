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
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.db.NewDB;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetaDataTest {
    Project project0;

    MetaData md0;
    MetaData md1;

    FileInfo fi0;
    FileInfo fi1;

    @BeforeEach
    void setUp() throws URISyntaxException {
        NewDB.closeDb();
        new TestDb();

        project0 = Project.openOrCreate("Test_Project_Meta_1", User.getLocalUser());

        URL sampleMediaFolder = this.getClass().getResource("/sampleFiles/media/video/sample03.wmv");
        assertNotNull(sampleMediaFolder);
        File sampleMediaFile = new File(sampleMediaFolder.toURI());
        URL sampleMixedFolder = this.getClass().getResource("/sampleFiles/mixed/sample07.webp");
        assertNotNull(sampleMixedFolder);
        File sampleMixedFile = new File(sampleMixedFolder.toURI());

        fi0 = new FileInfo(sampleMediaFile, project0);
        fi1 = new FileInfo(sampleMixedFile, project0);

        fi0.save();
        fi1.save();

        final ArrayList<String> enumerators0 = new ArrayList<>();
        enumerators0.add("Option 1");
        enumerators0.add("Option 2");

        TagDefinition td0 = new TagDefinition("key0", Type.STRING);
        TagDefinition td1 = new TagDefinition("key1", Type.STRING);
        TagDefinition td2 = new TagDefinition("key2", Type.LIST);
        TagDefinition tdl0 = new TagDefinition("value0", Type.LONG);
        td2.setInternal(tdl0);
        TagDefinition td3 = new TagDefinition("key3", Type.ENUM);
        td3.addAllEnumerators(enumerators0);

        StructureDefinition structureDefinition = project0.resolveStructureDefinition();
        ArrayList<TagDefinition> tagDefinitions = new ArrayList<>();
        tagDefinitions.add(td0);
        tagDefinitions.add(td1);
        tagDefinitions.add(td2);
        tagDefinitions.add(td3);
        structureDefinition.setTagDefinitions(tagDefinitions);
        structureDefinition.save();

        Tag tag0 = new TagString(td0, "Test String 0");
        Tag tag1 = new TagString(td1, "Test String 1");
        TagList tag2 = new TagList(td2);
        Tag internal0 = new TagLong(tdl0, 5);
        Tag internal1 = new TagLong(tdl0, 500);
        tag2.addValue(internal0);
        tag2.addValue(internal1);
        Tag tag3 = new TagEnum(td3, 0);

        md0 = new MetaData(fi0, project0);
        md0.addTag(tag0);
        md0.addTag(tag1);

        md1 = new MetaData(fi1, project0);
        md1.addTag(tag2);
        md1.addTag(tag3);
    }

    @Test
    void save() {
        List<MetaData> metaDataList = MetaData.queryAll(project0, 0, 0);
        assertEquals(0, metaDataList.size());

        assertTrue(md0.save());
        assertTrue(md1.save());

        metaDataList = MetaData.queryAll(project0, 0, 0);
        assertEquals(2, metaDataList.size());

        MetaData md2 = MetaData.queryFirst(fi0);
        assertEquals(md0, md2);

        MetaData md3 = MetaData.queryFirst(fi1);
        assertEquals(md1, md3);
    }

    @Test
    void testToString() {
        assertEquals("{\n" +
                "\t\"_id\": \n" +
                "\t\t{\n" +
                "\t\t\t\"file\": \"sample03.wmv\",\n" +
                "\t\t\t\"annotated\": false\n" +
                "\t\t},\n" +
                "\t\"key1\": \"Test String 1\",\n" +
                "\t\"key0\": \"Test String 0\",\n" +
                "\t\"_editHistory\": \n" +
                "\t\t[\n" +
                "\t\t\t{\"id\": 1, \"name\": \"Local\"}\n" +
                "\t\t]\n" +
                "}", md0.toString());
        assertEquals("{\n" +
                "\t\"_id\": \n" +
                "\t\t{\n" +
                "\t\t\t\"file\": \"sample07.webp\",\n" +
                "\t\t\t\"annotated\": false\n" +
                "\t\t},\n" +
                "\t\"key2\": [5, 500],\n" +
                "\t\"key3\": \"Option 1\",\n" +
                "\t\"_editHistory\": \n" +
                "\t\t[\n" +
                "\t\t\t{\"id\": 1, \"name\": \"Local\"}\n" +
                "\t\t]\n" +
                "}", md1.toString());
    }

    @AfterEach
    void tearDown() {
        project0.delete();
    }
}