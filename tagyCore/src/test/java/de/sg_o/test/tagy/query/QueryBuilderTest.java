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
import de.sg_o.lib.tagy.query.MetaDataQueryBuilder;
import de.sg_o.lib.tagy.query.modifiers.QueryInternal;
import de.sg_o.lib.tagy.query.properties.*;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.proto.tagy.TagDefinitionProto;
import de.sg_o.proto.tagy.query.QueryInternalProto;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {
    Project project0;
    Project project1;

    MetaData md0;
    MetaData md1;

    MetaData md2;
    MetaData md3;

    TagDefinition td0;
    TagDefinition td1;
    TagDefinition td2;
    TagDefinition td3;

    TagDefinition td4;
    TagDefinition td5;
    TagDefinition td6;
    TagDefinition td7;
    TagDefinition td8;


    TagDefinition tdl0;

    Tag tag0;
    Tag tag1;
    TagList tag2;
    Tag tag3;

    Tag tag4;
    Tag tag5;
    Tag tag6;
    Tag tag7;
    Tag tag8;

    Tag tag9;
    Tag tag10;
    Tag tag11;
    Tag tag12;
    Tag tag13;

    @BeforeEach
    void setUp() {
        DB.closeDb();
        new TestDb();

        project0 = Project.openOrCreate("Test_Query_Meta_1", User.getLocalUser());
        project0.save();

        project1 = Project.openOrCreate("Test_Query_Meta_2", User.getLocalUser());
        project1.save();

        URL sampleMediaFile = this.getClass().getResource("/sampleFiles/media/video/sample03.wmv");
        assertNotNull(sampleMediaFile);
        URL sampleMixedFile = this.getClass().getResource("/sampleFiles/mixed/sample07.webp");
        assertNotNull(sampleMixedFile);

        FileInfo fi0 = FileInfo.openOrCreate(sampleMediaFile, project0);
        FileInfo fi1 = FileInfo.openOrCreate(sampleMixedFile, project0);
        FileInfo fi2 = FileInfo.openOrCreate(sampleMediaFile, project1);
        FileInfo fi3 = FileInfo.openOrCreate(sampleMixedFile, project1);

        fi0.save();
        fi1.save();
        fi2.save();
        fi3.save();

        final ArrayList<String> enumerators0 = new ArrayList<>();
        enumerators0.add("Option 1");
        enumerators0.add("Option 2");

        td0 = new TagDefinition("key0", TagDefinitionProto.Type.STRING);
        td1 = new TagDefinition("key1", TagDefinitionProto.Type.STRING);
        td2 = new TagDefinition("key2", TagDefinitionProto.Type.LIST);
        tdl0 = new TagDefinition("value0", TagDefinitionProto.Type.LONG);
        td2.setInternal(tdl0);
        td3 = new TagDefinition("key3", TagDefinitionProto.Type.ENUM);
        td3.addAllEnumerators(enumerators0);

        td4 = new TagDefinition("long", TagDefinitionProto.Type.LONG);
        td5 = new TagDefinition("date", TagDefinitionProto.Type.DATE);
        td6 = new TagDefinition("double", TagDefinitionProto.Type.DOUBLE);
        td7 = new TagDefinition("bool", TagDefinitionProto.Type.BOOLEAN);
        td8 = new TagDefinition("String", TagDefinitionProto.Type.STRING);

        StructureDefinition structureDefinition0 = project0.resolveStructureDefinition();
        ArrayList<TagDefinition> tagDefinitions = new ArrayList<>();
        tagDefinitions.add(td0);
        tagDefinitions.add(td1);
        tagDefinitions.add(td2);
        tagDefinitions.add(td3);
        structureDefinition0.setTagDefinitions(tagDefinitions);
        structureDefinition0.save();

        StructureDefinition structureDefinition1 = project1.resolveStructureDefinition();
        tagDefinitions = new ArrayList<>();
        tagDefinitions.add(td4);
        tagDefinitions.add(td5);
        tagDefinitions.add(td6);
        tagDefinitions.add(td7);
        tagDefinitions.add(td8);
        structureDefinition1.setTagDefinitions(tagDefinitions);
        structureDefinition1.save();

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

        tag4 = new TagLong(td4, 23);
        tag5 = new TagDate(td5, new Date(1672531200000L));
        tag6 = new TagDouble(td6, 40.0f);
        tag7 = new TagBool(td7, true);
        tag8 = new TagString(td8, "Test 2");

        md2 = new MetaData(fi2, project1);
        md2.addTag(tag4);
        md2.addTag(tag5);
        md2.addTag(tag6);
        md2.addTag(tag7);
        md2.addTag(tag8);
        assertTrue(md2.save());

        tag9 = new TagLong(td4, -35);
        tag10 = new TagDate(td5, new Date(1609459200000L));
        tag11 = new TagDouble(td6, -70.0f);
        tag12 = new TagBool(td7, false);
        tag13 = new TagString(td8, "Alternative 7");

        md3 = new MetaData(fi3, project1);
        md3.addTag(tag9);
        md3.addTag(tag10);
        md3.addTag(tag11);
        md3.addTag(tag12);
        md3.addTag(tag13);
        assertTrue(md3.save());

    }

    @Test
    void query() {

        MetaDataQueryBuilder qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 0"));
        List<MetaData> result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td1, "Test String 0"));
        result = qb.query(0,0);
        assertEquals(0, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 0"));
        qb.addQueryElement(new Equals(td1, "Test String 1"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td0, "Test String 1"));
        qb.addQueryElement(new Equals(td1, "Test String 1"));
        result = qb.query(0,0);
        assertEquals(0, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(0, result.size());


        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td3, 0));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Equals(td3, 1));
        result = qb.query(0,0);
        assertEquals(0, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new QueryInternal(td2, new Equals(tdl0, 5), QueryInternalProto.MatchCondition.MATCH_ONE));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new QueryInternal(td2, new NotEquals(tdl0, 10), QueryInternalProto.MatchCondition.MATCH_ONE));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new QueryInternal(td2, new NotEquals(tdl0, 5), QueryInternalProto.MatchCondition.MATCH_ALL));
        result = qb.query(0, 0);
        assertEquals(0, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Between(td3, -1, 1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Between(td3, 5, 10));
        result = qb.query(0,0);
        assertEquals(0, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(0, result.size());

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Greater(td3, -1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Less(td3, 1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new Contains(td0, "Test"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new StartsWith(td1, "Test"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new EndsWith(td1, "1"));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new IsNull(td2));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md1, result.get(0));

        qb = new MetaDataQueryBuilder(project0);
        qb.addQueryElement(new NotNull(td1));
        result = qb.query(0,0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md0, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td4, 0, 100));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td4, -100, 0));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td5, new Date(1640995200000L), new Date(1704067200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td5, new Date(1577836800000L), new Date(1640995200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td6, 0.0, 100.0));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Between(td6, -100.0, 0.0));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Contains(td8, "Test"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Contains(td8, "Alternative"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new EndsWith(td8, " 2"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new EndsWith(td8, " 7"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td4, 23));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td4, -35));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td5, new Date(1672531200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td5, new Date(1609459200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td6, 40.0f, 0.001f));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td6, -70.0f, 0.001f));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td7, true));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td7, false));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td8, "Test 2"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Equals(td8, "Alternative 7"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td4, 20));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td4, -40));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td5, new Date(1640995200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td5, new Date(1577836800000L)));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td6, 30.0f));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Greater(td6, -80.0f));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td4, 30));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td4, -30));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td5, new Date(1704067200000L)));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td5, new Date(1640995200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td6, 50.0f));
        result = qb.query(0, 0);
        assertEquals(2, result.size());
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(2, result.size());

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new Less(td6, -60.0f));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td4, 23));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td4, -35));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td5, new Date(1672531200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td5, new Date(1609459200000L)));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td7, true));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td7, false));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td8, "Test 2"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new NotEquals(td8, "Alternative 7"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new StartsWith(td8, "Test"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md2, result.get(0));

        qb = new MetaDataQueryBuilder(project1);
        qb.addQueryElement(new StartsWith(td8, "Alternative"));
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
        qb = new MetaDataQueryBuilder(qb.getAsProto());
        result = qb.query(0, 0);
        assertEquals(1, result.size());
        assertEquals(md3, result.get(0));
    }
}