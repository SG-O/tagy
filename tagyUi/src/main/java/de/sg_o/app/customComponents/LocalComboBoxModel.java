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

package de.sg_o.app.customComponents;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

public class LocalComboBoxModel extends DefaultComboBoxModel<String> {
    private final List<Locale> locales;
    private int selectedIndex = -1;

    public LocalComboBoxModel(List<Locale> locales) {
        this.locales = locales;
    }

    @Override
    public int getSize() {
        return locales.size();
    }

    @Override
    public String getElementAt(int index) {
        return locales.get(index).getDisplayLanguage();
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if (!(anObject instanceof Locale)) {
            setSelectedStringItem(anObject);
            return;
        }
        Locale locale = (Locale) anObject;
        if (locales.contains(locale)) {
            this.selectedIndex = locales.indexOf(locale);
            fireContentsChanged(this, -1, -1);
            return;
        }
        for (Locale loc : locales) {
            if (loc.getLanguage().equals(locale.getLanguage())) {
                this.selectedIndex = locales.indexOf(loc);
                fireContentsChanged(this, -1, -1);
                return;
            }
        }
    }

    public void setSelectedStringItem(Object anObject) {
        if (!(anObject instanceof String)) return;
        for (Locale locale : locales) {
            if (locale.getDisplayLanguage().equals(anObject)) {
                this.selectedIndex = locales.indexOf(locale);
                fireContentsChanged(this, -1, -1);
                return;
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        if (selectedIndex < 0) return null;
        if (selectedIndex >=  locales.size()) return null;
        return locales.get(selectedIndex).getDisplayLanguage();
    }

    public Locale getAt(int index) {
        return locales.get(index);
    }
}
