package de.sg_o.test.tagy.tag;

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.string.TagString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TagStringTest {
    TagString tag0;
    TagString tag1;
    TagString tag2;
    TagString tag3;
    TagString tag4;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", Type.STRING);
        TagDefinition td1 = new TagDefinition("key1", Type.STRING);

        tag0 = new TagString(td0, "Value 1");
        tag1 = new TagString(td1, "Value 1");
        tag2 = new TagString(td0, "Value 2");
        tag3 = new TagString(td1, "Value 2");
        tag4 = new TagString(td0, "Value 1");
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
        assertEquals("Value 1", tag0.getValue());
        assertEquals("Value 1", tag1.getValue());
        assertEquals("Value 2", tag2.getValue());
        assertEquals("Value 2", tag3.getValue());
        assertEquals("Value 1", tag4.getValue());
    }

    @Test
    void addToDocument() {
        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=Value 1}", doc.toMap().toString());
        assertEquals(tag0, new TagString(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=Value 1, key0=Value 1}", doc.toMap().toString());
        assertEquals(tag1, new TagString(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=Value 1, key0=Value 2}", doc.toMap().toString());
        assertEquals(tag2, new TagString(tag2.getDefinition(), doc));
        tag3.addToDictionary(doc);
        assertEquals("{key1=Value 2, key0=Value 2}", doc.toMap().toString());
        assertEquals(tag3, new TagString(tag3.getDefinition(), doc));
        tag4.addToDictionary(doc);
        assertEquals("{key1=Value 2, key0=Value 1}", doc.toMap().toString());
        assertEquals(tag4, new TagString(tag4.getDefinition(), doc));
        assertEquals(tag4, Tag.create(tag4.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[Value 1]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[Value 1, Value 1]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[Value 1, Value 1, Value 2]", array.toList().toString());
        tag3.addToArray(array);
        assertEquals("[Value 1, Value 1, Value 2, Value 2]", array.toList().toString());
        tag4.addToArray(array);
        assertEquals("[Value 1, Value 1, Value 2, Value 2, Value 1]", array.toList().toString());
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

        assertEquals(-1790332429, tag0.hashCode());
        assertEquals(511170003, tag1.hashCode());
        assertEquals(-1790332428, tag2.hashCode());
        assertEquals(511170004, tag3.hashCode());
        assertEquals(tag0.hashCode(), tag4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": \"Value 1\"", tag0.toString());
        assertEquals("\"key1\": \"Value 1\"", tag1.toString());
        assertEquals("\"key0\": \"Value 2\"", tag2.toString());
        assertEquals("\"key1\": \"Value 2\"", tag3.toString());
        assertEquals(tag0.toString(), tag4.toString());
    }
}