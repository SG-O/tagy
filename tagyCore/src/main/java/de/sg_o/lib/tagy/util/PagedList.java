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

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * An immutable list that loads chunks of data in the background and
 * provides a list of the data.
 *
 * @param <T> the type of element in the list
 */
public class PagedList<T> implements List<T> {
    private static final Boolean[] updating = new Boolean[]{false, false, false, false, false};
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private List<T> page0 = new ArrayList<>(); //centerPage - 2
    private List<T> page1 = new ArrayList<>(); //centerPage - 1
    private List<T> page2 = new ArrayList<>(); //centerPage
    private List<T> page3 = new ArrayList<>(); //centerPage + 1
    private List<T> page4 = new ArrayList<>(); //centerPage + 2

    private final int thirdPage;
    private final boolean allowAutomaticLoading;

    private int centerPage = -3;

    private final ChunkGetter<T> chunkGetter;

    private final List<ListUpdateCompleteListener<T>> updateCompleteListeners = new ArrayList<>();


    /**
     * Creates a new PagedList.
     *
     * @param chunkGetter The chunk loading implementation
     * @param pageLength  The length of the pages required
     */
    public PagedList(@NotNull ChunkGetter<T> chunkGetter, int pageLength) {
        this(chunkGetter, pageLength, true);
    }

    /**
     * Creates a new PagedList.
     *
     * @param chunkGetter           The chunk loading implementation
     * @param pageLength            The length of the pages required
     * @param allowAutomaticLoading If true, the list will automatically load
     *                              the required page when accessing a page that
     *                              is not loaded yet. If false, the list will
     *                              throw an exception if accessing a page that
     *                              is not loaded yet.
     *                              Note that this setting only affects the
     *                              behavior of the {@link #get(int)}
     * @see #preparePages(int)
     * @see #get(int)
     */
    public PagedList(@NotNull ChunkGetter<T> chunkGetter, int pageLength, boolean allowAutomaticLoading) {
        if (pageLength < 1) pageLength = 1;
        this.chunkGetter = chunkGetter;
        this.thirdPage = (pageLength + 2) / 3;
        this.allowAutomaticLoading = allowAutomaticLoading;
    }

    private int calculateCenterPage(int index, boolean fromEnd) {
        int center = index / thirdPage;
        if (fromEnd) {
            center--;
        } else {
            center++;
        }
        return center;
    }

    private int calculatePage(int index) {
        return index / thirdPage;
    }

    private int calculateIndexInPage(int index) {
        return index % thirdPage;
    }

    private List<T> getListForPage(int page) {
        awaitUpdate(page + 2);
        if (page == -2) {
            synchronized (updating[0]) {
                return page0;
            }
        }
        if (page == -1) {
            synchronized (updating[1]) {
                return page1;
            }
        }
        if (page == 0) {
            synchronized (updating[2]) {
                return page2;
            }
        }
        if (page == 1) {
            synchronized (updating[3]) {
                return page3;
            }
        }
        if (page == 2) {
            synchronized (updating[4]) {
                return page4;
            }
        }
        throw new IndexOutOfBoundsException("Not loaded, yet");
    }

    private void awaitAllUpdates() {
        for (int i = 0; i < updating.length; i++) {
            awaitUpdate(i);
        }
    }

