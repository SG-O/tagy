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
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TagDateTest {
    TagDate tag0;
    TagDate tag1;
    TagDate tag2;
    TagDate tag3;
    TagDate tag4;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", TagDefinitionProto.Type.DATE);
        TagDefinition td1 = new TagDefinition("key1", TagDefinitionProto.Type.DATE);

        tag0 = new TagDate(td0, new Date(1672531200000L));
        tag1 = new TagDate(td1, new Date(1672531200000L));
        tag2 = new TagDate(td0, new Date(1672617600000L));
        tag3 = new TagDate(td1, new Date(1672617600000L));
        tag4 = new TagDate(td0, new Date(1672531200000L));
    }

    @Test
    void getKey() {
        assertEquals("key0", tag0.getKey());
        assertEquals("key1", tag1.getKey());
        assertEquals("key0", tag2.getKey());
        assertEquals("key1", tag3.getKey());
        assertEquals("key0", tag4.getKey());
    }

    @Test
    void getValue() {
        assertEquals(new Date(1672531200000L), tag0.getValue());
        assertEquals(new Date(1672531200000L), tag1.getValue());
        assertEquals(new Date(1672617600000L), tag2.getValue());
        assertEquals(new Date(1672617600000L), tag3.getValue());
        assertEquals(new Date(1672531200000L), tag4.getValue());
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

        assertEquals("{\"value\":1672531200000}", json0);
        assertEquals("{\"value\":1672531200000}", json1);
        assertEquals("{\"value\":1672617600000}", json2);
        assertEquals("{\"value\":1672617600000}", json3);
        assertEquals("{\"value\":1672531200000}", json4);

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
        assertEquals(tag0, tag4);
        assertNotEquals(tag0, tag1);
        assertNotEquals(tag0, tag2);
        assertNotEquals(tag0, tag3);

        assertEquals(2127224598, tag0.hashCode());
        assertEquals(1962904310, tag1.hashCode());
        assertEquals(-2081342698, tag2.hashCode());
        assertEquals(2049304310, tag3.hashCode());
        assertEquals(tag0.hashCode(), tag4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": \"01 Jan 2023 00:00:00 UTC\"", tag0.toString());
        assertEquals("\"key1\": \"01 Jan 2023 00:00:00 UTC\"", tag1.toString());
        assertEquals("\"key0\": \"02 Jan 2023 00:00:00 UTC\"", tag2.toString());
        assertEquals("\"key1\": \"02 Jan 2023 00:00:00 UTC\"", tag3.toString());
        assertEquals(tag0.toString(), tag4.toString());
    }
}