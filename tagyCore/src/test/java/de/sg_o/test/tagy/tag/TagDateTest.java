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
import de.sg_o.lib.tagy.tag.date.TagDate;
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
        TagDefinition td0 = new TagDefinition("key0", Type.DATE);
        TagDefinition td1 = new TagDefinition("key1", Type.DATE);

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
        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=2023-01-01T00:00:00.000Z}", doc.toMap().toString());
        assertEquals(tag0, new TagDate(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=2023-01-01T00:00:00.000Z, key0=2023-01-01T00:00:00.000Z}", doc.toMap().toString());
        assertEquals(tag1, new TagDate(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=2023-01-01T00:00:00.000Z, key0=2023-01-02T00:00:00.000Z}", doc.toMap().toString());
        assertEquals(tag2, new TagDate(tag2.getDefinition(), doc));
        tag3.addToDictionary(doc);
        assertEquals("{key1=2023-01-02T00:00:00.000Z, key0=2023-01-02T00:00:00.000Z}", doc.toMap().toString());
        assertEquals(tag3, new TagDate(tag3.getDefinition(), doc));
        tag4.addToDictionary(doc);
        assertEquals("{key1=2023-01-02T00:00:00.000Z, key0=2023-01-01T00:00:00.000Z}", doc.toMap().toString());
        assertEquals(tag4, new TagDate(tag4.getDefinition(), doc));
        assertEquals(tag4, Tag.create(tag4.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[2023-01-01T00:00:00.000Z]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[2023-01-01T00:00:00.000Z, 2023-01-01T00:00:00.000Z]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[2023-01-01T00:00:00.000Z, 2023-01-01T00:00:00.000Z, 2023-01-02T00:00:00.000Z]", array.toList().toString());
        tag3.addToArray(array);
        assertEquals("[2023-01-01T00:00:00.000Z, 2023-01-01T00:00:00.000Z, 2023-01-02T00:00:00.000Z, 2023-01-02T00:00:00.000Z]", array.toList().toString());
        tag4.addToArray(array);
        assertEquals("[2023-01-01T00:00:00.000Z, 2023-01-01T00:00:00.000Z, 2023-01-02T00:00:00.000Z, 2023-01-02T00:00:00.000Z, 2023-01-01T00:00:00.000Z]", array.toList().toString());
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

        assertEquals(-154856651, tag0.hashCode());
        assertEquals(2146645781, tag1.hashCode());
        assertEquals(-68456651, tag2.hashCode());
        assertEquals(-2061921515, tag3.hashCode());
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