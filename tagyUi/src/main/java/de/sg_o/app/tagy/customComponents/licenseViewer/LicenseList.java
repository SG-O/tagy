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

package de.sg_o.app.tagy.customComponents.licenseViewer;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LicenseList extends ArrayList<License> {
    public LicenseList(String resourceBundle) {
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundle);
        bundle.keySet().forEach(key -> {
            if (!key.endsWith(".dependencyName")) return;
            key = key.split("\\.")[0];
            try {
                License license = new License(bundle, key);
                add(license);
            } catch (MissingResourceException ignored) {
            }
        });
    }
}
