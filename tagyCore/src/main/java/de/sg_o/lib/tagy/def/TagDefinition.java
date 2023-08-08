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

package de.sg_o.lib.tagy.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.util.JsonPrintable;
import de.sg_o.lib.tagy.util.Util;
import de.sg_o.proto.tagy.TagDefinitionProto;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class TagDefinition extends JsonPrintable implements Serializable {
    @Id
    private Long id;
    @NotNull
    private final String key;
    private String name;
    private String description;
    @Convert(converter = TypeConverter.class, dbType = Integer.class)
    private final TagDefinitionProto.Type type;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private boolean required = false;
    @NotNull
    private final List<String> enumerators;
    @Convert(converter = ParameterConverter.class, dbType = Integer.class)
    private TagDefinitionProto.Parameter parameter = TagDefinitionProto.Parameter.NONE;
    private final ToOne<TagDefinition> internal = new ToOne<>(this, TagDefinition_.internal);
    private final ToOne<TagEnablerDefinition> tagEnabler = new ToOne<>(this, TagDefinition_.tagEnabler);

    @Transient
    transient BoxStore __boxStore = null;
    @Transient
    private static final TypeConverter typeConverter = new TypeConverter();
    @Transient
    private static final ParameterConverter parameterConverter = new ParameterConverter();

    @SuppressWarnings("NullableProblems")
    public TagDefinition(Long id, @NotNull String key, String name, String description,
                         TagDefinitionProto.Type type, double min, double max, boolean required, List<String> enumerators,
                         TagDefinitionProto.Parameter parameter, long internalId, long tagEnablerId) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
        if (enumerators == null) enumerators = new ArrayList<>();
        this.enumerators = enumerators;
        this.parameter = parameter;
        this.internal.setTargetId(internalId);
        this.tagEnabler.setTargetId(tagEnablerId);
    }

    public TagDefinition(@Nullable String key, @NotNull TagDefinitionProto.Type type) {
        if (key == null) key = "";
        key = Util.sanitize(key, new char[]{'_', '-'}, false, true, 64);
        this.key = key;
        this.type = type;
        enumerators = new ArrayList<>();
    }

    public TagDefinition(@NotNull JsonNode encoded) {
        if (!encoded.isObject()){
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        JsonNode keyNode = encoded.get(StructureConstants.KEY_KEY);
        if (keyNode == null) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        String key = Util.sanitize(keyNode.textValue(), new char[]{'_', '-'}, false, true, 64);
        if (key == null) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        JsonNode typeNode = encoded.get(StructureConstants.TYPE_KEY);
        if (typeNode == null) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }

        TagDefinitionProto.Type type = typeConverter.convertToEntityProperty(typeNode.intValue());
        String typeString = typeNode.textValue();
        if (typeString != null) {
            type = TagDefinitionProto.Type.valueOf(typeString);
        }
        if (type == TagDefinitionProto.Type.UNKNOWN){
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }

        this.key = key;
        this.type = type;
        this.enumerators = new ArrayList<>();
        JsonNode nameNode = encoded.get(StructureConstants.NAME_KEY);
        if (nameNode != null) {
            this.name = nameNode.textValue();
        }
        JsonNode descriptionNode = encoded.get(StructureConstants.DESCRIPTION_KEY);
        if (descriptionNode != null) {
            this.description = descriptionNode.textValue();
        }
        JsonNode minNode = encoded.get(StructureConstants.MIN_KEY);
        if (minNode != null) {
            this.min = minNode.doubleValue();
        }
        JsonNode maxNode = encoded.get(StructureConstants.MAX_KEY);
        if (maxNode != null) {
            this.max = maxNode.doubleValue();
        }

        JsonNode requiredNode = encoded.get(StructureConstants.REQUIRED_KEY);
        if (requiredNode != null) {
            this.required = requiredNode.booleanValue();
        }

        JsonNode enumeratorsNode = encoded.get(StructureConstants.ENUMERATORS_KEY);
        if (enumeratorsNode != null) {
            if (enumeratorsNode.isArray()) {
                for (int i = 0; i < enumeratorsNode.size(); i++) {
                    JsonNode enumeratorNode = enumeratorsNode.get(i);
                    if (enumeratorNode != null) {
                        String enumerator = enumeratorNode.textValue();
                        this.enumerators.add(enumerator);
                    }
                }
            }
        }

        JsonNode parameterNode = encoded.get(StructureConstants.PARAMETER_KEY);
        if (parameterNode != null) {
            TagDefinitionProto.Parameter parameter = parameterConverter.convertToEntityProperty(parameterNode.intValue());
            String parameterString = parameterNode.textValue();
            if (parameterString != null) {
                parameter = TagDefinitionProto.Parameter.valueOf(parameterString);
            }
            this.parameter = parameter;
        }

        if (this.type == TagDefinitionProto.Type.LIST) {
            JsonNode internalNode = encoded.get(StructureConstants.INTERNAL_KEY);
            if (internalNode != null) {
                this.internal.setTarget(new TagDefinition(internalNode));
            } else {
                throw new IllegalArgumentException("Invalid encoded TagDefinition");
            }
        }

        JsonNode tagEnablerNode = encoded.get(StructureConstants.TAG_ENABLER_KEY);
        if (tagEnablerNode != null) {
            this.tagEnabler.setTarget(new TagEnablerDefinition(tagEnablerNode));
        }
    }

    public TagDefinition(TagDefinitionProto.TagDefinition proto) {
        String key = Util.sanitize(proto.getKey(), new char[]{'_', '-'}, false, true, 64);
        if (key == null) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        this.key = key;
        this.type = proto.getType();
        if (this.type == TagDefinitionProto.Type.UNKNOWN){
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        if (proto.hasName()) this.name = proto.getName();
        if (proto.hasDescription()) this.description = proto.getDescription();
        if (proto.hasMin()) this.min = proto.getMin();
        if (proto.hasMax()) this.max = proto.getMax();
        this.required = proto.getRequired();
        this.enumerators = new ArrayList<>();
        this.enumerators.addAll(proto.getEnumeratorsList());
        if (proto.hasParameter()) this.parameter = proto.getParameter();
        if (this.type == TagDefinitionProto.Type.LIST) {
            if (proto.hasInternal()) {
                this.internal.setTarget(new TagDefinition(proto.getInternal()));
            } else {
                throw new IllegalArgumentException("Invalid encoded TagDefinition");
            }
        }
        if (proto.hasTagEnabler()) this.tagEnabler.setTarget(new TagEnablerDefinition(proto.getTagEnabler()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(index = 0)
    public @NotNull String getKey() {
        return key;
    }

    public @NotNull String resolveName() {
        if (name == null || name.isEmpty()) {
            return key;
        }
        return name;
    }

    @JsonProperty(index = 1)
    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(index = 2)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(index = 3)
    public @NotNull TagDefinitionProto.Type getType() {
        return type;
    }

    @JsonProperty(index = 4)
    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @JsonProperty(index = 5)
    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @JsonProperty(index = 6)
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean addEnumerator(String enumerator) {
        if (type !=  TagDefinitionProto.Type.ENUM) return false;
        return enumerators.add(enumerator);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addAllEnumerators(List<String> enumerator) {
        if (type != TagDefinitionProto.Type.ENUM) return false;
        return enumerators.addAll(enumerator);
    }

    public boolean removeEnumerator(String enumerator) {
        if (type != TagDefinitionProto.Type.ENUM) return false;
        return enumerators.remove(enumerator);
    }

    public boolean removeEnumerator(int index) {
        if (type != TagDefinitionProto.Type.ENUM) return false;
        if (index < 0 || index >= enumerators.size()) return false;
        return enumerators.remove(index) != null;
    }

    @SuppressWarnings("unused")
    public boolean clearEnumerators() {
        if (type != TagDefinitionProto.Type.ENUM) return false;
        enumerators.clear();
        return true;
    }

    @JsonProperty(index = 7)
    public ArrayList<String> getEnumerators() {
        if (type != TagDefinitionProto.Type.ENUM) return null;
        return new ArrayList<>(enumerators);
    }

    @JsonProperty(value = StructureConstants.INTERNAL_KEY, index = 8)
    public TagDefinition resolveInternal() {
        if (type != TagDefinitionProto.Type.LIST) return null;
        return internal.getTarget();
    }

    public ToOne<TagDefinition> getInternal() {
        if (type != TagDefinitionProto.Type.LIST) {
            internal.setTarget(null);
        }
        return internal;
    }

    public void setInternal(TagDefinition internal) {
        if (type != TagDefinitionProto.Type.LIST) return;
        this.internal.setTarget(internal);
    }

    @JsonProperty(index = 9)
    public @NotNull TagDefinitionProto.Parameter getParameter() {
        return parameter;
    }

    public void setParameter(TagDefinitionProto.Parameter parameter) {
        if (parameter == null) parameter = TagDefinitionProto.Parameter.NONE;
        this.parameter = parameter;
    }

    @JsonProperty(value = StructureConstants.TAG_ENABLER_KEY, index = 10)
    public TagEnablerDefinition resolveTagEnabler() {
        return tagEnabler.getTarget();
    }

    @SuppressWarnings("unused")
    public ToOne<TagEnablerDefinition> getTagEnabler() {
        return tagEnabler;
    }

    public void setTagEnabler(TagEnablerDefinition tagEnabler) {
        this.tagEnabler.setTarget(tagEnabler);
    }

    public StorageType getStorageType() {
        switch (this.type) {
            case BOOLEAN:
                return StorageType.BOOLEAN;
            case LIST:
                return StorageType.LIST;
            case DATE:
            case ENUM:
            case LONG:
                return StorageType.LONG;
            case DOUBLE:
                return StorageType.DOUBLE;
            case STRING:
            default:
                return StorageType.STRING;
        }
    }

    public TagDefinitionProto.TagDefinition getAsProto() {
        TagDefinitionProto.TagDefinition.Builder builder = TagDefinitionProto.TagDefinition.newBuilder();
        builder.setKey(this.key);
        builder.setType(this.type);
        if (this.name != null) builder.setName(this.name);
        if (this.description != null) builder.setDescription(this.description);
        if (this.min != Double.NEGATIVE_INFINITY && this.min != Double.POSITIVE_INFINITY) builder.setMin(this.min);
        if (this.max != Double.NEGATIVE_INFINITY && this.max != Double.POSITIVE_INFINITY) builder.setMax(this.max);
        builder.setRequired(this.required);
        builder.addAllEnumerators(this.enumerators);
        if (resolveInternal() != null) builder.setInternal(resolveInternal().getAsProto());
        if (this.parameter != TagDefinitionProto.Parameter.NONE) builder.setParameter(this.parameter);
        if (resolveTagEnabler() != null) builder.setTagEnabler(resolveTagEnabler().getAsProto());
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDefinition that = (TagDefinition) o;
        return Double.compare(that.getMin(), getMin()) == 0 &&
                Double.compare(that.getMax(), getMax()) == 0 &&
                isRequired() == that.isRequired() &&
                Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(resolveName(), that.resolveName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(typeConverter.convertToDatabaseValue(getType()), typeConverter.convertToDatabaseValue(that.getType())) &&
                Objects.equals(getEnumerators(), that.getEnumerators()) &&
                Objects.equals(resolveInternal(), that.resolveInternal()) &&
                Objects.equals(parameterConverter.convertToDatabaseValue(getParameter()), parameterConverter.convertToDatabaseValue(that.getParameter())) &&
                Objects.equals(resolveTagEnabler(), that.resolveTagEnabler());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), resolveName(), getDescription(),
                typeConverter.convertToDatabaseValue(getType()), getMin(),
                getMax(), isRequired(), getEnumerators(), resolveInternal(),
                parameterConverter.convertToDatabaseValue(getParameter()), resolveTagEnabler());
    }

    public String toString(int indent) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateIndent(indent));
        builder.append("{\n");
        builder.append(generateEntity(StructureConstants.KEY_KEY, key, true, indent)).append(",\n");
        builder.append(generateEntity(StructureConstants.TYPE_KEY, type, true, indent));
        if (name != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.NAME_KEY, name, true, indent));
        }
        if (description != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.DESCRIPTION_KEY, description, true, indent));
        }
        if (min != Double.NEGATIVE_INFINITY && min != Double.POSITIVE_INFINITY) {
            builder.append(",\n").append(generateEntity(StructureConstants.MIN_KEY, min, false, indent));
        }
        if (max != Double.NEGATIVE_INFINITY && max != Double.POSITIVE_INFINITY) {
            builder.append(",\n").append(generateEntity(StructureConstants.MAX_KEY, max, false, indent));
        }
        builder.append(",\n").append(generateEntity(StructureConstants.REQUIRED_KEY, required, false, indent));
        StringBuilder enumeratorsString = enumeratorToString(indent);
        builder.append(",\n").append(generateEntity(StructureConstants.ENUMERATORS_KEY, enumeratorsString, false, indent));
        if (resolveInternal() != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.INTERNAL_KEY, "\n" + resolveInternal().toString(indent + 2), false, indent));
        }
        if (parameter != TagDefinitionProto.Parameter.NONE) {
            builder.append(",\n").append(generateEntity(StructureConstants.PARAMETER_KEY, parameter, true, indent));
        }
        if (resolveTagEnabler() != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.TAG_ENABLER_KEY, "\n" + resolveTagEnabler().toString(indent + 2), false, indent));
        }
        builder.append("\n");
        builder.append(generateIndent(indent));
        builder.append("}");
        return builder.toString();
    }

    @NotNull
    private StringBuilder enumeratorToString(int indent) {
        StringBuilder enumeratorsString = new StringBuilder();
        if (!enumerators.isEmpty()) {
            enumeratorsString.append("\n");
            enumeratorsString.append(generateIndent(indent + 2));
        }
        enumeratorsString.append("[");
        for (int i = 0; i < enumerators.size(); i++) {
            String enumerator = enumerators.get(i);
            enumeratorsString.append("\n");
            for (int j = -3; j < indent; j++) {
                enumeratorsString.append('\t');
            }
            enumeratorsString.append("\"");
            enumeratorsString.append(enumerator);
            enumeratorsString.append("\"");
            if (i < enumerators.size() - 1) {
                enumeratorsString.append(",");
            }
        }
        if (!enumerators.isEmpty()) {
            enumeratorsString.append("\n");
            enumeratorsString.append(generateIndent(indent + 2));
        }
        enumeratorsString.append("]");
        return enumeratorsString;
    }
}
