package de.sg_o.lib.tagy.tag.enumerator;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class TagEnum extends Tag {
    private static final String UNRECOGNIZED = "UNRECOGNIZED";

    private final int value;

    public TagEnum(@NotNull TagDefinition definition, int value) {
        super(definition);
        if (definition.getType() != Type.ENUM) throw new IllegalArgumentException("Definition is not of type enum");
        if (value < 0) value = -1;
        this.value = value;
    }

    public TagEnum(@NotNull TagDefinition definition, @NotNull Dictionary document) {
        super(definition);
        if (definition.getType() != Type.ENUM) throw new IllegalArgumentException("Definition is not of type enum");
        if (!document.contains(super.getKey())) throw new IllegalArgumentException("Document does not contain key");
        this.value = document.getInt(super.getKey());
    }

    public int getValue() {
        return value;
    }

    public List<String> getEnumerators() {
        return super.getDefinition().getEnumerators();
    }

    @Override
    public String getValueAsString() {
        if (value < 0) return UNRECOGNIZED;
        List<String> enumerators = super.getDefinition().getEnumerators();
        if (value >= enumerators.size()) return UNRECOGNIZED;
        return enumerators.get(value);
    }

    @Override
    public void addToDictionary(@NotNull MutableDictionary dictionary) {
        dictionary.setInt(super.getKey(), value);
    }

    @Override
    public void addToArray(@NotNull MutableArray array) {
        array.addInt(value);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new EnumInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagEnum tagEnum = (TagEnum) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return getValue() == tagEnum.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": \"" + getValueAsString() + "\"";
    }
}
