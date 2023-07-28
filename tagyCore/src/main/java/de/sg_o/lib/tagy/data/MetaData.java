package de.sg_o.lib.tagy.data;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DbConstants;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.values.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class MetaData implements Serializable {
    @NotNull
    private final HashMap<String, Tag> tags = new HashMap<>();
    @NotNull
    private transient final Project project;
    @NotNull
    private transient final FileInfo reference;
    @NotNull
    private final LinkedList<User> editHistory = new LinkedList<>();

    private transient boolean updated = false;

    public MetaData(@NotNull FileInfo reference, @NotNull Project project) {
        this.reference = reference;
        this.project = project;
        if (!load()) {
            this.tags.clear();
            this.editHistory.clear();
            this.editHistory.add(project.getUser());
            this.updated = true;
        }
    }

    public boolean load () {
        this.tags.clear();
        this.editHistory.clear();
        Document document = project.getData(DbConstants.META_COLLECTION_NAME, reference.getId());
        if (document == null) return false;
        Dictionary tags = document.getDictionary(DbConstants.TAGS_KEY);
        if (tags == null) return false;
        for (TagDefinition tagDefinition : project.getStructureDefinition().getTags()) {
            try {
                Tag tag = Tag.create(tagDefinition, tags);
                if (tag != null) {
                    this.tags.put(tag.getKey(), tag);
                }
            } catch (Exception e) {
                if (tagDefinition.isRequired()) return false;
            }
        }
        Array editHistory = document.getArray(DbConstants.EDIT_HISTORY_KEY);
        if (editHistory == null) return false;
        for (int i = 0; i < editHistory.count(); i++) {
            Dictionary user = editHistory.getDictionary(i);
            if (user == null) continue;
            this.editHistory.add(new User(user));
        }
        return true;
    }

    public void addTag(@NotNull Tag tag) {
        tags.put(tag.getKey(), tag);
        if (!this.updated) {
            editHistory.add(project.getUser());
            this.updated = true;
        }
    }

    @SuppressWarnings("unused")
    @JsonProperty(value = "id", index = 0)
    public String getID() {
        return reference.getId();
    }

    @SuppressWarnings("unused")
    public void setTags(@Nullable List<Tag> tags) {
        this.tags.clear();
        if (tags == null) return;
        for (Tag tag : tags) {
            addTag(tag);
        }
    }

    public HashMap<String, Tag> getTags() {
        return new HashMap<>(this.tags);
    }

    public @NotNull FileInfo getReference() {
        return reference;
    }

    @SuppressWarnings("unused")
    public ArrayList<TagDefinition> getTagDefinitions() {
        return this.project.getStructureDefinition().getTags();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean save() {
        MutableDocument document = new MutableDocument(reference.getId());
        MutableDictionary tags = new MutableDictionary();
        for (Tag tag : this.tags.values()) {
            tag.addToDictionary(tags);
        }
        document.setDictionary(DbConstants.TAGS_KEY, tags);
        MutableArray editHistory = new MutableArray();
        for (User user : this.editHistory) {
            editHistory.addDictionary(user.getEncoded());
        }
        document.setArray(DbConstants.EDIT_HISTORY_KEY, editHistory);
        reference.setAnnotated(true);
        if (!reference.save(project)) return false;
        return project.saveData(DbConstants.META_COLLECTION_NAME, document);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    @SuppressWarnings("unused")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        throw new IOException("Deserialization not supported");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaData metaData = (MetaData) o;

        if (getTags() != null ? !getTags().equals(metaData.getTags()) : metaData.getTags() != null) return false;
        if (!Objects.equals(project, metaData.project)) return false;
        return getReference().equals(metaData.getReference());
    }

    @Override
    public int hashCode() {
        int result = getTags() != null ? getTags().hashCode() : 0;
        result = 31 * result + project.hashCode();
        result = 31 * result + getReference().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\t\"_id\": \n");
        builder.append(reference.toString(2));
        builder.append(",\n");
        for (Tag tag : tags.values()) {
            builder.append("\t");
            builder.append(tag.toString());
            builder.append(",\n");
        }
        builder.append("\t\"_editHistory\": \n\t\t[\n");
        for (int i = 0; i < editHistory.size(); i++) {
            User user = editHistory.get(i);
            builder.append("\t\t\t");
            builder.append(user.toString());
            if (i < editHistory.size() - 1) builder.append(",");
            builder.append("\n");
        }
        builder.append("\t\t]\n");
        builder.append('}');
        return builder.toString();
    }
}
