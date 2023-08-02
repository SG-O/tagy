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

package de.sg_o.lib.tagy.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.sg_o.lib.tagy.Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Export {
    public enum Format {
        XML,
        YAML,
        JSON
    }

    private final OutputStream outputStream;
    private final Format format;

    public Export(ByteArrayOutputStream baos, Format format) {
        this.outputStream = baos;
        if (format == null) format = Format.JSON;
        this.format = format;
    }

    public Export(File file) {
        Format format = Format.JSON;
        OutputStream outputStream = null;
        if (file != null) {
            try {
                outputStream = Files.newOutputStream(file.toPath());
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                switch (extension) {
                    case "xml":
                        format = Format.XML;
                        break;
                    case "yaml":
                        format = Format.YAML;
                        break;
                }
            } catch (Exception ignore) {
            }
        }

        this.outputStream = outputStream;
        this.format = format;
    }

    public static String asString(Project project, Format format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Export export = new Export(baos, format);
        export.export(project);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public void export(Project project) {
        ObjectMapper mapper;
        switch (format) {
            case XML:
                mapper = new XmlMapper();
                break;
            case YAML:
                mapper = new YAMLMapper();
                break;
            default:
                mapper = new JsonMapper();
                break;
        }

        mapper.setVisibility(
                mapper.getSerializationConfig()
                        .getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)

        );

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        OutputStream out = this.outputStream;
        if (out == null) {
            out = System.out;
        }

        try {
            writer.writeValue(out, project);
        } catch (Exception ex) {
            System.out.println("Failed to export data" + ex.getMessage());
        }
        try {
            out.close();
        } catch (IOException ignore) {
        }
    }
}
