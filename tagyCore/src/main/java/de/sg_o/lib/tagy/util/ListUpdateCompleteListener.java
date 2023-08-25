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

public interface ListUpdateCompleteListener<T> {

    /**
     * Called when the list has been updated.
     *
     * @param pagedList the updated list
     * @param minIndex  the index of the first loaded element in the updated list (inclusive)
     * @param maxIndex  the index of the last loaded element in the updated list (exclusive)
     */
    void onListUpdateComplete(PagedList<T> pagedList, int minIndex, int maxIndex);
}
