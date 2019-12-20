package ru.itpark.comparator;

import java.util.Comparator;

public class ResultsComparator implements Comparator<String> {
    int extractFirstInt(String str) {
        int index = str.indexOf("Line");
        String num = index != -1 ? str.substring(0, index).replaceAll("\\D", "") : "";
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }

    @Override
    public int compare(String o1, String o2) {
        return extractFirstInt(o1) - extractFirstInt(o2);
    }
}