    private void awaitUpdate(int page) {
        if (page < 0 || page > 4) return;
        while (updating[page]) {
            try {
                //noinspection BusyWait
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void notifyUpdateComplete() {
        if (isUpdating()) return;
        for (ListUpdateCompleteListener<T> listener : updateCompleteListeners) {
            listener.onListUpdateComplete(this, firstAvailableIndex(), lastAvailableIndex());
        }
    }

    public void preparePages(int index) {
        this.preparePages(index, false);
    }

    /**
     * Prepares the list by loading the pages around the given index.
     *
     * @param index   The index to center the list around.
     * @param fromEnd If true, the index is considered to be the last element in the used portion of the list.
     */
    public void preparePages(int index, boolean fromEnd) {
        int newCenterPage = calculateCenterPage(index, fromEnd);
        int distanceFromOld = newCenterPage - this.centerPage;
        if (distanceFromOld == 0) return;
        if (distanceFromOld > 0 && distanceFromOld < 5) {
            for (int i = 0; i < distanceFromOld; i++) {
                shiftUp();
            }
            reloadFromEnd(distanceFromOld);
        } else if (distanceFromOld < 0 && distanceFromOld > -5) {
            for (int i = 0; i > distanceFromOld; i--) {
                shiftDown();
            }
            reloadFromStart(distanceFromOld * -1);
        } else {
            this.centerPage = newCenterPage;
            getAll();
        }
    }

    private void shiftUp() {
        awaitAllUpdates();
        this.centerPage++;
        page0 = page1;
        page1 = page2;
        page2 = page3;
        page3 = page4;
    }

    private void shiftDown() {
        awaitAllUpdates();
        this.centerPage--;
        page4 = page3;
        page3 = page2;
        page2 = page1;
        page1 = page0;
    }

    private void reloadFromStart(int to) {
        for (int i = 0; i < to; i++) {
            getChunk((centerPage - 2) + i, i);
        }
    }

    private void reloadFromEnd(int to) {
        for (int i = 0; i < to; i++) {
            getChunk((centerPage + 2) - i, 4 - i);
        }
    }

    private void getAll() {
        getChunk(centerPage - 2, 0);
        getChunk(centerPage - 1, 1);
        getChunk(centerPage, 2);
        getChunk(centerPage + 1, 3);
        getChunk(centerPage + 2, 4);
    }

    private void getChunk(int chunk, int page) {
        int offset = thirdPage * chunk;
        awaitUpdate(page);
        synchronized (updating[page]) {
            if (offset < 0) {
                setPage(null, page);
                return;
            }
            updating[page] = true;
        }
        FutureTask<List<T>> futureTask = new FutureTask<>(() -> {
            synchronized (updating[page]) {
                try {
                    List<T> read = chunkGetter.getChunk(thirdPage, offset);
                    setPage(read, page);
                    updating[page] = false;
                    notifyUpdateComplete();
                    return read;
                } catch (Exception e) {
                    setPage(null, page);
                    updating[page] = false;
                    notifyUpdateComplete();
                    return null;
                }
            }
        }
        );
        executor.execute(futureTask);
    }

    private void setPage(List<T> list, int page) {
        if (page == 0) {
            page0 = list;
            return;
        }
        if (page == 1) {
            page1 = list;
            return;
        }
        if (page == 2) {
            page2 = list;
            return;
        }
        if (page == 3) {
            page3 = list;
            return;
        }
        if (page == 4) {
            page4 = list;
        }
    }

    public boolean isUpdating() {
        for (Boolean b : updating) {
            if (b) return true;
        }
        return false;
    }

    public int firstAvailableIndex() {
        int index = (centerPage - 1) * thirdPage;
        if (index < 0) index = 0;
        return index;
    }

    public int lastAvailableIndex() {
        int firstIndex = firstAvailableIndex();
        if (page1 != null) firstIndex += page1.size();
        if (page2 != null) firstIndex += page2.size();
        if (page3 != null) firstIndex += page3.size();
        return firstIndex;
    }

    public void addUpdateCompleteListener(@NotNull ListUpdateCompleteListener<T> listUpdateCompleteListener) {
        this.updateCompleteListeners.add(listUpdateCompleteListener);
    }

    public boolean removeUpdateCompleteListener(@NotNull ListUpdateCompleteListener<T> listUpdateCompleteListener) {
        return this.updateCompleteListeners.remove(listUpdateCompleteListener);
    }

    @Override
    public int size() {
        return chunkGetter.getTotal();
    }

    @Override
    public boolean isEmpty() {
        return size() < 1;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported");
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }


    @Override
    public Object @NotNull [] toArray() {
        Iterator<T> iterator = iterator();
        Object[] output = new Object[size()];
        for (int i = 0; i < output.length; i++) {
            if (!iterator.hasNext()) {
                continue;
            }
            output[i] = iterator.next();
        }
        return output;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
        Iterator<T> iterator = iterator();
        int size = this.size();
        if (a.length < this.size()) {
            a = (T1[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        for (int i = 0; i < a.length; i++) {
            if (!iterator.hasNext()) {
                a[i] = null;
                continue;
            }
            a[i] = (T1) iterator.next();
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public T get(int index) {
        int pageNumber = calculatePage(index);
        int offset = pageNumber - this.centerPage;
        if (this.allowAutomaticLoading) {
            if (offset < -1) {
                preparePages(index, false);
                return get(index);
            }
            if (offset > 1) {
                preparePages(index, true);
                return get(index);
            }
        }
        List<T> page = getListForPage(offset);
        if (page == null) throw new ArrayIndexOutOfBoundsException();
        int indexInPage = calculateIndexInPage(index);
        return page.get(indexInPage);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported");
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListIterator<T>() {
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            private final PagedList<T> creatingPagedList = new PagedList<>(chunkGetter, size() / 5);

            private int i = index;
            private final int size = size();

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public T next() {
                T result = creatingPagedList.get(i);
                i++;
                return result;
            }

            @Override
            public boolean hasPrevious() {
                return i > 0;
            }

            @Override
            public T previous() {
                i--;
                return creatingPagedList.get(i);
            }

            @Override
            public int nextIndex() {
                return i;
            }

            @Override
            public int previousIndex() {
                return i - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public void set(T t) {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public void add(T t) {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        if (size < 0) size = 0;
        List<T> output = new ArrayList<>(size);
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        PagedList<T> creatingPagedList = new PagedList<>(chunkGetter, size / 5);
        for (int i = fromIndex; i < toIndex; i++) {
            output.add(creatingPagedList.get(i));
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagedList<?> pagedList = (PagedList<?>) o;
        return thirdPage == pagedList.thirdPage && allowAutomaticLoading == pagedList.allowAutomaticLoading && Objects.equals(chunkGetter, pagedList.chunkGetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thirdPage, allowAutomaticLoading, chunkGetter);
    }
}
