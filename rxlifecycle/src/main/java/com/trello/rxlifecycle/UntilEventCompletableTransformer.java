package com.trello.rxlifecycle;


import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventCompletableTransformer<T> implements CompletableTransformer {

    final Observable<T> lifecycle;
    final T event;

    public UntilEventCompletableTransformer(@Nonnull Observable<T> lifecycle, @Nonnull T event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public CompletableSource apply(Completable source) throws Exception {
        return Completable.ambArray(
                source,
                takeUntilEvent(lifecycle, event)
                        .flatMap(Functions.CANCEL_COMPLETABLE)
                        .toCompletable()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UntilEventCompletableTransformer<?> that = (UntilEventCompletableTransformer<?>) o;

        if (!lifecycle.equals(that.lifecycle)) {
            return false;
        }
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = lifecycle.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

}
