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

package de.sg_o.test.tagy;

import de.sg_o.lib.tagy.ProjectManager;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.test.tagy.testDb.TestDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectManagerTest {
    ProjectManager pm;

    @BeforeEach
    void setUp() {
        pm = new ProjectManager();
        DB.closeDb();
        new TestDb();
    }

    @Test
    void listProjects() {
        List<String> projects = pm.listProjects();
        assertTrue(projects.contains("Demo_Project_0"));
        assertTrue(projects.contains("Demo_Project_1"));
    }
}