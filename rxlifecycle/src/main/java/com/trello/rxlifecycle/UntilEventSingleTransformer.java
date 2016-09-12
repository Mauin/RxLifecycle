package com.trello.rxlifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventSingleTransformer<T, R> implements SingleTransformer<T, T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventSingleTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public SingleSource<T> apply(Single<T> source) throws Exception {
        return source.toObservable().takeUntil(takeUntilEvent(lifecycle, event)).toSingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilEventSingleTransformer<?, ?> that = (UntilEventSingleTransformer<?, ?>) o;

        if (!lifecycle.equals(that.lifecycle)) { return false; }
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = lifecycle.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UntilEventSingleTransformer{" +
            "lifecycle=" + lifecycle +
            ", event=" + event +
            '}';
    }
}
