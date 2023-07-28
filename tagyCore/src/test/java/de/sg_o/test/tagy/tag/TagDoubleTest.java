package de.sg_o.test.tagy.tag;

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TagDoubleTest {
    TagDouble tag0;
    TagDouble tag1;
    TagDouble tag2;
    TagDouble tag3;
    TagDouble tag4;

    @BeforeEach
    void setUp() {
        TagDefinition td0 = new TagDefinition("key0", Type.DOUBLE);
        TagDefinition td1 = new TagDefinition("key1", Type.DOUBLE);

        tag0 = new TagDouble(td0, 1.0);
        tag1 = new TagDouble(td1, 1.0);
        tag2 = new TagDouble(td0, 2.4);
        tag3 = new TagDouble(td1, 2.4);
        tag4 = new TagDouble(td0, 1.0);
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
        assertEquals(1.0, tag0.getValue());
        assertEquals(1.0, tag1.getValue());
        assertEquals(2.4, tag2.getValue());
        assertEquals(2.4, tag3.getValue());
        assertEquals(1.0, tag4.getValue());
    }

    @Test
    void addToDocument() {
        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=1.0}", doc.toMap().toString());
        assertEquals(tag0, new TagDouble(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=1.0, key0=1.0}", doc.toMap().toString());
        assertEquals(tag1, new TagDouble(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=1.0, key0=2.4}", doc.toMap().toString());
        assertEquals(tag2, new TagDouble(tag2.getDefinition(), doc));
        tag3.addToDictionary(doc);
        assertEquals("{key1=2.4, key0=2.4}", doc.toMap().toString());
        assertEquals(tag3, new TagDouble(tag3.getDefinition(), doc));
        tag4.addToDictionary(doc);
        assertEquals("{key1=2.4, key0=1.0}", doc.toMap().toString());
        assertEquals(tag4, new TagDouble(tag4.getDefinition(), doc));
        assertEquals(tag4, Tag.create(tag4.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[1.0]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[1.0, 1.0]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[1.0, 1.0, 2.4]", array.toList().toString());
        tag3.addToArray(array);
        assertEquals("[1.0, 1.0, 2.4, 2.4]", array.toList().toString());
        tag4.addToArray(array);
        assertEquals("[1.0, 1.0, 2.4, 2.4, 1.0]", array.toList().toString());
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

        assertEquals(-1804549357, tag0.hashCode());
        assertEquals(496953075, tag1.hashCode());
        assertEquals(-944717037, tag2.hashCode());
        assertEquals(1356785395, tag3.hashCode());
        assertEquals(tag0.hashCode(), tag4.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("\"key0\": 1.0", tag0.toString());
        assertEquals("\"key1\": 1.0", tag1.toString());
        assertEquals("\"key0\": 2.4", tag2.toString());
        assertEquals("\"key1\": 2.4", tag3.toString());
        assertEquals(tag0.toString(), tag4.toString());
    }
}