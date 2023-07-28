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

package de.sg_o.lib.tagy.db;

public class DbConstants {
    //Collections
    public static final String CONFIG_COLLECTION_NAME = "config";
    public static final String DATA_COLLECTION_NAME = "data";
    public static final String META_COLLECTION_NAME = "meta";

    //Documents
    public static final String STRUCTURE_DEFINITION_DOCUMENT_NAME = "structureDefinition";
    public static final String DIRECTORY_CONFIG_DOCUMENT_NAME = "directoryConfig";

    //Keys
    public static final String ID_KEY = "id";
    public static final String USER_NAME_KEY = "userName";
    public static final String USER_ID_KEY = "userId";
    public static final String TAGS_KEY = "tags";
    public static final String EDIT_HISTORY_KEY = "editHistory";

}
