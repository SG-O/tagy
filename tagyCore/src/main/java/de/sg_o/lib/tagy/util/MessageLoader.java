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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageLoader {

    public static String getMessageFromBundle(String path, String key) {
        try {
            return ResourceBundle.getBundle(path, Locale.getDefault()).getString(key);
        } catch (MissingResourceException ignore) {
        }
        return key;
    }
}
