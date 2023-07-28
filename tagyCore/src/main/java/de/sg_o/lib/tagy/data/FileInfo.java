package de.sg_o.lib.tagy.data;

import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DbConstants;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class FileInfo implements Serializable {
    @NotNull
    private final File file;
    private boolean annotated;

    public FileInfo(@NotNull File file) {
        this.file = file;
        if (file.isDirectory()) throw new RuntimeException("File is a directory");
        if (!file.exists()) throw new RuntimeException("File does not exist");
        if (!file.canRead()) throw new RuntimeException("File is not readable");
        annotated = false;
    }

    public FileInfo(@NotNull String fileId, @NotNull Project project) {
        Document document = project.getData(DbConstants.DATA_COLLECTION_NAME, fileId);
        if (document == null) throw new RuntimeException("File does not exist");
        String fileName = document.getString("file");
        if (fileName == null) throw new RuntimeException("Missing file name");
        this.file = new File(fileName);
        this.annotated = document.getBoolean("annotated");
        validateLoadedData();
    }

    private void validateLoadedData() {
        if (file.isDirectory()) throw new RuntimeException("File is a directory");
        if (!file.exists()) throw new RuntimeException("File does not exist");
        if (!file.canRead()) throw new RuntimeException("File is not readable");
    }

    public @NotNull File getFile() {
        return file;
    }
    public @NotNull String getId() {
        return file.getAbsolutePath();
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
    }

    public boolean isAnnotated() {
        return annotated;
    }

    public MutableDocument getEncoded() {
        String fullPath = file.getAbsolutePath();
        MutableDocument document = new MutableDocument(getId());
        document.setString("file", fullPath);
        document.setBoolean("annotated", annotated);
        return document;
    }

    public MetaData getMetaData(Project project) {
        return new MetaData(this, project);
    }

    public boolean save(Project project) {
        return project.saveData(DbConstants.DATA_COLLECTION_NAME, getEncoded());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return isAnnotated() == fileInfo.isAnnotated() && Objects.equals(getFile(), fileInfo.getFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), isAnnotated());
    }

    private String indent(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }

    public String toString(int indent) {
        return indent(indent) +
                "{\n" +
                indent(indent + 1) +
                "\"file\": \"" +
                file +
                "\",\n" +
                indent(indent + 1) +
                "\"annotated\": " +
                annotated +
                "\n" +
                indent(indent) +
                "}";
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
