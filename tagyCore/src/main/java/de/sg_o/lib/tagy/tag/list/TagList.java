package de.sg_o.lib.tagy.tag.list;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class TagList extends Tag {
    private final ArrayList<Tag> values;

    public TagList(@NotNull TagDefinition definition) {
        this(definition, 32);
    }

    public TagList(@NotNull TagDefinition definition, int initialCapacity) {
        super(definition);
        if (definition.getType() != Type.LIST) throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        if (initialCapacity < 0) throw new IllegalArgumentException("Initial capacity is negative");
        values = new ArrayList<>(initialCapacity);
    }

    public TagList(@NotNull TagDefinition definition, @NotNull Dictionary document) {
        this(definition, document.getArray(definition.getKey()));
    }

    public TagList(@NotNull TagDefinition definition, Array array) {
        super(definition);
        if (definition.getType() != Type.LIST) throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        if (array != null) {
            this.values = new ArrayList<>(array.count());
            for (int i = 0; i < array.count(); i++) {
                values.add(Tag.create(definition.getInternal(), array, i));
            }
        } else {
            this.values = new ArrayList<>();
        }

    }

    public boolean addValue(Tag value) {
        if (value == null) return false;
        if (value.getDefinition().getType() != super.getDefinition().getInternal().getType()) return false;
        return values.add(value);
    }

    public Tag removeValue(int index) {
        if (index < 0 || index >= values.size()) return null;
        return values.remove(index);
    }

    public boolean removeValue(Tag value) {
        return values.remove(value);
    }

    public ArrayList<Tag> getValues() {
        return new ArrayList<>(values);
    }

    @Override
    public String getValueAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < values.size(); i++) {
            Tag value = values.get(i);
            builder.append(value.getValueAsString());
            if (i < values.size() - 1) builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void addToDictionary(@NotNull MutableDictionary dictionary) {
        MutableArray internal = new MutableArray();
        for (Tag value : values) {
            value.addToArray(internal);
        }
        dictionary.setArray(super.getKey(), internal);
    }

    @Override
    public void addToArray(@NotNull MutableArray array) {
        MutableArray internal = new MutableArray();
        for (Tag value : values) {
            value.addToArray(internal);
        }
        array.addArray(internal);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new ListInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagList tagList = (TagList) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;

        for (int i = 0; i < values.size(); i++) {
            if (!Objects.equals(values.get(i).getValueAsString(), tagList.values.get(i).getValueAsString())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValues());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() +
                "\": " +
                getValueAsString();
    }
}
