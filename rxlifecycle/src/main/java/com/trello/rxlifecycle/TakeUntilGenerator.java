package com.trello.rxlifecycle;


import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

final class TakeUntilGenerator {

    private TakeUntilGenerator() {
        throw new AssertionError("No instances!");
    }

    @Nonnull
    static <T> Observable<T> takeUntilEvent(@Nonnull final Observable<T> lifecycle, @Nonnull final T event) {
        return lifecycle.takeFirst(new Predicate<T>() {
            @Override
            public boolean test(T lifecycleEvent) throws Exception {
                return lifecycleEvent.equals(event);
            }
        });
    }

    @Nonnull
    static <T> Observable<Boolean> takeUntilCorrespondingEvent(@Nonnull final Observable<T> lifecycle,
                                                               @Nonnull final Function<T, T> correspondingEvents) {
        return Observable.combineLatest(
                lifecycle.take(1).map(correspondingEvents),
                lifecycle.skip(1),
                new BiFunction<T, T, Boolean>() {
                    @Override
                    public Boolean apply(T bindUntilEvent, T lifecycleEvent) throws Exception {
                        return lifecycleEvent.equals(bindUntilEvent);
                    }
                })
                .onErrorReturn(Functions.RESUME_FUNCTION)
                .takeFirst(Functions.SHOULD_COMPLETE);
    }
}
