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
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagListTest {
    TagList tag0;
    TagList tag1;
    TagList tag2;
    TagList tag3;

    TagDefinition tdl0;
    TagDefinition tdl1;
    TagDefinition tdl2;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", TagDefinitionProto.Type.LIST);
        TagDefinition td1 = new TagDefinition("key1", TagDefinitionProto.Type.LIST);
        TagDefinition td2 = new TagDefinition("key2", TagDefinitionProto.Type.LIST);
        td2.setFixedSize(4);

        tdl0 = new TagDefinition("value0", TagDefinitionProto.Type.LONG);
        tdl1 = new TagDefinition("value1", TagDefinitionProto.Type.LONG);
        tdl2 = new TagDefinition("value2", TagDefinitionProto.Type.LONG);

        td0.setInternal(tdl0);
        td1.setInternal(tdl1);
        td2.setInternal(tdl2);

        tag0 = new TagList(td0);
        tag1 = new TagList(td1);
        tag2 = new TagList(td0);
        tag3 = new TagList(td2);
    }

    @Test
    void getKey() {
        assertEquals("key0", tag0.getKey());
        assertEquals("key1", tag1.getKey());
        assertEquals("key0", tag2.getKey());
        assertEquals("key2", tag3.getKey());
    }

    @Test
    void getValues() {
        assertEquals(0, tag0.getValue().size());
        assertEquals(0, tag1.getValue().size());
        assertEquals(0, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));
        assertFalse(tag3.addValue(new TagLong(tdl2, 0)));

        assertEquals(1, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertTrue(tag0.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 2)));

        assertEquals(2, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(2, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertEquals("\"key0\": [0, 2]", tag0.toString());
        assertEquals("\"key1\": [1]", tag1.toString());
        assertEquals("\"key0\": [0, 2]", tag2.toString());
        assertEquals("\"key2\": [0, 0, 0, 0]", tag3.toString());

        assertNotNull(tag0.removeValue(1));
        assertNotNull(tag1.removeValue(0));
        assertNotNull(tag2.removeValue(0));
        assertNull(tag2.removeValue(2));
        assertNull(tag2.removeValue(-1));
        assertNull(tag3.removeValue(0));

        assertEquals(1, tag0.getValue().size());
        assertEquals(0, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertEquals("\"key0\": [0]", tag0.toString());
        assertEquals("\"key1\": []", tag1.toString());
        assertEquals("\"key0\": [2]", tag2.toString());
        assertEquals("\"key2\": [0, 0, 0, 0]", tag3.toString());

        assertTrue(tag0.removeValue(new TagLong(tdl0, 0)));
        assertTrue(tag2.removeValue(new TagLong(tdl0, 2)));
        assertFalse(tag2.removeValue(new TagLong(tdl0, 2)));
        assertFalse(tag3.removeValue(new TagLong(tdl2, 0)));

        assertEquals("\"key0\": []", tag0.toString());
        assertEquals("\"key1\": []", tag1.toString());
        assertEquals("\"key0\": []", tag2.toString());
        assertEquals("\"key2\": [0, 0, 0, 0]", tag3.toString());

        assertTrue(tag0.setValue(new TagLong(tdl0, 2), 0));
        assertTrue(tag1.setValue(new TagLong(tdl0, 4), 0));
        assertTrue(tag2.setValue(new TagLong(tdl0, 2), 0));
        assertTrue(tag3.setValue(new TagLong(tdl0, 8), 0));

        assertEquals(1, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertEquals("\"key0\": [2]", tag0.toString());
        assertEquals("\"key1\": [4]", tag1.toString());
        assertEquals("\"key0\": [2]", tag2.toString());
        assertEquals("\"key2\": [8, 0, 0, 0]", tag3.toString());

        assertTrue(tag0.setValue(new TagLong(tdl0, 3), 0));
        assertTrue(tag1.setValue(new TagLong(tdl0, 2), 0));
        assertTrue(tag2.setValue(new TagLong(tdl0, 3), 0));
        assertTrue(tag3.setValue(new TagLong(tdl0, 5), 0));

        assertEquals(1, tag0.getValue().size());
        assertEquals(1, tag1.getValue().size());
        assertEquals(1, tag2.getValue().size());
        assertEquals(4, tag3.getValue().size());

        assertEquals("\"key0\": [3]", tag0.toString());
        assertEquals("\"key1\": [2]", tag1.toString());
        assertEquals("\"key0\": [3]", tag2.toString());
        assertEquals("\"key2\": [5, 0, 0, 0]", tag3.toString());

        assertTrue(tag0.setValue(new TagLong(tdl0, 2), 2));
        assertTrue(tag1.setValue(new TagLong(tdl0, 4), 2));
        assertTrue(tag2.setValue(new TagLong(tdl0, 2), 2));
        assertTrue(tag3.setValue(new TagLong(tdl0, 8), 2));

        assertEquals("\"key0\": [3, 2]", tag0.toString());
        assertEquals("\"key1\": [2, 4]", tag1.toString());
        assertEquals("\"key0\": [3, 2]", tag2.toString());
        assertEquals("\"key2\": [5, 0, 8, 0]", tag3.toString());

        assertTrue(tag0.setValue(new TagLong(tdl0, 3), 4));
        assertTrue(tag1.setValue(new TagLong(tdl0, 2), 4));
        assertTrue(tag2.setValue(new TagLong(tdl0, 3), 4));
        assertFalse(tag3.setValue(new TagLong(tdl0, 5), 4));

        assertEquals("\"key0\": [3, 2, 3]", tag0.toString());
        assertEquals("\"key1\": [2, 4, 2]", tag1.toString());
        assertEquals("\"key0\": [3, 2, 3]", tag2.toString());
        assertEquals("\"key2\": [5, 0, 8, 0]", tag3.toString());
    }

    @Test
    void migration() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 5), 0));

        assertTrue(tag0.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 8), 2));

        TagMigration holder0 = new TagMigration(tag0);
        TagMigration holder1 = new TagMigration(tag1);
        TagMigration holder2 = new TagMigration(tag2);
        TagMigration holder3 = new TagMigration(tag3);

        String json0 = holder0.getEncoded();
        String json1 = holder1.getEncoded();
        String json2 = holder2.getEncoded();
        String json3 = holder3.getEncoded();

        assertEquals("{\"values\":[0,2],\"isFixedSize\":false}", json0);
        assertEquals("{\"values\":[1],\"isFixedSize\":false}", json1);
        assertEquals("{\"values\":[0,2],\"isFixedSize\":false}", json2);
        assertEquals("{\"values\":[5,0,8,0],\"isFixedSize\":true}", json3);

        TagMigration holder5 = new TagMigration(tag0.getDefinition(), json0);
        TagMigration holder6 = new TagMigration(tag1.getDefinition(), json1);
        TagMigration holder7 = new TagMigration(tag2.getDefinition(), json2);
        TagMigration holder8 = new TagMigration(tag3.getDefinition(), json3);

        Tag decoded0 = holder5.getTag();
        Tag decoded1 = holder6.getTag();
        Tag decoded2 = holder7.getTag();
        Tag decoded3 = holder8.getTag();

        assertEquals(tag0, decoded0);
        assertEquals(tag1, decoded1);
        assertEquals(tag2, decoded2);
        assertEquals(tag3, decoded3);
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEquals() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 5), 0));

        assertTrue(tag0.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 8), 2));

        assertEquals(tag0, tag0);
        assertEquals(tag1, tag1);
        assertNotEquals(tag0, tag1);
        assertEquals(tag0, tag2);
        assertEquals(tag3, tag3);
        assertNotEquals(tag0, tag3);

        assertEquals(-221190587, tag0.hashCode());
        assertEquals(1260284727, tag1.hashCode());
        assertEquals(tag0.hashCode(), tag2.hashCode());
        assertEquals(-314740527, tag3.hashCode());
    }

    @Test
    void testToString() {
        assertTrue(tag0.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag1.addValue(new TagLong(tdl1, 1)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 0)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 5), 0));

        assertTrue(tag0.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag2.addValue(new TagLong(tdl0, 2)));
        assertTrue(tag3.setValue(new TagLong(tdl0, 8), 2));

        assertEquals("\"key0\": [0, 2]", tag0.toString());
        assertEquals("\"key1\": [1]", tag1.toString());
        assertEquals(tag0.toString(), tag2.toString());
        assertEquals("\"key2\": [5, 0, 8, 0]", tag3.toString());
    }
}