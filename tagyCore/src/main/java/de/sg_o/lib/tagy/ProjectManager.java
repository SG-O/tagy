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

package de.sg_o.lib.tagy;

import com.couchbase.lite.Scope;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.values.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Set;

public class ProjectManager extends AbstractListModel<String>  {
    private final ArrayList<String> projects;

    public ProjectManager() {
        projects = listProjects();
    }

    public void createProject(String name, User user) {
        Project project = new Project(name, user);
        project.save();
        projects.add(name);
        fireContentsChanged(this, 0, projects.size());
    }

    public ArrayList<String> listProjects() {
        ArrayList<String> projects = new ArrayList<>();
        Set<Scope> scopes = DB.listScopes();
        for (Scope scope : scopes) {
            String scopeName = scope.getName();
            if (scopeName.equals(Scope.DEFAULT_NAME)) continue;
            projects.add(scope.getName());
        }
        return projects;
    }


    @Override
    public int getSize() {
        return projects.size();
    }

    @Override
    public String getElementAt(int index) {
        if (index < 0 || index >= projects.size()) return null;
        return projects.get(index);
    }
}