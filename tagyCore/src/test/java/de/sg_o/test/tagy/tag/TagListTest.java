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
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagListTest {
    TagList tag0;
    TagList tag1;
    TagList tag2;

    TagDefinition tdl0;
    TagDefinition tdl1;
    TagDefinition tdl2;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", Type.LIST);
        TagDefinition td1 = new TagDefinition("key1", Type.LIST);

        tdl0 = new TagDefinition("value0", Type.LONG);
        tdl1 = new TagDefinition("value1", Type.LONG);
        tdl2 = new TagDefinition("value2", Type.LONG);

        td0.setInternal(tdl0);
        td1.setInternal(tdl0);

        tag0 = new TagList(td0);
        tag1 = new TagList(td1);
        tag2 = new TagList(td0);
    }

    @Test
    void getKey() {
        assertEquals("key0", tag0.getKey());
        assertEquals("key1", tag1.getKey());
        assertEquals("key0", tag2.getKey());
    }

    @Test
    void getValues() {
        assertEquals(0, tag0.getValue().size());
        assertEquals(0, tag1.getValue().size());
        assertEquals(0, tag2.getValue().size());

        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertEquals(1, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        assertEquals(2, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(2, tag2.getValue().size());

        assertEquals("\"key0\": [0, 2]", tag0.toString());
        assertEquals("\"key1\": [1]", tag1.toString());
        assertEquals("\"key0\": [0, 2]", tag2.toString());

        assertNotNull(tag0.removeValue(1));
        assertNotNull(tag1.removeValue(0));
        assertNotNull(tag2.removeValue(0));
        assertNull(tag2.removeValue(2));
        assertNull(tag2.removeValue(-1));

        assertEquals(1, tag0.getValue().size());
        assertEquals(0, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());

        assertEquals("\"key0\": [0]", tag0.toString());
        assertEquals("\"key1\": []", tag1.toString());
        assertEquals("\"key0\": [2]", tag2.toString());

        assertTrue(tag0.removeValue(new TagLong(tdl0, 0)));
        assertTrue(tag2.removeValue(new TagLong(tdl2, 2)));
        assertFalse(tag2.removeValue(new TagLong(tdl2, 2)));

        assertEquals("\"key0\": []", tag0.toString());
        assertEquals("\"key1\": []", tag1.toString());
        assertEquals("\"key0\": []", tag2.toString());
    }

    @Test
    void addToDocument() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        TagHolder holder0 = new TagHolder(tag0);
        TagHolder holder1 = new TagHolder(tag1);
        TagHolder holder2 = new TagHolder(tag2);

        String json0 = holder0.getEncoded();
        String json1 = holder1.getEncoded();
        String json2 = holder2.getEncoded();

        assertEquals("{\"values\":[{\"value\":0},{\"value\":2}]}", json0);
        assertEquals("{\"values\":[{\"value\":1}]}", json1);
        assertEquals("{\"values\":[{\"value\":0},{\"value\":2}]}", json2);

        TagHolder holder5 = new TagHolder(tag0.getDefinition(), json0);
        TagHolder holder6 = new TagHolder(tag1.getDefinition(), json1);
        TagHolder holder7 = new TagHolder(tag2.getDefinition(), json2);

        Tag decoded0 = holder5.getTag();
        Tag decoded1 = holder6.getTag();
        Tag decoded2 = holder7.getTag();

        assertEquals(tag0, decoded0);
        assertEquals(tag1, decoded1);
        assertEquals(tag2, decoded2);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEquals() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        assertEquals(tag0, tag0);
        assertEquals(tag1, tag1);
        assertNotEquals(tag0, tag1);
        assertEquals(tag0, tag2);

        assertEquals(962620637, tag0.hashCode());
        assertEquals(1958621416, tag1.hashCode());
        assertEquals(tag0.hashCode(), tag2.hashCode());
    }

    @Test
    void testToString() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        assertEquals("\"key0\": [0, 2]", tag0.toString());
        assertEquals("\"key1\": [1]", tag1.toString());
        assertEquals(tag0.toString(), tag2.toString());
    }
}