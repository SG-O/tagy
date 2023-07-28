package de.sg_o.lib.tagy.tag.string;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TagString extends Tag {
    @NotNull
    private final String value;

    public TagString(@NotNull TagDefinition definition, @NotNull String value) {
        super(definition);
        if (definition.getType() != Type.STRING) throw new IllegalArgumentException("Definition is not of type string");
        this.value = value;
    }

    public TagString(@NotNull TagDefinition definition, @NotNull Dictionary document) {
        super(definition);
        if (definition.getType() != Type.STRING) throw new IllegalArgumentException("Definition is not of type string");
        if (!document.contains(super.getKey())) throw new IllegalArgumentException("Document does not contain key");
        String value = document.getString(super.getKey());
        if (value == null) throw new IllegalArgumentException("Value is null");
        this.value = value;
    }

    public @NotNull String getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }

    @Override
    public void addToDictionary(@NotNull MutableDictionary dictionary) {
        dictionary.setString(super.getKey(), value);
    }

    @Override
    public void addToArray(@NotNull MutableArray array) {
        array.addString(value);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new StringInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagString tagString = (TagString) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return getValue().equals(tagString.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": \"" + value + "\"";
    }
}
