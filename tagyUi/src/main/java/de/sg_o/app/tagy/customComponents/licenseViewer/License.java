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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class License {
    private final String dependencyName;
    private final String licenseName;
    private final String licenseText;
    private final String licenseUrl;

    public License(ResourceBundle bundle, String dependencyId) throws MissingResourceException {
        this.dependencyName = bundle.getString(dependencyId + ".dependencyName");
        this.licenseName = bundle.getString(dependencyId + ".licenseName");
        this.licenseText = bundle.getString(dependencyId + ".licenseText");
        this.licenseUrl = bundle.getString(dependencyId + ".licenseUrl");
    }

    public String getDependencyName() {
        return dependencyName;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public String getLicenseText() {
        return licenseText;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }
}
