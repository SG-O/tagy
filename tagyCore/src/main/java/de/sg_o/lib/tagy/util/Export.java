package de.sg_o.lib.tagy.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.sg_o.lib.tagy.Project;

import java.io.*;

public class Export {
    public enum Format {
        XML,
        YAML,
        JSON
    }

    private final File file;
    private final Format format;

    public Export(File file) {
        this.file = file;
        Format format = Format.JSON;
        if (file == null) {
            this.format = format;
            return;
        }
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
        this.format = format;
    }

    @SuppressWarnings("unused")
    public Export(Format format) {
        this.file = null;
        this.format = format;
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
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        );

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        OutputStream out = null;
        if (file != null) {
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException ignore) {
            }
        }
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
