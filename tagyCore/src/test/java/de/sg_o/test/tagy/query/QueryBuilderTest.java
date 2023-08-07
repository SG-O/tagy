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

package de.sg_o.test.tagy.query;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.query.MetaDataQueryBuilder;
import de.sg_o.lib.tagy.query.QueryInternal;
import de.sg_o.lib.tagy.query.properties.*;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {
    Project project0;

    MetaData md0;
    MetaData md1;

    TagDefinition td0;
    TagDefinition td1;
    TagDefinition td2;
    TagDefinition td3;

    TagDefinition tdl0;

    Tag tag0;
    Tag tag1;
    TagList tag2;
    Tag tag3;

    @BeforeEach
    void setUp() {
        DB.closeDb();
        new TestDb();

        project0 = Project.openOrCreate("Test_Query_Meta_1", User.getLocalUser());
        project0.save();

        URL sampleMediaFile = this.getClass().getResource("/sampleFiles/media/video/sample03.wmv");
        assertNotNull(sampleMediaFile);
        URL sampleMixedFile = this.getClass().getResource("/sampleFiles/mixed/sample07.webp");
        assertNotNull(sampleMixedFile);

        FileInfo fi0 = FileInfo.openOrCreate(sampleMediaFile, project0);
        FileInfo fi1 = FileInfo.openOrCreate(sampleMixedFile, project0);

        fi0.save();
        fi1.save();

        final ArrayList<String> enumerators0 = new ArrayList<>();
        enumerators0.add("Option 1");
        enumerators0.add("Option 2");

        td0 = new TagDefinition("key0", Type.STRING);
        td1 = new TagDefinition("key1", Type.STRING);
        td2 = new TagDefinition("key2", Type.LIST);
        tdl0 = new TagDefinition("value0", Type.LONG);
        td2.setInternal(tdl0);
        td3 = new TagDefinition("key3", Type.ENUM);
        td3.addAllEnumerators(enumerators0);

        StructureDefinition structureDefinition = project0.resolveStructureDefinition();
        ArrayList<TagDefinition> tagDefinitions = new ArrayList<>();
        tagDefinitions.add(td0);
        tagDefinitions.add(td1);
        tagDefinitions.add(td2);
        tagDefinitions.add(td3);
        structureDefinition.setTagDefinitions(tagDefinitions);
        structureDefinition.save();

        tag0 = new TagString(td0, "Test String 0");
        tag1 = new TagString(td1, "Test String 1");
        tag2 = new TagList(td2);
        Tag internal0 = new TagLong(tdl0, 5);
        Tag internal1 = new TagLong(tdl0, 500);
        tag2.addValue(internal0);
        tag2.addValue(internal1);
        tag3 = new TagEnum(td3, 0);

        md0 = new MetaData(fi0, project0);
        md0.addTag(tag0);
        md0.addTag(tag1);
        assertTrue(md0.save());

        md1 = new MetaData(fi1, project0);
        md1.addTag(tag2);
        md1.addTag(tag3);
        assertTrue(md1.save());
    }

    @Test
    void query() {

        MetaDataQueryBuilder qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 0"));
        List<MetaData> result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td1, "Test String 0"));
        result = qb.query(0,0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 0"));
        qb.addQueryElement(new Equals(td1, "Test String 1"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 1"));
        qb.addQueryElement(new Equals(td1, "Test String 1"));
        result = qb.query(0,0);
        assertEquals(0, result.size());


        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td3, 0));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td3, 1));
        result = qb.query(0,0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new QueryInternal(new Equals(tdl0, 5)));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Between(td3, -1, 1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Between(td3, 5, 10));
        result = qb.query(0,0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Greater(td3, -1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Less(td3, 1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Contains(td0, "Test"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new StartsWith(td1, "Test"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new EndsWith(td1, "1"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new IsNull(td2));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new NotNull(td1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
    }
}