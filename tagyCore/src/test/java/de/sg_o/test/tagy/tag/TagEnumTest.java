package de.sg_o.test.tagy.tag;

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        TagDefinition td0 = new TagDefinition("key0", Type.ENUM);
        TagDefinition td1 = new TagDefinition("key1", Type.ENUM);
        TagDefinition td2 = new TagDefinition("key0", Type.ENUM);
        TagDefinition td3 = new TagDefinition("key1", Type.ENUM);

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
        MutableDictionary doc = new MutableDictionary();
        tag0.addToDictionary(doc);
        assertEquals("{key0=0}", doc.toMap().toString());
        assertEquals(tag0, new TagEnum(tag0.getDefinition(), doc));
        tag1.addToDictionary(doc);
        assertEquals("{key1=0, key0=0}", doc.toMap().toString());
        assertEquals(tag1, new TagEnum(tag1.getDefinition(), doc));
        tag2.addToDictionary(doc);
        assertEquals("{key1=0, key0=0}", doc.toMap().toString());
        assertEquals(tag2, new TagEnum(tag2.getDefinition(), doc));
        tag3.addToDictionary(doc);
        assertEquals("{key1=1, key0=0}", doc.toMap().toString());
        assertEquals(tag3, new TagEnum(tag3.getDefinition(), doc));
        tag4.addToDictionary(doc);
        assertEquals("{key1=1, key0=4}", doc.toMap().toString());
        assertEquals(tag4, new TagEnum(tag4.getDefinition(), doc));
        tag5.addToDictionary(doc);
        assertEquals("{key1=1, key0=0}", doc.toMap().toString());
        assertEquals(tag5, new TagEnum(tag5.getDefinition(), doc));
        assertEquals(tag5, Tag.create(tag5.getDefinition(), doc));
    }

    @Test
    void addToArray() {
        MutableArray array = new MutableArray();
        tag0.addToArray(array);
        assertEquals("[0]", array.toList().toString());
        tag1.addToArray(array);
        assertEquals("[0, 0]", array.toList().toString());
        tag2.addToArray(array);
        assertEquals("[0, 0, 0]", array.toList().toString());
        tag3.addToArray(array);
        assertEquals("[0, 0, 0, 1]", array.toList().toString());
        tag4.addToArray(array);
        assertEquals("[0, 0, 0, 1, 4]", array.toList().toString());
        tag5.addToArray(array);
        assertEquals("[0, 0, 0, 1, 4, 0]", array.toList().toString());
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

        assertEquals(211949616, tag0.hashCode());
        assertEquals(-1781515248, tag1.hashCode());
        assertEquals(213856240, tag2.hashCode());
        assertEquals(-1779608623, tag3.hashCode());
        assertEquals(211949620, tag4.hashCode());
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