package org.ooka.sfisc12s.util.proxy;

import org.ooka.sfisc12s.util.Relation;

import java.util.*;

/**
 * Created by steve on 18.11.15.
 */
public class VirtualList<U, V, R extends Relation<U, V>> implements List<U> {
    private List<U> source;
    private R relation;
    private V foreignEntity;

    public VirtualList(V foreignEntity, R relation) {
        this.foreignEntity = foreignEntity;
        this.relation = relation;
    }

    private List<U> getSource() {
        return source == null ? source = relation.findAllByEntity(foreignEntity) : source;
    }

    @Override
    public int size() {
        return this.getSource().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getSource().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.getSource().contains(o);
    }

    @Override
    public Iterator<U> iterator() {
        return this.getSource().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.getSource().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.getSource().toArray(a);
    }

    @Override
    public boolean add(U t) {
        return this.getSource().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.getSource().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.getSource().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends U> c) {
        return this.getSource().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends U> c) {
        return this.getSource().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.getSource().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.getSource().retainAll(c);
    }

    @Override
    public void clear() {
        this.getSource().clear();
    }

    @Override
    public U get(int index) {
        return this.getSource().get(index);
    }

    @Override
    public U set(int index, U element) {
        return this.getSource().set(index, element);
    }

    @Override
    public void add(int index, U element) {
        this.getSource().add(index, element);
    }

    @Override
    public U remove(int index) {
        return this.getSource().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.getSource().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.getSource().lastIndexOf(o);
    }

    @Override
    public ListIterator<U> listIterator() {
        return this.getSource().listIterator();
    }

    @Override
    public ListIterator<U> listIterator(int index) {
        return this.getSource().listIterator(index);
    }

    @Override
    public List<U> subList(int fromIndex, int toIndex) {
        return this.getSource().subList(fromIndex, toIndex);
    }
}

