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
import de.sg_o.lib.tagy.util.Util;
import io.objectbox.BoxStore;
import io.objectbox.annotation.*;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class TagDefinition implements Serializable {
    @Id
    private Long id;
    @NotNull
    private final String key;
    private String name;
    private String description;
    @Convert(converter = Type.TypeConverter.class, dbType = Integer.class)
    private final Type type;
    private double min = Double.NEGATIVE_INFINITY;
    private double max= Double.POSITIVE_INFINITY;;
    private boolean required = false;
    private final List<String> enumerators;
    @Convert(converter = Parameter.ParameterConverter.class, dbType = Integer.class)
    private Parameter parameter = Parameter.NONE;
    private final ToOne<TagDefinition> internal = new ToOne<>(this, TagDefinition_.internal);

    @Transient
    transient BoxStore __boxStore = null;

    public TagDefinition(Long id, @NotNull String key, String name, String description, Type type, double min, double max, boolean required, List<String> enumerators, Parameter parameter, long internalId) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
        this.enumerators = enumerators;
        this.internal.setTargetId(internalId);
        this.parameter = parameter;
    }

    public TagDefinition(@Nullable String key, @NotNull Type type) {
        if (key == null) key = "";
        key = Util.sanitize(key, new char[]{'_', '-'}, false, true, 64);
        this.key = key;
        this.type = type;
        enumerators = new ArrayList<>();
    }

    public TagDefinition(@NotNull JsonNode encoded) {

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
        Type type = Type.getType(typeNode.intValue());
        String typeString = typeNode.textValue();
        if (typeString != null) {
            type = Type.valueOf(typeString);
        }
        if (type == null) {
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
            Parameter parameter = Parameter.getParameter(parameterNode.intValue());
            String parameterString = parameterNode.textValue();
            if (parameterString != null) {
                parameter = Parameter.valueOf(parameterString);
            }
            if (parameter == null) parameter = Parameter.NONE;
            this.parameter = parameter;
        }

        if (this.type == Type.LIST) {
            JsonNode internalNode = encoded.get(StructureConstants.INTERNAL_KEY);
            if (internalNode != null) {
                this.internal.setTarget(new TagDefinition(internalNode));
            } else {
                throw new IllegalArgumentException("Invalid encoded TagDefinition");
            }
        }
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
    public @NotNull Type getType() {
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
        if (type !=  Type.ENUM) return false;
        return enumerators.add(enumerator);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addAllEnumerators(List<String> enumerator) {
        if (type != Type.ENUM) return false;
        return enumerators.addAll(enumerator);
    }

    public boolean removeEnumerator(String enumerator) {
        if (type != Type.ENUM) return false;
        return enumerators.remove(enumerator);
    }

    public boolean removeEnumerator(int index) {
        if (type != Type.ENUM) return false;
        if (index < 0 || index >= enumerators.size()) return false;
        return enumerators.remove(index) != null;
    }

    @SuppressWarnings("unused")
    public boolean clearEnumerators() {
        if (type != Type.ENUM) return false;
        enumerators.clear();
        return true;
    }

    @JsonProperty(index = 7)
    public ArrayList<String> getEnumerators() {
        if (type != Type.ENUM) return null;
        return new ArrayList<>(enumerators);
    }

    @JsonProperty(index = 8)
    public TagDefinition resolveInternal() {
        if (type != Type.LIST) return null;
        return internal.getTarget();
    }

    public ToOne<TagDefinition> getInternal() {
        if (type != Type.LIST) {
            internal.setTarget(null);
        }
        return internal;
    }

    public void setInternal(TagDefinition internal) {
        if (type != Type.LIST) return;
        this.internal.setTarget(internal);
    }

    @JsonProperty(index = 9)
    public @NotNull Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        if (parameter == null) parameter = Parameter.NONE;
        this.parameter = parameter;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDefinition that = (TagDefinition) o;
        return Double.compare(that.getMin(),
                getMin()) == 0 && Double.compare(that.getMax(),
                getMax()) == 0 && isRequired() == that.isRequired() && Objects.equals(getKey(),
                that.getKey()) && Objects.equals(resolveName(),
                that.resolveName()) && Objects.equals(getDescription(),
                that.getDescription()) && getType().getId() == that.getType().getId() && Objects.equals(getEnumerators(),
                that.getEnumerators()) && Objects.equals(resolveInternal(),
                that.resolveInternal()) && getParameter().getId() == that.getParameter().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), resolveName(), getDescription(), getType().getId(), getMin(), getMax(), isRequired(), getEnumerators(), resolveInternal(), getParameter().getId());
    }

    private static String generateEntity(String key, Object value, boolean encapsulate, int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = -1; i < indent; i++) {
            builder.append('\t');
        }
        builder.append("\"");
        builder.append(key);
        builder.append("\": ");
        if(encapsulate) builder.append("\"");
        builder.append(value);
        if(encapsulate) builder.append("\"");
        return builder.toString();
    }

    public String toString(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append('\t');
        }
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
        if (internal.getTarget() != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.INTERNAL_KEY, "\n" + internal.getTarget().toString(indent + 1), false, indent));
        }
        if (parameter != Parameter.NONE) {
            builder.append(",\n").append(generateEntity(StructureConstants.PARAMETER_KEY, parameter, true, indent));
        }
        builder.append("\n");
        for (int i = 0; i < indent; i++) {
            builder.append('\t');
        }
        builder.append("}");
        return builder.toString();
    }

    @NotNull
    private StringBuilder enumeratorToString(int indent) {
        StringBuilder enumeratorsString = new StringBuilder();
        if (enumerators == null) {
            return enumeratorsString.append("[]");
        }
        if (!enumerators.isEmpty()) {
            enumeratorsString.append("\n");
            for (int i = -2; i < indent; i++) {
                enumeratorsString.append('\t');
            }
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
            for (int i = -2; i < indent; i++) {
                enumeratorsString.append('\t');
            }
        }
        enumeratorsString.append("]");
        return enumeratorsString;
    }

    public String toString() {
        return toString(0);
    }
}
