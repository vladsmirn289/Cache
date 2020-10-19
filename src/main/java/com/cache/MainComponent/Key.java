package com.cache.MainComponent;

import java.util.Objects;

public class Key<T> implements Comparable<Key<T>> {
    private T value;
    private final long lastUsed = System.currentTimeMillis();

    public Key(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key = (Key<?>) o;
        return Objects.equals(value, key.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(Key<T> o) {
        return (int)(lastUsed/1000) - (int)(o.lastUsed/1000);
    }
}
