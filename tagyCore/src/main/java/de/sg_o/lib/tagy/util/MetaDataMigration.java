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

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.ProjectManager;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.data.MetaData_;
import de.sg_o.lib.tagy.data.TagContainer_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MetaDataMigration {
    private static final int BATCH_SIZE = 100;

    private static final Executor singleThreadExecutor = Executors.newSingleThreadExecutor();

    long total = 0;
    long done = 0;
    boolean finished = false;
    ProjectManager projectManager;

    MigrationCallBack callBack;


    public MetaDataMigration(ProjectManager projectManager) {
        this.projectManager = projectManager;
        for (Project project : projectManager.getAllProjects()) {
            this.total += CountMigration(project);
        }
    }

    public void migrate() {
        singleThreadExecutor.execute(new MigrationTask());
    }

    public boolean needsMigration() {
        return this.total > 0;
    }

    public boolean isFinished() {
        if (this.total < 1) return true;
        return this.finished;
    }

    public void setCallBack(MigrationCallBack callBack) {
        this.callBack = callBack;
    }

    private static long CountMigration(Project project) {
        BoxStore db = DB.getDb();
        if (db == null) {
            return 0;
        }
        Box<MetaData> metaData = db.boxFor(MetaData.class);
        if (metaData == null) {
            return 0;
        }
        long count = 0;
        try (Query<MetaData> query = metaData.query().apply(MetaData_.projectId.equal(project.getId())
                .and(MetaData_.tags.notNull().or(MetaData_.fileReference.isNull()))).build()) {
            count += query.count();
        }
        QueryBoxSpec<MetaData> repair = qb -> {
            qb.apply(MetaData_.projectId.equal(project.getId()));
            qb.link(MetaData_.tagContainers).apply(TagContainer_.tagDefinitionId.equal(0));
            return qb;
        };
        try (Query<MetaData> query = repair.buildQuery(metaData.query()).build()) {
            count += query.count();
        }
        return count;
    }

    private static List<MetaData> needsMigration(Project project) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb.apply(MetaData_.projectId.equal(project.getId())
                    .and(MetaData_.tags.notNull().or(MetaData_.fileReference.isNull())));
            return qb;
        };
        return DB.query(MetaData.class, qbs, BATCH_SIZE, 0);
    }

    private static List<MetaData> needsRepair(Project project) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb.apply(MetaData_.projectId.equal(project.getId()));
            qb.link(MetaData_.tagContainers).apply(TagContainer_.tagDefinitionId.equal(0));
            return qb;
        };
        return DB.query(MetaData.class, qbs, BATCH_SIZE, 0);
    }

    private void migrationFinished() {
        if (callBack == null) return;
        callBack.onMigrationFinished();
    }

    private void migrationProgressChanged(float progress) {
        if (callBack == null) return;
        callBack.onProgressChanged(progress);
    }

    private class MigrationTask implements Runnable {
        @Override
        public void run() {
            BoxStore db = DB.getDb();
            if (db == null || total == 0) {
                finished = true;
                migrationFinished();
                return;
            }
            for (Project project : projectManager.getAllProjects()) {
                List<MetaData> needsMigration = needsMigration(project);
                while (!needsMigration.isEmpty()) {
                    List<MetaData> finalNeedsMigration = needsMigration;
                    db.runInTx(() -> {
                                for (MetaData md : finalNeedsMigration) {
                                    md.save();
                                    done++;
                                    migrationProgressChanged((float) done / (float) total);
                                }
                            });
                    needsMigration = needsMigration(project);
                }
                List<MetaData> needRepair = needsRepair(project);
                while (!needRepair.isEmpty()) {
                    List<MetaData> finalNeedsMigration = needRepair;
                    db.runInTx(() -> {
                        for (MetaData md : finalNeedsMigration) {
                            md.repairTagContainer();
                            md.save();
                            done++;
                            migrationProgressChanged((float) done / (float) total);
                        }
                    });
                    needRepair = needsMigration(project);
                }
            }
            finished = true;
            migrationFinished();
        }
    }
}
