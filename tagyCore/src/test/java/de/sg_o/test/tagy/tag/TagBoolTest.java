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
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.TagHolder;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagBoolTest {
    TagBool tag0;
    TagBool tag1;
    TagBool tag2;
    TagBool tag3;
    TagBool tag4;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", Type.BOOLEAN);
        TagDefinition td1 = new TagDefinition("key1", Type.BOOLEAN);

        tag0 = new TagBool(td0, true);
        tag1 = new TagBool(td1, true);
        tag2 = new TagBool(td0, false);
        tag3 = new TagBool(td1, false);
        tag4 = new TagBool(td0, true);
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
        assertTrue(tag0.getValue());
        assertTrue(tag1.getValue());
        assertFalse(tag2.getValue());
        assertFalse(tag3.getValue());
        assertTrue(tag4.getValue());
    }

    @Test
    void addToDocument() {
        TagHolder holder0 = new TagHolder(tag0);
        TagHolder holder1 = new TagHolder(tag1);
        TagHolder holder2 = new TagHolder(tag2);
        TagHolder holder3 = new TagHolder(tag3);
        TagHolder holder4 = new TagHolder(tag4);

        String json0 = holder0.getEncoded();
        String json1 = holder1.getEncoded();
        String json2 = holder2.getEncoded();
        String json3 = holder3.getEncoded();
        String json4 = holder4.getEncoded();

        assertEquals("{\"value\":true}", json0);
        assertEquals("{\"value\":true}", json1);
        assertEquals("{\"value\":false}", json2);
        assertEquals("{\"value\":false}", json3);
        assertEquals("{\"value\":true}", json4);

        TagHolder holder5 = new TagHolder(tag0.getDefinition(), json0);
        TagHolder holder6 = new TagHolder(tag1.getDefinition(), json1);
        TagHolder holder7 = new TagHolder(tag2.getDefinition(), json2);
        TagHolder holder8 = new TagHolder(tag3.getDefinition(), json3);
        TagHolder holder9 = new TagHolder(tag4.getDefinition(), json4);

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

        assertEquals(-200967330, tag0.hashCode());
        assertEquals(2100535102, tag1.hashCode());
        assertEquals(-200967324, tag2.hashCode());
        assertEquals(2100535108, tag3.hashCode());
        assertEquals(tag0.hashCode(), tag4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": true", tag0.toString());
        assertEquals("\"key1\": true", tag1.toString());
        assertEquals("\"key0\": false", tag2.toString());
        assertEquals("\"key1\": false", tag3.toString());
        assertEquals(tag0.toString(), tag4.toString());
    }
}