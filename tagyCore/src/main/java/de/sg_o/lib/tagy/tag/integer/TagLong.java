package de.sg_o.lib.tagy.tag.integer;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TagLong extends Tag {
    private final long value;
    public TagLong(@NotNull TagDefinition definition, long value) {
        super(definition);
        if (definition.getType() != Type.LONG) throw new IllegalArgumentException("Definition is not of type long");
        this.value = value;
    }

    public TagLong(@NotNull TagDefinition definition, @NotNull Dictionary document) {
        super(definition);
        if (definition.getType() != Type.LONG) throw new IllegalArgumentException("Definition is not of type long");
        if (!document.contains(super.getKey())) throw new IllegalArgumentException("Document does not contain key");
        this.value = document.getLong(super.getKey());
    }

    public long getValue() {
        long value = this.value;
        if (value < super.getDefinition().getMin()) value = Math.round(super.getDefinition().getMin());
        if (value > super.getDefinition().getMax()) value = Math.round(super.getDefinition().getMax());
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(value);
    }

    @Override
    public void addToDictionary(@NotNull MutableDictionary dictionary) {
        dictionary.setLong(super.getKey(), value);
    }

    @Override
    public void addToArray(@NotNull MutableArray array) {
        array.addLong(value);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new LongInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagLong tagLong = (TagLong) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return getValue() == tagLong.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": " + value;
    }
}
