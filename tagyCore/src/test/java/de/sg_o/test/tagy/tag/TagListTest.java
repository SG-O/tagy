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

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.integer.TagLong;
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
        assertEquals(0, tag0.getValues().size());
        assertEquals(0, tag1.getValues().size());
        assertEquals(0, tag2.getValues().size());

        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertEquals(1, tag0.getValues().size());
        assertEquals(1, tag1.getValues().size());
        assertEquals(1, tag2.getValues().size());

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        assertEquals(2, tag0.getValues().size());
        assertEquals(1, tag1.getValues().size());
        assertEquals(2, tag2.getValues().size());

        assertEquals("\"key0\": [0, 2]", tag0.toString());
        assertEquals("\"key1\": [1]", tag1.toString());
        assertEquals("\"key0\": [0, 2]", tag2.toString());

        assertNotNull(tag0.removeValue(1));
        assertNotNull(tag1.removeValue(0));
        assertNotNull(tag2.removeValue(0));
        assertNull(tag2.removeValue(2));
        assertNull(tag2.removeValue(-1));

        assertEquals(1, tag0.getValues().size());
        assertEquals(0, tag1.getValues().size());
        assertEquals(1, tag2.getValues().size());

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

        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=[0, 2]}", doc.toMap().toString());
        assertEquals(tag0, new TagList(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=[1], key0=[0, 2]}", doc.toMap().toString());
        assertEquals(tag1, new TagList(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=[1], key0=[0, 2]}", doc.toMap().toString());
        assertEquals(tag2, new TagList(tag2.getDefinition(), doc));
        assertEquals(tag2, Tag.create(tag2.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));

        assertTrue(tag0.addValue(new TagLong(tdl2, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl2, 2)));

        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[[0, 2]]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[[0, 2], [1]]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[[0, 2], [1], [0, 2]]", array.toList().toString());
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

        assertEquals(856438021, tag0.hashCode());
        assertEquals(-635483306, tag1.hashCode());
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