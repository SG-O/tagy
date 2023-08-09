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

package de.sg_o.lib.tagy.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.db.DB;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Uid;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Uid(2718146996373471930L)
public class DataSource {
    @Id
    @Uid(7262970591174364797L)
    private Long id;
    @NotNull
    @Uid(6831534710827527872L)
    private final String source;
    @Uid(4757138858399486251L)
    private boolean recursive;
    @NotNull
    @Uid(2033058702265229064L)
    private final List<String> fileExtensions;

    @SuppressWarnings("unused")
    public DataSource(Long id, @NotNull String source, boolean recursive, @NotNull List<String> fileExtensions) {
        this.id = id;
        this.source = source;
        this.recursive = recursive;
        this.fileExtensions = fileExtensions;
    }

    /**
     * @param locator       The directory containing the files of interest or a file containing a list of fies of interest
     * @param recursive     Whether to recursively search for files in subdirectories
     */
    public DataSource(@NotNull File locator, boolean recursive) {
        if (!locator.exists()) throw new RuntimeException("Root directory does not exist");

        this.source = locator.getAbsolutePath();
        this.recursive = recursive;
        this.fileExtensions = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull File resolveSource() {
        return new File(source);
    }

    @JsonProperty(value = "source", index = 0)
    public @NotNull String getSource() {
        return source;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    @JsonProperty(value = "recursive", index = 1)
    public boolean isRecursive() {
        return recursive;
    }

    @JsonProperty(value = "allowedFileExtensions", index = 2)
    public @NotNull ArrayList<String> getFileExtensions() {
        return new ArrayList<>(fileExtensions);
    }

    public void setFileExtensions(@NotNull List<String> fileExtensions) {
        this.fileExtensions.clear();
        for (String fileExtension : fileExtensions) {
            if (fileExtension.startsWith(".")) fileExtension = fileExtension.substring(1);
            if (fileExtension.contains(".")) throw new RuntimeException("File extension contains a dot");
            if (fileExtension.isEmpty()) continue;
            this.fileExtensions.add(fileExtension);
        }
    }

    public void setFileExtensions(@NotNull String fileExtension) {
        this.fileExtensions.clear();
        String[] fileExtensionArray = fileExtension.split(",");
        for (String fileExtensionElement : fileExtensionArray) {
            fileExtensionElement = fileExtensionElement.trim();
            if (fileExtensionElement.startsWith(".")) fileExtensionElement = fileExtensionElement.substring(1);
            if (fileExtensionElement.contains(".")) continue;
            if (fileExtensionElement.isEmpty()) continue;
            fileExtensions.add(fileExtensionElement);
        }
    }

    public @NotNull String getFileExtensionsAsString() {
        StringBuilder fileExtensions = new StringBuilder();
        for (int i = 0; i < this.fileExtensions.size(); i++) {
            String fileExtension = this.fileExtensions.get(i);
            fileExtensions.append(fileExtension);
            if (i < this.fileExtensions.size() - 1) fileExtensions.append(", ");
        }
        return fileExtensions.toString();
    }

    public @NotNull ArrayList<URL> getFiles() {
        StringBuilder regex = new StringBuilder();
        if (fileExtensions.isEmpty()) {
            regex.append(".*");
        }
        for (int i = 0; i < fileExtensions.size(); i++) {
            String fileExtension = fileExtensions.get(i);
            regex.append(".*\\.").append(fileExtension);
            if (i < fileExtensions.size() - 1) regex.append("|");
        }
        File locator = new File(source);
        if (locator.isDirectory()) {
            return getFilesInDirectory(regex.toString());
        } else {
            return getFilesFromList(regex.toString());
        }
    }

    private @NotNull ArrayList<URL> getFilesInDirectory(String regex) {
        int maxDepth = recursive ? 999 : 1;

        try (Stream<Path> files = Files.find(
                Paths.get(source), maxDepth,
                (p, bfa) -> bfa.isRegularFile()
                        && p.getFileName().toString().matches(regex)))
        {
            return files.map(Path::toUri).map(uri -> {
                try {
                    return uri.toURL();
                } catch (MalformedURLException e) {
                    return null;
                }
            }).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private @NotNull ArrayList<URL> getFilesFromList(String regex) {
        ArrayList<URL> urls = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(source));
            for (String line : lines) {
                if (line.matches(regex)) {
                    try {
                        urls.add(new URL(line));
                    } catch (MalformedURLException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            return urls;
        }
        return urls;
    }

    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<DataSource> box = db.boxFor(DataSource.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSource dataSource = (DataSource) o;
        return isRecursive() == dataSource.isRecursive() && Objects.equals(resolveSource(), dataSource.resolveSource()) && Objects.equals(getFileExtensions(), dataSource.getFileExtensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolveSource(), isRecursive(), getFileExtensions());
    }

    @Override
    public String toString() {
        return "{"
                + "\"source\": \"" + resolveSource().getName() + "\""
                + ", \"recursive\": " + recursive
                + ", \"fileExtensions\":" + fileExtensions
                + "}";
    }
}
