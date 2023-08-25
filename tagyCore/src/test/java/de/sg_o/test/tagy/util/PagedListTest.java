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

package de.sg_o.test.tagy.util;

import de.sg_o.lib.tagy.util.ChunkGetter;
import de.sg_o.lib.tagy.util.ListUpdateCompleteListener;
import de.sg_o.lib.tagy.util.PagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class PagedListTest {
    List<String> testData = new ArrayList<>();

    PagedList<String> pagedList0;
    PagedList<String> pagedList1;
    PagedList<String> pagedList2;
    PagedList<String> pagedList3;
    PagedList<String> pagedList4;
    PagedList<String> pagedList5;
    PagedList<String> pagedList6;
    PagedList<String> pagedList7;
    PagedList<String> pagedList8;
    PagedList<String> pagedList9;
    PagedList<String> pagedList10;
    PagedList<String> pagedList11;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 100; i++) {
            testData.add("TestData-" + i);
        }

        ChunkGetter<String> cg0 = new ChunkGetter<String>() {
            @Override
            public List<String> getChunk(int length, int offset) {
                int end = offset + length;
                if (end > testData.size()) {
                    end = testData.size();
                }
                return testData.subList(offset, end);
            }

            @Override
            public int getTotal() {
                return testData.size();
            }
        };

        pagedList0 = new PagedList<>(cg0, 5, false);
        pagedList1 = new PagedList<>(cg0, 5);
        pagedList2 = new PagedList<>(cg0, 6);
        pagedList3 = new PagedList<>(cg0, 7);
        pagedList4 = new PagedList<>(cg0, 8);
        pagedList5 = new PagedList<>(cg0, 9);
        pagedList6 = new PagedList<>(cg0, 10);
        pagedList7 = new PagedList<>(cg0, 0);
        pagedList8 = new PagedList<>(cg0, 1);
        pagedList9 = new PagedList<>(cg0, 2);
        pagedList10 = new PagedList<>(cg0, 3);
        pagedList11 = new PagedList<>(cg0, 4);
    }

    @Test
    void testGet() {
        AtomicBoolean listenerHit = new AtomicBoolean(false);
        ListUpdateCompleteListener<String> listUpdateCompleteListener = (pagedList, minIndex, maxIndex) -> {
            assertEquals(pagedList0, pagedList);
            assertEquals(0, minIndex);
            assertEquals(6, maxIndex);
            listenerHit.set(true);
        };
        pagedList0.addUpdateCompleteListener(listUpdateCompleteListener);
        assertFalse(pagedList0.isEmpty());
        pagedList0.preparePages(0);
        assertEquals(testData.get(0), pagedList0.get(0));
        assertFalse(pagedList0.isUpdating());
        assertEquals(testData.get(1), pagedList0.get(1));
        assertEquals(testData.get(2), pagedList0.get(2));
        assertEquals(testData.get(3), pagedList0.get(3));
        assertEquals(testData.get(4), pagedList0.get(4));
        assertTrue(listenerHit.get());
        assertTrue(pagedList0.removeUpdateCompleteListener(listUpdateCompleteListener));

        listenerHit.set(false);
        listUpdateCompleteListener = (pagedList, minIndex, maxIndex) -> {
            assertEquals(pagedList0, pagedList);
            assertEquals(20, minIndex);
            assertEquals(26, maxIndex);
            listenerHit.set(true);
        };
        pagedList0.addUpdateCompleteListener(listUpdateCompleteListener);
        pagedList0.preparePages(20);
        assertEquals(testData.get(20), pagedList0.get(20));
        assertEquals(testData.get(21), pagedList0.get(21));
        assertEquals(testData.get(22), pagedList0.get(22));
        assertEquals(testData.get(23), pagedList0.get(23));
        assertEquals(testData.get(24), pagedList0.get(24));
        assertTrue(listenerHit.get());
        assertTrue(pagedList0.removeUpdateCompleteListener(listUpdateCompleteListener));

        pagedList0.preparePages(21);
        assertEquals(testData.get(20), pagedList0.get(20));
        assertEquals(testData.get(21), pagedList0.get(21));
        assertEquals(testData.get(22), pagedList0.get(22));
        assertEquals(testData.get(23), pagedList0.get(23));
        assertEquals(testData.get(24), pagedList0.get(24));

        pagedList0.preparePages(19);
        assertEquals(testData.get(20), pagedList0.get(20));
        assertEquals(testData.get(21), pagedList0.get(21));
        assertEquals(testData.get(22), pagedList0.get(22));
        assertEquals(testData.get(23), pagedList0.get(23));
        assertEquals(testData.get(24), pagedList0.get(24));

        pagedList0.preparePages(20);
        assertEquals(testData.get(20), pagedList0.get(20));
        assertEquals(testData.get(21), pagedList0.get(21));
        assertEquals(testData.get(22), pagedList0.get(22));
        assertEquals(testData.get(23), pagedList0.get(23));
        assertEquals(testData.get(24), pagedList0.get(24));

        pagedList0.preparePages(1);
        assertEquals(testData.get(0), pagedList0.get(0));
        assertEquals(testData.get(1), pagedList0.get(1));
        assertEquals(testData.get(2), pagedList0.get(2));
        assertEquals(testData.get(3), pagedList0.get(3));
        assertEquals(testData.get(4), pagedList0.get(4));

        assertEquals(testData.get(5), pagedList0.get(5));
        assertEquals(testData.get(6), pagedList0.get(6));
        assertEquals(testData.get(7), pagedList0.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(9));

        pagedList0.preparePages(6);
        assertEquals(testData.get(5), pagedList0.get(5));
        assertEquals(testData.get(6), pagedList0.get(6));
        assertEquals(testData.get(7), pagedList0.get(7));
        assertEquals(testData.get(8), pagedList0.get(8));
        assertEquals(testData.get(9), pagedList0.get(9));

        assertEquals(testData.get(8), pagedList0.get(8));
        assertEquals(testData.get(7), pagedList0.get(7));
        assertEquals(testData.get(6), pagedList0.get(6));
        assertEquals(testData.get(5), pagedList0.get(5));
        assertEquals(testData.get(4), pagedList0.get(4));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList0.get(0));
    }

    @Test
    void testGetAutomatic() {
        testGetAutomatic(pagedList1);
        testGetAutomatic(pagedList2);
        testGetAutomatic(pagedList3);
        testGetAutomatic(pagedList4);
        testGetAutomatic(pagedList5);
        testGetAutomatic(pagedList6);
        testGetAutomatic(pagedList7);
        testGetAutomatic(pagedList8);
        testGetAutomatic(pagedList9);
        testGetAutomatic(pagedList10);
        testGetAutomatic(pagedList11);
    }


    void testGetAutomatic(PagedList<String> pagedList) {
        assertEquals(testData.get(0), pagedList.get(0));
        assertEquals(testData.get(1), pagedList.get(1));
        assertEquals(testData.get(2), pagedList.get(2));
        assertEquals(testData.get(3), pagedList.get(3));
        assertEquals(testData.get(4), pagedList.get(4));

        pagedList.preparePages(20);
        assertEquals(testData.get(20), pagedList.get(20));
        assertEquals(testData.get(21), pagedList.get(21));
        assertEquals(testData.get(22), pagedList.get(22));
        assertEquals(testData.get(23), pagedList.get(23));
        assertEquals(testData.get(24), pagedList.get(24));

        assertEquals(testData.get(20), pagedList.get(20));
        assertEquals(testData.get(21), pagedList.get(21));
        assertEquals(testData.get(22), pagedList.get(22));
        assertEquals(testData.get(23), pagedList.get(23));
        assertEquals(testData.get(24), pagedList.get(24));

        assertEquals(testData.get(20), pagedList.get(20));
        assertEquals(testData.get(21), pagedList.get(21));
        assertEquals(testData.get(22), pagedList.get(22));
        assertEquals(testData.get(23), pagedList.get(23));
        assertEquals(testData.get(24), pagedList.get(24));

        assertEquals(testData.get(20), pagedList.get(20));
        assertEquals(testData.get(21), pagedList.get(21));
        assertEquals(testData.get(22), pagedList.get(22));
        assertEquals(testData.get(23), pagedList.get(23));
        assertEquals(testData.get(24), pagedList.get(24));

        assertEquals(testData.get(0), pagedList.get(0));
        assertEquals(testData.get(1), pagedList.get(1));
        assertEquals(testData.get(2), pagedList.get(2));
        assertEquals(testData.get(3), pagedList.get(3));
        assertEquals(testData.get(4), pagedList.get(4));

        assertEquals(testData.get(5), pagedList.get(5));
        assertEquals(testData.get(6), pagedList.get(6));
        assertEquals(testData.get(7), pagedList.get(7));
        assertEquals(testData.get(8), pagedList.get(8));
        assertEquals(testData.get(9), pagedList.get(9));

        assertEquals(testData.get(5), pagedList.get(5));
        assertEquals(testData.get(6), pagedList.get(6));
        assertEquals(testData.get(7), pagedList.get(7));
        assertEquals(testData.get(8), pagedList.get(8));
        assertEquals(testData.get(9), pagedList.get(9));

        assertEquals(testData.get(8), pagedList.get(8));
        assertEquals(testData.get(7), pagedList.get(7));
        assertEquals(testData.get(6), pagedList.get(6));
        assertEquals(testData.get(5), pagedList.get(5));
        assertEquals(testData.get(4), pagedList.get(4));
        assertEquals(testData.get(3), pagedList.get(3));
        assertEquals(testData.get(2), pagedList.get(2));
        assertEquals(testData.get(1), pagedList.get(1));
        assertEquals(testData.get(0), pagedList.get(0));

        assertEquals(testData.get(95), pagedList.get(95));
        assertEquals(testData.get(96), pagedList.get(96));
        assertEquals(testData.get(97), pagedList.get(97));
        assertEquals(testData.get(98), pagedList.get(98));
        assertEquals(testData.get(99), pagedList.get(99));
        assertThrows(IndexOutOfBoundsException.class, () -> pagedList.get(100));

        List<String> sublist = pagedList.subList(5, 45);
        assertEquals(testData.subList(5, 45), sublist);
    }

    @Test
    void testIterator() {
        int index = 0;
        Iterator<String> iterator = pagedList0.iterator();
        while (iterator.hasNext()) {
            assertEquals(testData.get(index), iterator.next());
            index++;
        }

        index = 0;
        iterator = pagedList1.iterator();
        while (iterator.hasNext()) {
            assertEquals(testData.get(index), iterator.next());
            index++;
        }

        index = 0;
        for (String entry : pagedList0) {
            assertEquals(testData.get(index), entry);
            index++;
        }

        index = 0;
        for (String entry : pagedList1) {
            assertEquals(testData.get(index), entry);
            index++;
        }

        String[] array0 = pagedList0.toArray(new String[0]);
        String[] array1 = pagedList1.toArray(new String[120]);
        Object[] array2 = pagedList1.toArray();

        assertEquals(testData.size(), array0.length);
        assertEquals(120, array1.length);
        assertEquals(testData.size(), array2.length);

        index = 0;
        for (String entry : array0) {
            assertEquals(testData.get(index), entry);
            index++;
        }

        index = 0;
        for (String entry : array1) {
            if (index >= testData.size()) {
                assertNull(entry);
                continue;
            }
            assertEquals(testData.get(index), entry);
            index++;
        }

        index = 0;
        for (Object entry : array2) {
            assertEquals(testData.get(index), entry);
            index++;
        }

        ListIterator<String> listIterator0 = pagedList0.listIterator(50);
        index = 50;
        while (listIterator0.hasNext()) {
            assertEquals(testData.get(index), listIterator0.next());
            index++;
        }

        while (listIterator0.hasPrevious()) {
            index--;
            assertEquals(testData.get(index), listIterator0.previous());
        }

        assertThrows(RuntimeException.class, listIterator0::remove);
        assertThrows(RuntimeException.class, () -> listIterator0.set("Test"));
        assertThrows(RuntimeException.class, () -> listIterator0.add("Test"));

        ListIterator<String> listIterator1 = pagedList0.listIterator(20);
        index = listIterator1.nextIndex();
        while (listIterator1.hasNext()) {
            assertEquals(testData.get(index), listIterator1.next());
            index++;
        }
        index = listIterator1.previousIndex();
        while (listIterator1.hasPrevious()) {
            assertEquals(testData.get(index), listIterator1.previous());
            index--;
        }
    }

    @Test
    void notImplemented() {
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.contains("Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.add("Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.remove("Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.containsAll(testData));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.addAll(testData));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.addAll(0, testData));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.removeAll(testData));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.retainAll(testData));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.clear());
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.set(0, "Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.add(0, "Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.indexOf("Test"));
        assertThrows(UnsupportedOperationException.class, () -> pagedList0.lastIndexOf("Test"));
    }

    @Test
    void equalsAndHashCode() {
        assertNotEquals(pagedList0, pagedList1);
        assertEquals(pagedList7, pagedList8);

        assertEquals(1640710054L, pagedList0.hashCode());
        assertEquals(1640709868L, pagedList1.hashCode());
        assertEquals(1640709868L, pagedList2.hashCode());
        assertEquals(1640710829L, pagedList3.hashCode());
        assertEquals(1640710829L, pagedList4.hashCode());
        assertEquals(1640710829L, pagedList5.hashCode());
        assertEquals(1640711790L, pagedList6.hashCode());
        assertEquals(1640708907L, pagedList7.hashCode());
        assertEquals(1640708907L, pagedList8.hashCode());
        assertEquals(1640708907L, pagedList9.hashCode());
        assertEquals(1640708907L, pagedList10.hashCode());
        assertEquals(1640709868L, pagedList11.hashCode());

        assertNotEquals(pagedList0.hashCode(), pagedList1.hashCode());
        assertEquals(pagedList7.hashCode(), pagedList8.hashCode());
    }
}
