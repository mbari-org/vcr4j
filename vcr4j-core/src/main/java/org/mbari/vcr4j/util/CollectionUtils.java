package org.mbari.vcr4j.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionUtils {

    public static <T> Collection<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();

        for (T t : a) {
            if(b.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static <T> List<List<T>> grouped(List<T> collection, int batchSize) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return IntStream.iterate(0, i -> i < collection.size(), i -> i + batchSize)
                .mapToObj(i -> collection.subList(i, Math.min(i + batchSize, collection.size())))
                .collect(Collectors.toList());
    }
}
