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

package de.sg_o.test.tagy.def;

import de.sg_o.lib.tagy.def.Parameter;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagDefinitionTest {
    TagDefinition td0;
    TagDefinition td1;
    TagDefinition td2;
    TagDefinition td3;
    TagDefinition td4;
    TagDefinition td5;

    @BeforeEach
    void setUp() {
        td0 = new TagDefinition("test0", Type.LIST);
        td1 = new TagDefinition("test1", Type.LONG);
        td2 = new TagDefinition("test2", Type.DOUBLE);
        td3 = new TagDefinition("test3", Type.ENUM);
        td4 = new TagDefinition("test4", Type.STRING);
        td5 = new TagDefinition("test5", Type.DATE);
    }

    @Test
    void getKey() {
        assertEquals("test0", td0.getKey());
        assertEquals("test1", td1.getKey());
        assertEquals("test2", td2.getKey());
        assertEquals("test3", td3.getKey());
        assertEquals("test4", td4.getKey());
        assertEquals("test5", td5.getKey());
    }

    @Test
    void getType() {
        assertEquals(Type.LIST, td0.getType());
        assertEquals(Type.LONG, td1.getType());
        assertEquals(Type.DOUBLE, td2.getType());
        assertEquals(Type.ENUM, td3.getType());
        assertEquals(Type.STRING, td4.getType());
        assertEquals(Type.DATE, td5.getType());
    }

    @Test
    void getMin() {
        assertEquals(Double.NEGATIVE_INFINITY, td0.getMin());
        assertEquals(Double.NEGATIVE_INFINITY, td1.getMin());
        assertEquals(Double.NEGATIVE_INFINITY, td2.getMin());
        assertEquals(Double.NEGATIVE_INFINITY, td3.getMin());
        assertEquals(Double.NEGATIVE_INFINITY, td4.getMin());
        assertEquals(Double.NEGATIVE_INFINITY, td5.getMin());

        td0.setMin(1);
        td1.setMin(2);
        td2.setMin(3);
        td3.setMin(4);
        td4.setMin(5);
        td5.setMin(6);

        assertEquals(1, td0.getMin());
        assertEquals(2, td1.getMin());
        assertEquals(3, td2.getMin());
        assertEquals(4, td3.getMin());
        assertEquals(5, td4.getMin());
        assertEquals(6, td5.getMin());
    }

    @Test
    void getMax() {
        assertEquals(Double.POSITIVE_INFINITY, td0.getMax());
        assertEquals(Double.POSITIVE_INFINITY, td1.getMax());
        assertEquals(Double.POSITIVE_INFINITY, td2.getMax());
        assertEquals(Double.POSITIVE_INFINITY, td3.getMax());
        assertEquals(Double.POSITIVE_INFINITY, td4.getMax());
        assertEquals(Double.POSITIVE_INFINITY, td5.getMax());

        td0.setMax(10);
        td1.setMax(20);
        td2.setMax(30);
        td3.setMax(40);
        td4.setMax(50);
        td5.setMax(60);

        assertEquals(10, td0.getMax());
        assertEquals(20, td1.getMax());
        assertEquals(30, td2.getMax());
        assertEquals(40, td3.getMax());
        assertEquals(50, td4.getMax());
        assertEquals(60, td5.getMax());
    }

    @Test
    void isRequired() {
        assertFalse(td0.isRequired());
        assertFalse(td1.isRequired());
        assertFalse(td2.isRequired());
        assertFalse(td3.isRequired());
        assertFalse(td4.isRequired());
        assertFalse(td5.isRequired());

        td0.setRequired(true);
        td1.setRequired(true);
        td2.setRequired(true);
        td3.setRequired(true);
        td4.setRequired(true);
        td5.setRequired(true);

        assertTrue(td0.isRequired());
        assertTrue(td1.isRequired());
        assertTrue(td2.isRequired());
        assertTrue(td3.isRequired());
        assertTrue(td4.isRequired());
        assertTrue(td5.isRequired());
    }

    @Test
    void getEnumerators() {
        assertNull(td0.getEnumerators());
        assertNull(td1.getEnumerators());
        assertNull(td2.getEnumerators());
        assertEquals(0, td3.getEnumerators().size());
        assertNull(td4.getEnumerators());
        assertNull(td5.getEnumerators());

        assertFalse(td0.addEnumerator("TEST"));
        assertFalse(td1.removeEnumerator("TEST"));
        assertFalse(td2.removeEnumerator(0));

        assertTrue(td3.addEnumerator("TEST"));
        assertTrue(td3.addEnumerator("TEST1"));
        assertTrue(td3.removeEnumerator("TEST"));
        assertTrue(td3.removeEnumerator(0));

        assertEquals(0, td3.getEnumerators().size());

        assertTrue(td3.addEnumerator("TEST"));
        assertTrue(td3.addEnumerator("TEST1"));

        assertEquals(2, td3.getEnumerators().size());
        assertEquals("[TEST, TEST1]", td3.getEnumerators().toString());
    }

    @Test
    void getInternal() {
        assertNull(td0.getInternal());
        assertNull(td1.getInternal());
        assertNull(td2.getInternal());
        assertNull(td3.getInternal());
        assertNull(td4.getInternal());
        assertNull(td5.getInternal());

        td0.setInternal(new TagDefinition("Key", Type.LONG));

        assertEquals("{\n" +
                        "\t\"key\": \"Key\",\n" +
                        "\t\"type\": \"LONG\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td0.getInternal().toString());
    }

    @Test
    void getEncoded() {
        assertNull(td0.getEncoded());
        assertEquals("{type=1, key=test1, required=false}", td1.getEncoded().toMap().toString());
        assertEquals("{type=2, key=test2, required=false}", td2.getEncoded().toMap().toString());
        assertEquals("{enumerators=[], type=3, key=test3, required=false}", td3.getEncoded().toMap().toString());
        assertEquals("{type=4, key=test4, required=false}", td4.getEncoded().toMap().toString());
        assertEquals("{type=5, key=test5, required=false}", td5.getEncoded().toMap().toString());

        td0.setName("Name 0");
        td1.setName("Name 1");
        td2.setName("Name 2");
        td3.setName("Name 3");
        td4.setName("Name 4");
        td5.setName("Name 5");

        td0.setDescription("Description 0");
        td1.setDescription("Description 1");
        td2.setDescription("Description 2");
        td3.setDescription("Description 3");
        td4.setDescription("Description 4");
        td5.setDescription("Description 5");

        td0.setMin(1);
        td1.setMin(2);
        td2.setMin(3);
        td3.setMin(4);
        td4.setMin(5);
        td5.setMin(6);

        td0.setMax(10);
        td1.setMax(20);
        td2.setMax(30);
        td3.setMax(40);
        td4.setMax(50);
        td5.setMax(60);

        td0.setRequired(true);
        td1.setRequired(true);
        td2.setRequired(true);
        td3.setRequired(true);
        td4.setRequired(true);
        td5.setRequired(true);

        td0.setParameter(Parameter.IN);
        td1.setParameter(Parameter.OUT);
        td2.setParameter(Parameter.LENGTH);
        td3.setParameter(Parameter.LENGTH);
        td4.setParameter(Parameter.OUT);
        td5.setParameter(Parameter.IN);

        assertTrue(td3.addEnumerator("TEST"));
        assertTrue(td3.addEnumerator("TEST1"));

        td0.setInternal(new TagDefinition("Key", Type.LONG));

        assertEquals("{internal={type=1, key=Key, required=false}, min=1.0, max=10.0, parameter=1, name=Name 0, description=Description 0, type=0, key=test0, required=true}",
                td0.getEncoded().toMap().toString());
        assertEquals("{min=2.0, max=20.0, parameter=2, name=Name 1, description=Description 1, type=1, key=test1, required=true}",
                td1.getEncoded().toMap().toString());
        assertEquals("{min=3.0, max=30.0, parameter=3, name=Name 2, description=Description 2, type=2, key=test2, required=true}",
                td2.getEncoded().toMap().toString());
        assertEquals("{min=4.0, enumerators=[TEST, TEST1], max=40.0, parameter=3, name=Name 3, description=Description 3, type=3, key=test3, required=true}",
                td3.getEncoded().toMap().toString());
        assertEquals("{min=5.0, max=50.0, parameter=2, name=Name 4, description=Description 4, type=4, key=test4, required=true}",
                td4.getEncoded().toMap().toString());
        assertEquals("{min=6.0, max=60.0, parameter=1, name=Name 5, description=Description 5, type=5, key=test5, required=true}",
                td5.getEncoded().toMap().toString());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void testEquals() {
        TagDefinition td6 = new TagDefinition("test0", Type.LIST);

        assertEquals(td0, td0);
        assertEquals(td2, td2);
        assertNotEquals(td0, td1);
        assertNotEquals(td0, td2);
        assertNotEquals(td0, td3);
        assertNotEquals(td0, td4);
        assertNotEquals(td0, td5);
        assertEquals(td0, td6);

        assertEquals(-1094124148, td0.hashCode());
        assertEquals(2084378861, td1.hashCode());
        assertEquals(967914574, td2.hashCode());
        assertEquals(-148548752, td3.hashCode());
        assertEquals(-1265014000, td4.hashCode());
        assertEquals(1913489009, td5.hashCode());
        assertEquals(td0.hashCode(), td6.hashCode());

        td0.setMin(1);
        td1.setMin(2);
        td2.setMin(3);
        td3.setMin(4);
        td4.setMin(5);
        td5.setMin(6);

        td0.setMax(10);
        td1.setMax(20);
        td2.setMax(30);
        td3.setMax(40);
        td4.setMax(50);
        td5.setMax(60);

        td0.setRequired(true);
        td1.setRequired(true);
        td2.setRequired(true);
        td3.setRequired(true);
        td4.setRequired(true);
        td5.setRequired(true);

        assertTrue(td3.addEnumerator("TEST"));
        assertTrue(td3.addEnumerator("TEST1"));

        td0.setInternal(new TagDefinition("Key", Type.LONG));

        assertEquals(td0, td0);
        assertEquals(td2, td2);
        assertNotEquals(td0, td1);
        assertNotEquals(td0, td2);
        assertNotEquals(td0, td3);
        assertNotEquals(td0, td4);
        assertNotEquals(td0, td5);
        assertNotEquals(td0, td6);

        TagDefinition td7 = new TagDefinition(td0.getEncoded());
        TagDefinition td8 = new TagDefinition(td1.getEncoded());
        TagDefinition td9 = new TagDefinition(td2.getEncoded());
        TagDefinition td10 = new TagDefinition(td3.getEncoded());
        TagDefinition td11 = new TagDefinition(td4.getEncoded());
        TagDefinition td12 = new TagDefinition(td5.getEncoded());

        assertEquals(td0, td7);
        assertEquals(td1, td8);
        assertEquals(td2, td9);
        assertEquals(td3, td10);
        assertEquals(td4, td11);
        assertEquals(td5, td12);

        td0.setName("Name 0");
        td1.setName("Name 1");
        td2.setName("Name 2");
        td3.setName("Name 3");
        td4.setName("Name 4");
        td5.setName("Name 5");

        td0.setDescription("Description 0");
        td1.setDescription("Description 1");
        td2.setDescription("Description 2");
        td3.setDescription("Description 3");
        td4.setDescription("Description 4");
        td5.setDescription("Description 5");

        td0.setParameter(Parameter.IN);
        td1.setParameter(Parameter.OUT);
        td2.setParameter(Parameter.LENGTH);
        td3.setParameter(Parameter.LENGTH);
        td4.setParameter(Parameter.OUT);
        td5.setParameter(Parameter.IN);

        td7 = new TagDefinition(td0.getEncoded());
        td8 = new TagDefinition(td1.getEncoded());
        td9 = new TagDefinition(td2.getEncoded());
        td10 = new TagDefinition(td3.getEncoded());
        td11 = new TagDefinition(td4.getEncoded());
        td12 = new TagDefinition(td5.getEncoded());

        assertEquals(td0, td7);
        assertEquals(td1, td8);
        assertEquals(td2, td9);
        assertEquals(td3, td10);
        assertEquals(td4, td11);
        assertEquals(td5, td12);
    }

    @Test
    void testToString() {
        assertEquals("{\n" +
                        "\t\"key\": \"test0\",\n" +
                        "\t\"type\": \"LIST\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td0.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test1\",\n" +
                        "\t\"type\": \"LONG\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td1.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test2\",\n" +
                        "\t\"type\": \"DOUBLE\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td2.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test3\",\n" +
                        "\t\"type\": \"ENUM\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td3.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test4\",\n" +
                        "\t\"type\": \"STRING\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td4.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test5\",\n" +
                        "\t\"type\": \"DATE\",\n" +
                        "\t\"required\": false,\n" +
                        "\t\"enumerators\": []\n" +
                        "}",
                td5.toString());

        td0.setName("Name 0");
        td1.setName("Name 1");
        td2.setName("Name 2");
        td3.setName("Name 3");
        td4.setName("Name 4");
        td5.setName("Name 5");

        td0.setDescription("Description 0");
        td1.setDescription("Description 1");
        td2.setDescription("Description 2");
        td3.setDescription("Description 3");
        td4.setDescription("Description 4");
        td5.setDescription("Description 5");

        td0.setMin(1);
        td1.setMin(2);
        td2.setMin(3);
        td3.setMin(4);
        td4.setMin(5);
        td5.setMin(6);

        td0.setMax(10);
        td1.setMax(20);
        td2.setMax(30);
        td3.setMax(40);
        td4.setMax(50);
        td5.setMax(60);

        td0.setRequired(true);
        td1.setRequired(true);
        td2.setRequired(true);
        td3.setRequired(true);
        td4.setRequired(true);
        td5.setRequired(true);

        td0.setParameter(Parameter.IN);
        td1.setParameter(Parameter.OUT);
        td2.setParameter(Parameter.LENGTH);
        td3.setParameter(Parameter.LENGTH);
        td4.setParameter(Parameter.OUT);
        td5.setParameter(Parameter.IN);

        assertTrue(td3.addEnumerator("TEST"));
        assertTrue(td3.addEnumerator("TEST1"));

        td0.setInternal(new TagDefinition("Key", Type.LONG));

        assertEquals("{\n" +
                        "\t\"key\": \"test0\",\n" +
                        "\t\"type\": \"LIST\",\n" +
                        "\t\"name\": \"Name 0\",\n" +
                        "\t\"description\": \"Description 0\",\n" +
                        "\t\"min\": 1.0,\n" +
                        "\t\"max\": 10.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": [],\n" +
                        "\t\"internal\": \n" +
                        "\t{\n" +
                        "\t\t\"key\": \"Key\",\n" +
                        "\t\t\"type\": \"LONG\",\n" +
                        "\t\t\"required\": false,\n" +
                        "\t\t\"enumerators\": []\n" +
                        "\t},\n" +
                        "\t\"parameter\": \"IN\"\n" +
                        "}",
                td0.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test1\",\n" +
                        "\t\"type\": \"LONG\",\n" +
                        "\t\"name\": \"Name 1\",\n" +
                        "\t\"description\": \"Description 1\",\n" +
                        "\t\"min\": 2.0,\n" +
                        "\t\"max\": 20.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": [],\n" +
                        "\t\"parameter\": \"OUT\"\n" +
                        "}",
                td1.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test2\",\n" +
                        "\t\"type\": \"DOUBLE\",\n" +
                        "\t\"name\": \"Name 2\",\n" +
                        "\t\"description\": \"Description 2\",\n" +
                        "\t\"min\": 3.0,\n" +
                        "\t\"max\": 30.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": [],\n" +
                        "\t\"parameter\": \"LENGTH\"\n" +
                        "}",
                td2.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test3\",\n" +
                        "\t\"type\": \"ENUM\",\n" +
                        "\t\"name\": \"Name 3\",\n" +
                        "\t\"description\": \"Description 3\",\n" +
                        "\t\"min\": 4.0,\n" +
                        "\t\"max\": 40.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": \n" +
                        "\t\t[\n" +
                        "\t\t\t\"TEST\",\n" +
                        "\t\t\t\"TEST1\"\n" +
                        "\t\t],\n" +
                        "\t\"parameter\": \"LENGTH\"\n" +
                        "}",
                td3.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test4\",\n" +
                        "\t\"type\": \"STRING\",\n" +
                        "\t\"name\": \"Name 4\",\n" +
                        "\t\"description\": \"Description 4\",\n" +
                        "\t\"min\": 5.0,\n" +
                        "\t\"max\": 50.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": [],\n" +
                        "\t\"parameter\": \"OUT\"\n" +
                        "}",
                td4.toString());
        assertEquals("{\n" +
                        "\t\"key\": \"test5\",\n" +
                        "\t\"type\": \"DATE\",\n" +
                        "\t\"name\": \"Name 5\",\n" +
                        "\t\"description\": \"Description 5\",\n" +
                        "\t\"min\": 6.0,\n" +
                        "\t\"max\": 60.0,\n" +
                        "\t\"required\": true,\n" +
                        "\t\"enumerators\": [],\n" +
                        "\t\"parameter\": \"IN\"\n" +
                        "}",
                td5.toString());
    }

    @Test
    void getName() {
        assertEquals("test0", td0.getName());
        assertEquals("test1", td1.getName());
        assertEquals("test2", td2.getName());
        assertEquals("test3", td3.getName());
        assertEquals("test4", td4.getName());
        assertEquals("test5", td5.getName());

        td0.setName("Name 0");
        td1.setName("Name 1");
        td2.setName("Name 2");
        td3.setName("Name 3");
        td4.setName("Name 4");
        td5.setName("Name 5");

        assertEquals("Name 0", td0.getName());
        assertEquals("Name 1", td1.getName());
        assertEquals("Name 2", td2.getName());
        assertEquals("Name 3", td3.getName());
        assertEquals("Name 4", td4.getName());
        assertEquals("Name 5", td5.getName());

        assertEquals("test0", td0.getKey());
        assertEquals("test1", td1.getKey());
        assertEquals("test2", td2.getKey());
        assertEquals("test3", td3.getKey());
        assertEquals("test4", td4.getKey());
        assertEquals("test5", td5.getKey());
    }

    @Test
    void getDescription() {
        assertNull(td0.getDescription());
        assertNull(td1.getDescription());
        assertNull(td2.getDescription());
        assertNull(td3.getDescription());
        assertNull(td4.getDescription());
        assertNull(td5.getDescription());

        td0.setDescription("Description 0");
        td1.setDescription("Description 1");
        td2.setDescription("Description 2");
        td3.setDescription("Description 3");
        td4.setDescription("Description 4");
        td5.setDescription("Description 5");

        assertEquals("Description 0", td0.getDescription());
        assertEquals("Description 1", td1.getDescription());
        assertEquals("Description 2", td2.getDescription());
        assertEquals("Description 3", td3.getDescription());
        assertEquals("Description 4", td4.getDescription());
        assertEquals("Description 5", td5.getDescription());
    }

    @Test
    void getParameter() {
        assertEquals(Parameter.NONE, td0.getParameter());
        assertEquals(Parameter.NONE, td1.getParameter());
        assertEquals(Parameter.NONE, td2.getParameter());
        assertEquals(Parameter.NONE, td3.getParameter());
        assertEquals(Parameter.NONE, td4.getParameter());
        assertEquals(Parameter.NONE, td5.getParameter());

        td0.setParameter(Parameter.IN);
        td1.setParameter(Parameter.OUT);
        td2.setParameter(Parameter.LENGTH);
        td3.setParameter(Parameter.LENGTH);
        td4.setParameter(Parameter.OUT);
        td5.setParameter(Parameter.IN);

        assertEquals(Parameter.IN, td0.getParameter());
        assertEquals(Parameter.OUT, td1.getParameter());
        assertEquals(Parameter.LENGTH, td2.getParameter());
        assertEquals(Parameter.LENGTH, td3.getParameter());
        assertEquals(Parameter.OUT, td4.getParameter());
        assertEquals(Parameter.IN, td5.getParameter());
    }
}