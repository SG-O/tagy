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

import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StructureDefinitionTest {
    StructureDefinition def0;
    StructureDefinition def1;
    StructureDefinition def2;
    StructureDefinition def3;

    final ArrayList<TagDefinition> tags0 = new ArrayList<>();
    final ArrayList<TagDefinition> tags1 = new ArrayList<>();

    @BeforeEach
    void setUp() {
        tags0.add(new TagDefinition("tag0", Type.LONG));
        tags0.add(new TagDefinition("tag1", Type.DOUBLE));

        tags1.add(new TagDefinition("tag2", Type.LONG));
        TagDefinition td = new TagDefinition("tag3", Type.ENUM);
        td.addEnumerator("Option 1");
        td.addEnumerator("Option 2");
        tags1.add(td);

        def0 = new StructureDefinition();
        def0.setTags(tags0);

        def1 = new StructureDefinition();
        def1.setTags(tags1);

        def2 = new StructureDefinition();
        assertTrue(def2.setTags("[{\"key\": \"tag0\", \"type\": \"LONG\", \"required\": false, \"enumerators\": []}, {\"key\": \"tag1\", \"type\": \"DOUBLE\", \"required\": false, \"enumerators\": []}]"));
        def3 = new StructureDefinition();
        assertTrue(def3.setTags("[{\"key\": \"tag2\", \"type\": \"LONG\", \"required\": false, \"enumerators\": []}, {\"key\": \"tag3\", \"type\": \"ENUM\", \"required\": false, \"enumerators\": [\"Option 1\", \"Option 2\"]}]"));
    }

    @Test
    void getTags() {
        assertEquals(tags0, def0.getTags());
        assertEquals(tags1, def1.getTags());
        assertEquals(tags0, def2.getTags());
        assertEquals(tags1, def3.getTags());
    }

    @Test
    void getEncoded() {
        StructureDefinition def4 = new StructureDefinition();
        def4.setTags(def0.getEncoded());
        StructureDefinition def5 = new StructureDefinition();
        def5.setTags(def1.getEncoded());

        assertEquals(tags0, def4.getTags());
        assertEquals(tags1, def5.getTags());
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