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

package de.sg_o.test.tagy.def;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.Project_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.StructureDefinition_;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.util.Util;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.proto.tagy.TagDefinitionProto;
import de.sg_o.test.tagy.testDb.TestDb;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StructureDefinitionTest {
    Project p0;
    Project p1;

    StructureDefinition def0;
    StructureDefinition def1;
    StructureDefinition def2;
    StructureDefinition def3;

    final ArrayList<TagDefinition> tags0 = new ArrayList<>();
    final ArrayList<TagDefinition> tags1 = new ArrayList<>();

    @BeforeEach
    void setUp() {
        DB.closeDb();
        new TestDb();

        tags0.add(new TagDefinition("tag0", TagDefinitionProto.Type.LONG));
        tags0.add(new TagDefinition("tag1", TagDefinitionProto.Type.DOUBLE));

        tags1.add(new TagDefinition("tag2", TagDefinitionProto.Type.LONG));
        TagDefinition td = new TagDefinition("tag3", TagDefinitionProto.Type.ENUM);
        td.addEnumerator("Option 1");
        td.addEnumerator("Option 2");
        tags1.add(td);

        p0 = Project.openOrCreate("testProject0", User.getLocalUser());
        p1 = Project.openOrCreate("testProject1", User.getLocalUser());

        def0 = new StructureDefinition(p0);
        def0.setTagDefinitions(tags0);

        def1 = new StructureDefinition(p1);
        def1.setTagDefinitions(tags1);

        def2 = new StructureDefinition(p0);
        assertTrue(def2.setTagDefinitions("[{\"key\": \"tag0\", \"type\": \"LONG\", \"required\": false, \"enumerators\": []}, {\"key\": \"tag1\", \"type\": \"DOUBLE\", \"required\": false, \"enumerators\": []}]"));
        def3 = new StructureDefinition(p1);
        assertTrue(def3.setTagDefinitions("[{\"key\": \"tag2\", \"type\": \"LONG\", \"required\": false, \"enumerators\": []}, {\"key\": \"tag3\", \"type\": \"ENUM\", \"required\": false, \"enumerators\": [\"Option 1\", \"Option 2\"]}]"));
    }

    @Test
    void getTags() {
        assertTrue(Util.betterListEquals(tags0, def0.getTagDefinitions()));
        assertTrue(Util.betterListEquals(tags1, def1.getTagDefinitions()));
        assertTrue(Util.betterListEquals(tags0, def2.getTagDefinitions()));
        assertTrue(Util.betterListEquals(tags1, def3.getTagDefinitions()));


        assertEquals(tags0.get(0), def0.getTagDefinitionForKey("tag0"));
        assertEquals(tags0.get(1), def0.getTagDefinitionForKey("tag1"));

        assertEquals(tags1.get(0), def1.getTagDefinitionForKey("tag2"));
        assertEquals(tags1.get(1), def1.getTagDefinitionForKey("tag3"));

        assertEquals(tags0.get(0), def2.getTagDefinitionForKey("tag0"));
        assertEquals(tags0.get(1), def2.getTagDefinitionForKey("tag1"));

        assertEquals(tags1.get(0), def3.getTagDefinitionForKey("tag2"));
        assertEquals(tags1.get(1), def3.getTagDefinitionForKey("tag3"));
    }

    @Test
    void getEncoded() {
        BoxStore db = DB.getDb();
        assertNotNull(db);
        Box<StructureDefinition> box = db.boxFor(StructureDefinition.class);
        assertNotNull(box);
        box.removeAll();
        assertEquals(0L, box.count());

        assertTrue(def0.save());
        assertTrue(def1.save());
        assertEquals(2L, box.count());

        List<StructureDefinition> q = StructureDefinition.query(qb -> {
            qb.link(StructureDefinition_.project).apply(Project_.projectName.equal("testProject0"));
            return qb;
        }, 0,0);
        assertEquals(1, q.size());

        StructureDefinition qr0 = StructureDefinition.queryFirst(qb -> {
            qb.link(StructureDefinition_.project).apply(Project_.projectName.equal("testProject0"));
            return qb;
        });
        StructureDefinition qr1 = StructureDefinition.queryFirst(qb -> {
            qb.link(StructureDefinition_.project).apply(Project_.projectName.equal("testProject1"));
            return qb;
        });

        assertEquals(def0, qr0);
        assertEquals(def1, qr1);
    }

    @Test
    void repeatedInsert() {
        Project p2 = Project.openOrCreate("testProject2", User.getLocalUser());
        assertTrue(p2.save());

        StructureDefinition sd = p2.resolveStructureDefinition();
        sd.clearTagDefinitions();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.save();

        sd = p2.resolveStructureDefinition();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.setTagDefinitions(tags0);
        assertEquals(2, sd.getTagDefinitions().size());
        sd.save();
        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags0, sd.getTagDefinitions()));
            sd.setTagDefinitions(tags0);
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags0, sd.getTagDefinitions()));
            sd.save();
        }
        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags0, sd.getTagDefinitions()));
            sd.setTagDefinitions(sd.toString());
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags0, sd.getTagDefinitions()));
            sd.save();
        }

        sd.clearTagDefinitions();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.save();

        sd = p2.resolveStructureDefinition();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.setTagDefinitions(tags1);
        assertEquals(2, sd.getTagDefinitions().size());
        sd.save();

        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags1, sd.getTagDefinitions()));
            sd.setTagDefinitions(tags1);
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags1, sd.getTagDefinitions()));
            sd.save();
        }
        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags1, sd.getTagDefinitions()));
            sd.setTagDefinitions(sd.toString());
            assertEquals(2, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags1, sd.getTagDefinitions()));
            sd.save();
        }

        sd.clearTagDefinitions();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.save();

        TagDefinition tag4 = new TagDefinition("tag4", TagDefinitionProto.Type.LIST);
        tag4.setInternal(new TagDefinition("", TagDefinitionProto.Type.STRING));

        ArrayList<TagDefinition> tags2 = new ArrayList<>(tags1);
        tags2.add(tag4);
        tags2.add(new TagDefinition("tag5", TagDefinitionProto.Type.DATE));
        tags2.addAll(tags0);

        sd = p2.resolveStructureDefinition();
        assertEquals(0, sd.getTagDefinitions().size());
        sd.setTagDefinitions(tags2);
        assertEquals(6, sd.getTagDefinitions().size());
        sd.save();

        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(6, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags2, sd.getTagDefinitions()));
            sd.setTagDefinitions(tags2);
            assertEquals(6, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags2, sd.getTagDefinitions()));
            sd.save();
        }
        for (int i = 0; i < 20; i++) {
            sd = p2.resolveStructureDefinition();
            assertEquals(6, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags2, sd.getTagDefinitions()));
            sd.setTagDefinitions(sd.toString());
            assertEquals(6, sd.getTagDefinitions().size());
            assertTrue(Util.betterListEquals(tags2, sd.getTagDefinitions()));
            sd.save();
        }

        sd = p2.resolveStructureDefinition();
        assertEquals(6, sd.getTagDefinitions().size());

        assertTrue(Util.betterListEquals(tags2, sd.getTagDefinitions()));

        assertEquals(tags1.get(0), sd.getTagDefinitions().get(0));
        assertEquals(tags1.get(1), sd.getTagDefinitions().get(1));
        assertEquals(tag4, sd.getTagDefinitions().get(2));
        assertEquals(tags0.get(0), sd.getTagDefinitions().get(4));
        assertEquals(tags0.get(1), sd.getTagDefinitions().get(5));

        assertEquals("[\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag2\",\n" +
                "\t\t\"type\": \"LONG\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag3\",\n" +
                "\t\t\"type\": \"ENUM\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": \n" +
                "\t\t\t[\n" +
                "\t\t\t\t\"Option 1\",\n" +
                "\t\t\t\t\"Option 2\"\n" +
                "\t\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag4\",\n" +
                "\t\t\"type\": \"LIST\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": [],\n" +
                "\t\t\"internal\": \n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"key\": \"\",\n" +
                "\t\t\t\t\"type\": \"STRING\",\n" +
                "\t\t\t\t\"required\": false,\n" +
                "\t\t\t\t\"enumerators\": []\n" +
                "\t\t\t}\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag5\",\n" +
                "\t\t\"type\": \"DATE\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag0\",\n" +
                "\t\t\"type\": \"LONG\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag1\",\n" +
                "\t\t\"type\": \"DOUBLE\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t}\n" +
                "]", sd.toString());
    }

    @Test
    void testToString() {
        assertEquals("[\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag0\",\n" +
                "\t\t\"type\": \"LONG\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag1\",\n" +
                "\t\t\"type\": \"DOUBLE\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t}\n" +
                "]", def0.toString());
        assertEquals("[\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag2\",\n" +
                "\t\t\"type\": \"LONG\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": []\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"key\": \"tag3\",\n" +
                "\t\t\"type\": \"ENUM\",\n" +
                "\t\t\"required\": false,\n" +
                "\t\t\"enumerators\": \n" +
                "\t\t\t[\n" +
                "\t\t\t\t\"Option 1\",\n" +
                "\t\t\t\t\"Option 2\"\n" +
                "\t\t\t]\n" +
                "\t}\n" +
                "]", def1.toString());
    }
}