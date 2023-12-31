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

package de.sg_o.app.tagy.annotator;

import de.sg_o.app.tagy.annotator.inputs.EnumInput;
import de.sg_o.app.tagy.annotator.inputs.ListInput;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.TagEnablerDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.sg_o.app.tagy.annotator.Input.create;

public class InputHolder {
    private final ArrayList<Input> inputs;
    private MetaData loaded;

    public InputHolder(Project project) {
        StructureDefinition structureDefinition = project.resolveStructureDefinition();
        HashMap<String, EnumInput> enumMap = new HashMap<>();
        ArrayList<Input> inputs = new ArrayList<>();
        for (TagDefinition tagDefinition : structureDefinition.getTagDefinitions()) {
            Input input = create(tagDefinition);
            if (input != null) {
                inputs.add(input);
                if (input instanceof EnumInput) {
                    enumMap.put(tagDefinition.getKey(), (EnumInput) input);
                }
            }
        }
        attachInputEnablers(enumMap, inputs);
        for (Input input : inputs) {
            if (input instanceof ListInput) {
                ((ListInput) input).setOtherInputs(inputs);
            }
        }
        this.inputs = inputs;
    }

    public ArrayList<Input> getInputs() {
        return inputs;
    }

    public void reset() {
        for (Input input : inputs) {
            input.reset(null);
        }
        this.loaded = null;
    }

    public void setData(@NotNull MetaData metaData) {
        this.loaded = metaData;
        List<TagContainer> tags = metaData.getTagContainers();
        HashMap<String, Tag> tagsMap = new HashMap<>();
        for (TagContainer tagContainer : tags) {
            Tag t = tagContainer.getTag();
            tagsMap.put(t.getKey(), t);
        }
        for (Input input : inputs) {
            Tag tag = null;
            if (tagsMap.containsKey(input.getTagDefinition().getKey())) {
                tag = tagsMap.get(input.getTagDefinition().getKey());
            }
            input.reset(tag);
        }
    }

    public MetaData getData() throws InputException {
        if (loaded == null) {
            return null;
        }
        loaded.clearTags();
        for (Input input : inputs) {
            Tag tag = input.getTag();
            if (tag == null) continue;
            loaded.addTag(tag);
        }
        return loaded;
    }

    private static void attachInputEnablers(@NotNull HashMap<String, EnumInput> enumMap, @NotNull ArrayList<Input> inputs) {
        for (Input input : inputs) {
            TagEnablerDefinition ted = input.getTagDefinition().resolveTagEnabler();
            if (ted == null) continue;
            input.removeViewer(input);
            EnumInput enabler = enumMap.get(ted.getSelectorKey());
            if (enabler == null) continue;
            int index = getEnumIndex(ted, enabler.getTagDefinition());
            if (index > -1) enabler.attachInputToEnable(input, index);
        }
    }

    public static int getEnumIndex(TagEnablerDefinition ted, TagDefinition enabler) {
        if (ted == null) return -1;
        if (enabler == null) return -1;
        if (ted.getEnumIndex() > -1 && ted.getEnumIndex() < enabler.getEnumerators().size()) {
            return ted.getEnumIndex();
        }
        String enumString = ted.getEnumString();
        if (enumString == null) return -1;
        return enabler.getEnumerators().indexOf(enumString);
    }
}
