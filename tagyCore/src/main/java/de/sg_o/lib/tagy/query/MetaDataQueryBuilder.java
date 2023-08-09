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

package de.sg_o.lib.tagy.query;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.data.MetaData_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.data.TagContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MetaDataQueryBuilder {
    Project project;
    private final ArrayList<QueryElement> queryProperties = new ArrayList<>();

    public MetaDataQueryBuilder(Project project) {
        this.project = project;
    }

    public void addQueryElement(@NotNull QueryElement queryProperty) {
        queryProperties.add(queryProperty);
    }

    public List<MetaData> query(int length, int offset) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb.equal(MetaData_.projectId, project.getId());
            for (QueryElement queryElement : queryProperties) {
                io.objectbox.query.QueryBuilder<TagContainer> tcQb = qb.link(MetaData_.tagContainers);
                queryElement.genrateQuerySpec().buildQuery(tcQb);
            }
            return qb;
        };
        return DB.query(MetaData.class, qbs, length, offset);
    }
}
