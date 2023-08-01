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

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Directory {
    @Id
    Long id;
    @NotNull
    private final String rootDirectory;
    private boolean recursive;
    @NotNull
    private final List<String> fileExtensions;

    public Directory(Long id, @NotNull String rootDirectory, boolean recursive, @NotNull List<String> fileExtensions) {
        this.id = id;
        this.rootDirectory = rootDirectory;
        this.recursive = recursive;
        this.fileExtensions = fileExtensions;
    }

    /**
     * @param rootDirectory The directory containing the files of interest
     * @param recursive     Whether to recursively search for files in subdirectories
     */
    public Directory(@NotNull File rootDirectory, boolean recursive) {
        if (!rootDirectory.isDirectory()) throw new RuntimeException("Root directory is not a directory");
        if (!rootDirectory.exists()) throw new RuntimeException("Root directory does not exist");

        this.rootDirectory = rootDirectory.getAbsolutePath();
        this.recursive = recursive;
        this.fileExtensions = new ArrayList<>();
    }

    public @NotNull File resolveRootDirectory() {
        return new File(rootDirectory);
    }

    public @NotNull String getRootDirectory() {
        return rootDirectory;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isRecursive() {
        return recursive;
    }

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

    public @NotNull ArrayList<File> getFiles() {
        StringBuilder regex = new StringBuilder();
        if (fileExtensions.isEmpty()) {
            regex.append(".*");
        }
        for (int i = 0; i < fileExtensions.size(); i++) {
            String fileExtension = fileExtensions.get(i);
            regex.append(".*\\.").append(fileExtension);
            if (i < fileExtensions.size() - 1) regex.append("|");
        }

        int maxDepth = recursive ? 999 : 1;

        try (Stream<Path> files = Files.find(
                Paths.get(rootDirectory), maxDepth,
                (p, bfa) -> bfa.isRegularFile()
                        && p.getFileName().toString().matches(regex.toString())))
        {
            return files.map(Path::toFile).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return isRecursive() == directory.isRecursive() && Objects.equals(resolveRootDirectory(), directory.resolveRootDirectory()) && Objects.equals(getFileExtensions(), directory.getFileExtensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolveRootDirectory(), isRecursive(), getFileExtensions());
    }

    @Override
    public String toString() {
        return "{"
                + "\"rootDirectory\": \"" + resolveRootDirectory().getName() + "\""
                + ", \"recursive\": " + recursive
                + ", \"fileExtensions\":" + fileExtensions
                + "}";
    }
}
