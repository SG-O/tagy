package de.sg_o.test.tagy.tag;

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagLongTest {
    TagLong tag0;
    TagLong tag1;
    TagLong tag2;
    TagLong tag3;
    TagLong tag4;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", Type.LONG);
        TagDefinition td1 = new TagDefinition("key1", Type.LONG);

        tag0 = new TagLong(td0, 1);
        tag1 = new TagLong(td1, 1);
        tag2 = new TagLong(td0, 2);
        tag3 = new TagLong(td1, 2);
        tag4 = new TagLong(td0, 1);
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
        assertEquals(1L, tag0.getValue());
        assertEquals(1L, tag1.getValue());
        assertEquals(2L, tag2.getValue());
        assertEquals(2L, tag3.getValue());
        assertEquals(1L, tag4.getValue());
    }

    @Test
    void addToDocument() {
        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=1}", doc.toMap().toString());
        assertEquals(tag0, new TagLong(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=1, key0=1}", doc.toMap().toString());
        assertEquals(tag1, new TagLong(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=1, key0=2}", doc.toMap().toString());
        assertEquals(tag2, new TagLong(tag2.getDefinition(), doc));
        tag3.addToDictionary(doc);
        assertEquals("{key1=2, key0=2}", doc.toMap().toString());
        assertEquals(tag3, new TagLong(tag3.getDefinition(), doc));
        tag4.addToDictionary(doc);
        assertEquals("{key1=2, key0=1}", doc.toMap().toString());
        assertEquals(tag4, new TagLong(tag4.getDefinition(), doc));
        assertEquals(tag4, Tag.create(tag4.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[1]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[1, 1]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[1, 1, 2]", array.toList().toString());
        tag3.addToArray(array);
        assertEquals("[1, 1, 2, 2]", array.toList().toString());
        tag4.addToArray(array);
        assertEquals("[1, 1, 2, 2, 1]", array.toList().toString());
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

        assertEquals(-325085643, tag0.hashCode());
        assertEquals(1976416789, tag1.hashCode());
        assertEquals(-325085642, tag2.hashCode());
        assertEquals(1976416790, tag3.hashCode());
        assertEquals(tag0.hashCode(), tag4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": 1", tag0.toString());
        assertEquals("\"key1\": 1", tag1.toString());
        assertEquals("\"key0\": 2", tag2.toString());
        assertEquals("\"key1\": 2", tag3.toString());
        assertEquals(tag0.toString(), tag4.toString());
    }
}