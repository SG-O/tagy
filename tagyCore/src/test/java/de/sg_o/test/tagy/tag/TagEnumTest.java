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

package de.sg_o.test.tagy.tag;

import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.TagMigration;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TagEnumTest {
    final ArrayList<String> enumerators0 = new ArrayList<>();
    final ArrayList<String> enumerators1 = new ArrayList<>();

    TagEnum tag0;
    TagEnum tag1;
    TagEnum tag2;
    TagEnum tag3;
    TagEnum tag4;
    TagEnum tag5;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", TagDefinitionProto.Type.ENUM);
        TagDefinition td1 = new TagDefinition("key1", TagDefinitionProto.Type.ENUM);
        TagDefinition td2 = new TagDefinition("key0", TagDefinitionProto.Type.ENUM);
        TagDefinition td3 = new TagDefinition("key1", TagDefinitionProto.Type.ENUM);

        enumerators0.add("Option 1");
        enumerators0.add("Option 2");

        enumerators1.add("Option 3");
        enumerators1.add("Option 4");

        td0.addAllEnumerators(enumerators0);
        td1.addAllEnumerators(enumerators0);
        td2.addAllEnumerators(enumerators1);
        td3.addAllEnumerators(enumerators1);

        tag0 = new TagEnum(td0, 0);
        tag1 = new TagEnum(td1, 0);
        tag2 = new TagEnum(td2, 0);
        tag3 = new TagEnum(td3, 1);
        tag4 = new TagEnum(td0, 4);
        tag5 = new TagEnum(td0, 0);
    }

    @Test
    void getKey() {
        assertEquals("key0", tag0.getKey());
        assertEquals("key1", tag1.getKey());
        assertEquals("key0", tag2.getKey());
        assertEquals("key1", tag3.getKey());
        assertEquals("key0", tag4.getKey());
        assertEquals("key0", tag5.getKey());
    }

    @Test
    void getValue() {
        assertEquals(0, tag0.getValue());
        assertEquals(0, tag1.getValue());
        assertEquals(0, tag2.getValue());
        assertEquals(1, tag3.getValue());
        assertEquals(4, tag4.getValue());
        assertEquals(0, tag5.getValue());
    }

    @Test
    void getEnumerator() {
        assertEquals("Option 1", tag0.getValueAsString());
        assertEquals("Option 1", tag1.getValueAsString());
        assertEquals("Option 3", tag2.getValueAsString());
        assertEquals("Option 4", tag3.getValueAsString());
        assertEquals("UNRECOGNIZED", tag4.getValueAsString());
        assertEquals("Option 1", tag5.getValueAsString());
    }

    @Test
    void getEnumerators() {
        assertEquals(enumerators0, tag0.getEnumerators());
        assertEquals(enumerators0, tag1.getEnumerators());
        assertEquals(enumerators1, tag2.getEnumerators());
        assertEquals(enumerators1, tag3.getEnumerators());
        assertEquals(enumerators0, tag4.getEnumerators());
        assertEquals(enumerators0, tag5.getEnumerators());
    }

    @Test
    void addToDocument() {
        TagMigration holder0 = new TagMigration(tag0);
        TagMigration holder1 = new TagMigration(tag1);
        TagMigration holder2 = new TagMigration(tag2);
        TagMigration holder3 = new TagMigration(tag3);
        TagMigration holder4 = new TagMigration(tag4);

        String json0 = holder0.getEncoded();
        String json1 = holder1.getEncoded();
        String json2 = holder2.getEncoded();
        String json3 = holder3.getEncoded();
        String json4 = holder4.getEncoded();

        assertEquals("{\"value\":0}", json0);
        assertEquals("{\"value\":0}", json1);
        assertEquals("{\"value\":0}", json2);
        assertEquals("{\"value\":1}", json3);
        assertEquals("{\"value\":4}", json4);

        TagMigration holder5 = new TagMigration(tag0.getDefinition(), json0);
        TagMigration holder6 = new TagMigration(tag1.getDefinition(), json1);
        TagMigration holder7 = new TagMigration(tag2.getDefinition(), json2);
        TagMigration holder8 = new TagMigration(tag3.getDefinition(), json3);
        TagMigration holder9 = new TagMigration(tag4.getDefinition(), json4);

        Tag decoded0 = holder5.getTag();
        Tag decoded1 = holder6.getTag();
        Tag decoded2 = holder7.getTag();
        Tag decoded3 = holder8.getTag();
        Tag decoded4 = holder9.getTag();

        assertEquals(tag0, decoded0);
        assertEquals(tag1, decoded1);
        assertEquals(tag2, decoded2);
        assertEquals(tag3, decoded3);
        assertEquals(tag4, decoded4);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEquals() {
        assertEquals(tag0, tag0);
        assertEquals(tag1, tag1);
        assertEquals(tag0, tag5);
        assertNotEquals(tag0, tag1);
        assertNotEquals(tag0, tag2);
        assertNotEquals(tag0, tag3);
        assertNotEquals(tag0, tag4);

        assertEquals(1819165713, tag0.hashCode());
        assertEquals(1654845425, tag1.hashCode());
        assertEquals(-643535919, tag2.hashCode());
        assertEquals(-807856206, tag3.hashCode());
        assertEquals(1819165717, tag4.hashCode());
        assertEquals(tag0.hashCode(), tag5.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": \"Option 1\"", tag0.toString());
        assertEquals("\"key1\": \"Option 1\"", tag1.toString());
        assertEquals("\"key0\": \"Option 3\"", tag2.toString());
        assertEquals("\"key1\": \"Option 4\"", tag3.toString());
        assertEquals("\"key0\": \"UNRECOGNIZED\"", tag4.toString());
        assertEquals("\"key0\": \"Option 1\"", tag5.toString());
    }
}