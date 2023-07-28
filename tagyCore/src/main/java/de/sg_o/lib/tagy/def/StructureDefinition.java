package de.sg_o.lib.tagy.def;

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DbConstants;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StructureDefinition implements Serializable {
    @NotNull
    private final ArrayList<TagDefinition> tags = new ArrayList<>();
    private transient final Project project;

    public StructureDefinition(@NotNull Project project) {
        this.project = project;
        loadConfig();
    }

    public StructureDefinition() {
        this.project = null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean loadConfig() {
        if (project == null) return false;
        Document document = project.getData(DbConstants.CONFIG_COLLECTION_NAME, DbConstants.STRUCTURE_DEFINITION_DOCUMENT_NAME);
        if (document == null) return false;
        Array tags = document.getArray(DbConstants.TAGS_KEY);
        if (tags == null) return false;
        this.tags.clear();
        parseTagArray(tags);
        return true;
    }

    private void parseTagArray(@NotNull Array tags) {
        for (int i = 0; i < tags.count(); i++) {
            Dictionary tag = tags.getDictionary(i);
            if (tag == null) {
                continue;
            }
            try {
                this.tags.add(new TagDefinition(tag));
            } catch (Exception ignore) {
            }
        }
    }

    public void setTags(List<TagDefinition> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void setTags(Array tags) {
        this.tags.clear();
        parseTagArray(tags);
    }

    public boolean setTags(String json) {
        try {
            MutableArray tags = new MutableArray(json);
            setTags(tags);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ArrayList<TagDefinition> getTags() {
        return new ArrayList<>(tags);
    }

    public @NotNull MutableArray getEncoded() {
        MutableArray tags = new MutableArray();
        for (TagDefinition tag : this.tags) {
            tags.addDictionary(tag.getEncoded());
        }
        return tags;
    }

    public boolean saveConfig() {
        if (project == null) return false;
        MutableDocument document = new MutableDocument(DbConstants.STRUCTURE_DEFINITION_DOCUMENT_NAME);
        document.setArray(DbConstants.TAGS_KEY, getEncoded());
        return project.saveData(DbConstants.CONFIG_COLLECTION_NAME, document);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < tags.size(); i++) {
            TagDefinition tag = tags.get(i);
            builder.append("\n");
            builder.append(tag.toString(1));
            if (i < tags.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n]");
        return builder.toString();
    }
}
