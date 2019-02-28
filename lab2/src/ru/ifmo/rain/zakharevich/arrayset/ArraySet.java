package ru.ifmo.rain.zakharevich.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final String UNSUPPORTED_OPERATION_MESSAGE = "ArraySet is immutable";

    private final List<E> elements;
    private Comparator<? super E> comparator;
    private ArraySet<E> descendingSet;

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        SortedSet<E> sortingSet = new TreeSet<>(comparator);
        sortingSet.addAll(collection);

        elements = new ArrayList<>(sortingSet);
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet() {
        this(Collections.emptyList());
    }

    private ArraySet(List<E> elements, Comparator<? super E> comparator) {
        this.elements = elements;
        this.comparator = comparator;
    }

    private int getIndexOfElement(E e, int shiftIfFound, int shiftIfNotFound) {
        int index = Collections.binarySearch(elements, e, comparator);
        return index >= 0 ? index + shiftIfFound : -index - 1 + shiftIfNotFound;
    }

    private int lowerIndex(E e) {
        return getIndexOfElement(e, -1, -1);
    }

    private int floorIndex(E e) {
        return getIndexOfElement(e, 0, -1);
    }

    private int ceilingIndex(E e) {
        return getIndexOfElement(e, 0, 0);
    }

    private int higherIndex(E e) {
        return getIndexOfElement(e, 1, 0);
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < elements.size();
    }

    private E getElementByIndex(int index) {
        return isValidIndex(index) ? elements.get(index) : null;
    }

    @Override
    public E lower(E e) {
        return getElementByIndex(lowerIndex(e));
    }

    @Override
    public E floor(E e) {
        return getElementByIndex(floorIndex(e));
    }

    @Override
    public E ceiling(E e) {
        return getElementByIndex(ceilingIndex(e));
    }

    @Override
    public E higher(E e) {
        return getElementByIndex(higherIndex(e));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) o, comparator) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    private class ReverseView<E> extends AbstractList<E> {

        private List<E> backingList;

        ReverseView(List<E> list) {
            backingList = list;
        }

        @Override
        public E get(int index) {
            return backingList.get(size() - index - 1);
        }

        @Override
        public int size() {
            return backingList.size();
        }
    }

    @Override
    public NavigableSet<E> descendingSet() {
        if (descendingSet == null) {
            descendingSet = new ArraySet<>(new ReverseView<>(elements), Collections.reverseOrder(comparator));
            descendingSet.descendingSet = this;
        }
        return descendingSet;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    private NavigableSet<E> subSetFromElements(int fromIndex, int toIndex) {

        if (fromIndex >= elements.size() || toIndex > elements.size() || fromIndex > toIndex) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }

        return new ArraySet<>(elements.subList(fromIndex, toIndex), comparator);
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {

        int fromIndex = (fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement));
        int toIndex = (toInclusive ? floorIndex(toElement) : lowerIndex(toElement)) + 1;
        return subSetFromElements(fromIndex, toIndex);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {

        int toIndex = (inclusive ? floorIndex(toElement) : lowerIndex(toElement)) + 1;
        return subSetFromElements(0, toIndex);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {

        int fromIndex = (inclusive ? ceilingIndex(fromElement) : higherIndex(fromElement));
        return subSetFromElements(fromIndex, elements.size());
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(0);
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return elements.get(elements.size() - 1);
    }

    @Override
    public int size() {
        return elements.size();
    }
}
