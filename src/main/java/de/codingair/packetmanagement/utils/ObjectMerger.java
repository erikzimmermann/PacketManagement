package de.codingair.packetmanagement.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ObjectMerger<A> {
    private final int results;
    private final BiFunction<A, A, A> merger;
    private final List<A> objects;

    public ObjectMerger(int results, BiFunction<A, A, A> merger) {
        this.results = results;
        this.merger = merger;
        objects = new ArrayList<>(results);
    }

    public boolean append(A object) {
        if (this.objects.size() == results) return false;
        this.objects.add(object);
        return this.objects.size() == results;
    }

    public A complete(A def) {
        A result = null;

        for (A f : objects) {
            if (result == null) result = f;
            else result = merger.apply(result, f);
        }

        return result == null ? def : result;
    }
}
