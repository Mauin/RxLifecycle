package com.trello.rxlifecycle.navi;

import io.reactivex.functions.Predicate;

final class RxUtils {

    private RxUtils() {
        throw new AssertionError("No instances!");
    }

    static <T> Predicate<T> notNull() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return t != null;
            }
        };
    }
}
