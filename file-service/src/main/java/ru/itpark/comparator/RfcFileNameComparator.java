package ru.itpark.comparator;

import java.util.Comparator;

public class RfcFileNameComparator implements Comparator<String> {
    int extractInt(String str) {
        String num = str.replaceAll("\\D", "");
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }

    @Override
    public int compare(String o1, String o2) {
        return extractInt(o1) - extractInt(o2);
    }
}
