package de.sg_o.lib.tagy.def;

import com.couchbase.lite.Array;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import de.sg_o.lib.tagy.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagDefinition implements Serializable {
    @NotNull
    private final String key;
    private String name = null;
    private String description = null;
    @NotNull
    private final Type type;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private boolean required = false;
    @NotNull
    private final ArrayList<String> enumerators;
    private TagDefinition internal = null;
    @NotNull
    private Parameter parameter = Parameter.NONE;

    public TagDefinition(@Nullable String key, @NotNull Type type) {
        if (key == null) key = "";
        key = Util.sanitize(key, new char[]{'_', '-'}, false, true, 64);
        this.key = key;
        this.type = type;
        enumerators = new ArrayList<>();
    }

    public TagDefinition(@NotNull Dictionary encoded) {
        String key = encoded.getString(StructureConstants.KEY_KEY);
        key = Util.sanitize(key, new char[]{'_', '-'}, false, true, 64);
        if (key == null || (!encoded.contains(StructureConstants.TYPE_KEY))) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        Type type = Type.getType(encoded.getInt(StructureConstants.TYPE_KEY));
        String typeString = encoded.getString(StructureConstants.TYPE_KEY);
        if (typeString != null) {
            type = Type.valueOf(typeString);
        }
        if (type == null) {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }

        this.key = key;
        this.type = type;
        this.enumerators = new ArrayList<>();
        if (encoded.contains(StructureConstants.NAME_KEY)) {
            this.name = encoded.getString(StructureConstants.NAME_KEY);
        }
        if (encoded.contains(StructureConstants.DESCRIPTION_KEY)) {
            this.description = encoded.getString(StructureConstants.DESCRIPTION_KEY);
        }

        if (encoded.contains(StructureConstants.MIN_KEY)) {
            this.min = encoded.getDouble(StructureConstants.MIN_KEY);
        }
        if (encoded.contains(StructureConstants.MAX_KEY)) {
            this.max = encoded.getDouble(StructureConstants.MAX_KEY);
        }

        if (encoded.contains(StructureConstants.REQUIRED_KEY)) {
            this.required = encoded.getBoolean(StructureConstants.REQUIRED_KEY);
        }

        if (encoded.contains(StructureConstants.ENUMERATORS_KEY)) {
            Array enumerators = encoded.getArray(StructureConstants.ENUMERATORS_KEY);
            if (enumerators != null) {
                for (int i = 0; i < enumerators.count(); i++) {
                    String enumerator = enumerators.getString(i);
                    if (enumerator != null) {
                        this.enumerators.add(enumerator);
                    }
                }
            }
        }
        if (encoded.contains(StructureConstants.PARAMETER_KEY)) {
            Parameter parameter = Parameter.getParameter(encoded.getInt(StructureConstants.PARAMETER_KEY));
            String parameterString = encoded.getString(StructureConstants.PARAMETER_KEY);
            if (parameterString != null) {
                parameter = Parameter.valueOf(parameterString);
            }
            if (parameter == null) parameter = Parameter.NONE;
            this.parameter = parameter;
        }

        if (this.type == Type.LIST) {
            Dictionary internal = encoded.getDictionary(StructureConstants.INTERNAL_KEY);
            if (internal != null) {
                this.internal = new TagDefinition(internal);
            } else {
                throw new IllegalArgumentException("Invalid encoded TagDefinition");
            }
        }
    }

    public @NotNull String getKey() {
        return key;
    }

    public @NotNull String getName() {
        if (name == null || name.isEmpty()) {
            return key;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull Type getType() {
        return type;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

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

    public ArrayList<String> getEnumerators() {
        if (type != Type.ENUM) return null;
        return new ArrayList<>(enumerators);
    }

    public TagDefinition getInternal() {
        if (type != Type.LIST) return null;
        return internal;
    }

    public void setInternal(TagDefinition internal) {
        if (type != Type.LIST) return;
        this.internal = internal;
    }

    public @NotNull Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        if (parameter == null) parameter = Parameter.NONE;
        this.parameter = parameter;
    }

    public MutableDictionary getEncoded() {
        MutableDictionary encoded = new MutableDictionary();
        encoded.setString(StructureConstants.KEY_KEY, key);
        encoded.setInt(StructureConstants.TYPE_KEY, type.getId());
        if (name != null) {
            encoded.setString(StructureConstants.NAME_KEY, name);
        }
        if (description != null) {
            encoded.setString(StructureConstants.DESCRIPTION_KEY, description);
        }
        if (min != Double.NEGATIVE_INFINITY && min != Double.POSITIVE_INFINITY) {
            encoded.setDouble(StructureConstants.MIN_KEY, min);
        }
        if (min != Double.NEGATIVE_INFINITY && min != Double.POSITIVE_INFINITY) {
            encoded.setDouble(StructureConstants.MAX_KEY, max);
        }
        encoded.setBoolean(StructureConstants.REQUIRED_KEY, required);
        if (type == Type.ENUM) {
            MutableArray enumerators = new MutableArray();
            for (String enumerator : this.enumerators) {
                enumerators.addString(enumerator);
            }
            encoded.setArray(StructureConstants.ENUMERATORS_KEY, enumerators);
        }
        if (type == Type.LIST) {
            if (internal == null) return null;
            encoded.setDictionary(StructureConstants.INTERNAL_KEY, internal.getEncoded());
        }
        if (parameter != Parameter.NONE) {
            encoded.setInt(StructureConstants.PARAMETER_KEY, parameter.getId());
        }
        return encoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDefinition that = (TagDefinition) o;
        return Double.compare(that.getMin(),
                getMin()) == 0 && Double.compare(that.getMax(),
                getMax()) == 0 && isRequired() == that.isRequired() && Objects.equals(getKey(),
                that.getKey()) && Objects.equals(getName(),
                that.getName()) && Objects.equals(getDescription(),
                that.getDescription()) && getType().getId() == that.getType().getId() && Objects.equals(getEnumerators(),
                that.getEnumerators()) && Objects.equals(getInternal(),
                that.getInternal()) && getParameter().getId() == that.getParameter().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getName(), getDescription(), getType().getId(), getMin(), getMax(), isRequired(), getEnumerators(), getInternal(), getParameter().getId());
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
        if (internal != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.INTERNAL_KEY, "\n" + internal.toString(indent + 1), false, indent));
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
