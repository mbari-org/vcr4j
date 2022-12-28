package org.mbari.vcr4j.util;

public interface Tuple {
    int size();

    Object getValueAt(int i);

    Class getTypeAt(int i);
}
