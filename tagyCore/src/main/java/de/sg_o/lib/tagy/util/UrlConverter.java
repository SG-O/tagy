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

import io.objectbox.converter.PropertyConverter;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlConverter implements PropertyConverter<URL, String> {
    @Override
    @NotNull
    public URL convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            try {
                return new URL("file:/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        if (databaseValue.startsWith("/")) {
            databaseValue = "file:" + databaseValue;
        }
        try {
            return new URL(databaseValue);
        } catch (MalformedURLException e) {
            try {
                return new URL("file:/");
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public String convertToDatabaseValue(URL entityProperty) {
        return entityProperty == null ? null : entityProperty.toString();
    }
}