package org.mbari.vcr4j.util;

import java.util.Objects;

public class Pair<A,B> implements Tuple {
    private A a;
    private B b;


    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() { return a; }
    public void setA(final A a) { this.a = a; }

    public B getB() { return b; }
    public void setB(final B b) { this.b = b; }

    @Override public int size() { return 2; }

    @Override public Object getValueAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a;    }
            case 1  -> { return this.b;    }
            default -> { return null; }
        }
    }

    @Override public Class getTypeAt(final int i) {
        if (0 > i && i >= size()) { throw new IllegalArgumentException("Index out of bounds, " + getClass().getSimpleName() + " has " + size() + " elements"); }
        switch(i) {
            case 0  -> { return this.a.getClass(); }
            case 1  -> { return this.b.getClass(); }
            default -> { return null; }
        }
    }

    @Override public String toString() {
        return "{" + a + "," + b + "}";
    }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return a.equals(pair.a) && b.equals(pair.b);
    }

    @Override public int hashCode() {
        return Objects.hash(a, b);
    }
}
