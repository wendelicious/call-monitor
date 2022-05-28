package com.ximasoftware.util;

import java.util.Arrays;
import java.util.Objects;

public class Assertion {
    public static <T> T checkNotNull(T thing) {
        if (thing == null) {
            throw new NullPointerException("can't be null");
        }
        return thing;
    }

    public static <T> T checkNotNull(T thing, String errorDescription) {
        if (thing == null) {
            throw new NullPointerException(errorDescription);
        }
        return thing;
    }

    public static void checkState(boolean shouldBeTrue, String errorDescription) {
        if (!shouldBeTrue) {
            throw new IllegalStateException(errorDescription);
        }
    }

    public static void checkArgument(boolean shouldBeTrue, String errorDescription) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException(errorDescription);
        }
    }

    public static boolean isEmpty(String test) {
        return test == null || test.trim().isEmpty();
    }

    public static boolean isNotEmpty(String test) {
        return !isEmpty(test);
    }

    public static <T> T firstNonNull(T... things) {
        if (things == null) {
            return null;
        }

        return Arrays.stream(things)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
