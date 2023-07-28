package de.sg_o.lib.tagy.tag.floating;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TagDouble extends Tag {
    private final double value;
    public TagDouble(@NotNull TagDefinition definition, double value) {
        super(definition);
        if (definition.getType() != Type.DOUBLE) throw new IllegalArgumentException("Definition is not of type double");
        this.value = value;
    }

    public TagDouble(@NotNull TagDefinition definition, @NotNull Dictionary document) {
        super(definition);
        if (definition.getType() != Type.DOUBLE) throw new IllegalArgumentException("Definition is not of type double");
        if (!document.contains(super.getKey())) throw new IllegalArgumentException("Document does not contain key");
        this.value = document.getDouble(super.getKey());
    }

    public double getValue() {
        double value = this.value;
        if (value < super.getDefinition().getMin()) value = super.getDefinition().getMin();
        if (value > super.getDefinition().getMax()) value = super.getDefinition().getMax();
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void addToDictionary(@NotNull MutableDictionary dictionary) {
        dictionary.setDouble(super.getKey(), value);
    }

    @Override
    public void addToArray(@NotNull MutableArray array) {
        array.addDouble(value);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new DoubleInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDouble tagDouble = (TagDouble) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return Double.compare(tagDouble.getValue(), getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": " + getValue();
    }
}
