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

public abstract class JsonPrintable {

    public abstract String toString(int indent);
    public String toString() {
        return toString(0);
    }

    public String generateEntity(String key, Object value, boolean encapsulate, int indent) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateIndent(indent + 1));
        builder.append("\"");
        builder.append(key);
        builder.append("\": ");
        if(encapsulate) builder.append("\"");
        builder.append(value);
        if(encapsulate) builder.append("\"");
        return builder.toString();
    }

    public String generateIndent(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append('\t');
        }
        return builder.toString();
    }
}
