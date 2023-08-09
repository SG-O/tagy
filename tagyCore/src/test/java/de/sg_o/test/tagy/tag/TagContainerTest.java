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
import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TagContainerTest {

    @Test
    void getTag() {
        TagDefinition td0 = new TagDefinition("td0", TagDefinitionProto.Type.BOOLEAN);
        TagDefinition td1 = new TagDefinition("td1", TagDefinitionProto.Type.DATE);
        TagDefinition td2 = new TagDefinition("td2", TagDefinitionProto.Type.ENUM);
        td2.addEnumerator("e1");
        td2.addEnumerator("e2");
        TagDefinition td3 = new TagDefinition("td3", TagDefinitionProto.Type.DOUBLE);
        TagDefinition td4 = new TagDefinition("td4", TagDefinitionProto.Type.LONG);
        TagDefinition td5 = new TagDefinition("td5", TagDefinitionProto.Type.LIST);
        TagDefinition td51 = new TagDefinition("td5.1", TagDefinitionProto.Type.STRING);
        td5.setInternal(td51);
        TagDefinition td6 = new TagDefinition("td6", TagDefinitionProto.Type.STRING);


        TagBool tag0 = new TagBool(td0, true);
        TagDate tag1 = new TagDate(td1, new Date());
        TagEnum tag2 = new TagEnum(td2, 0);
        TagDouble tag3 = new TagDouble(td3, 1.0);
        TagLong tag4 = new TagLong(td4, 2);
        TagList tag5 = new TagList(td5);
        tag5.addValue(new TagString(td51, "s1"));
        tag5.addValue(new TagString(td51, "s2"));
        TagString tag6 = new TagString(td6, "s");


        TagContainer tc0 = new TagContainer(tag0);
        TagContainer tc1 = new TagContainer(tag1);
        TagContainer tc2 = new TagContainer(tag2);
        TagContainer tc3 = new TagContainer(tag3);
        TagContainer tc4 = new TagContainer(tag4);
        TagContainer tc5 = new TagContainer(tag5);
        TagContainer tc6 = new TagContainer(tag6);

        assertEquals(tag0, tc0.getTag());
        assertEquals(tag1, tc1.getTag());
        assertEquals(tag2, tc2.getTag());
        assertEquals(tag3, tc3.getTag());
        assertEquals(tag4, tc4.getTag());
        assertEquals(tag5, tc5.getTag());
        assertEquals(tag6, tc6.getTag());
    }
}