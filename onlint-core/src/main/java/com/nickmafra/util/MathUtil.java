package com.nickmafra.util;

public class MathUtil {

    private MathUtil() {}

    public static <T extends Comparable<T>> boolean lessThan(T a, T b, boolean inclusive) {
        int s = a.compareTo(b);
        return s < 0 || inclusive && s == 0;
    }

    public static <T extends Comparable<T>> boolean greaterThan(T a, T b, boolean inclusive) {
        int s = a.compareTo(b);
        return s > 0 || inclusive && s == 0;
    }

    public static <T extends Comparable<T>, U> U getRange(T val, T min, T max, U inRangeOut, U minOut, U maxOut, boolean inclusive, Runnable outCallback) {
        if (lessThan(max, min, !inclusive))
            throw new IllegalArgumentException("max must be greater than min.");

        U outVal;
        boolean calback;

        if (lessThan(val, min, !inclusive)) {
            outVal = minOut;
            calback = true;
        } else if (greaterThan(val, max, !inclusive)) {
            outVal = maxOut;
            calback = true;
        } else {
            outVal = inRangeOut;
            calback = false;
        }

        if (calback && outCallback != null) {
            outCallback.run();
        }
        return outVal;
    }

    public static <T extends Comparable<T>> T limitRange(T val, T min, T max, boolean inclusive, Runnable outCallback) {
        return getRange(val, min, max, val, min, max, inclusive, outCallback);
    }

    public static <T extends Comparable<T>> T limitRange(T value, T min, T max) {
        return limitRange(value, min, max, false, null);
    }

    public static <T extends Comparable<T>> boolean inRange(T value, T min, T max) {
        return getRange(value, min, max, true, false, false, true, null);
    }
}
