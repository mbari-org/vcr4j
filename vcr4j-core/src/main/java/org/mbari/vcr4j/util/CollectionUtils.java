package org.mbari.vcr4j.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}
