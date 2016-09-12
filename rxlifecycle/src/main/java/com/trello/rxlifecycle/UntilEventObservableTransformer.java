package com.trello.rxlifecycle;


import javax.annotation.Nonnull;

import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleTransformer;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventObservableTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> source) throws Exception {
        return source.takeUntil(takeUntilEvent(lifecycle, event));
    }

    @Nonnull
    @Override
    public SingleTransformer<T, T> forSingle() {
        return new UntilEventSingleTransformer<>(lifecycle, event);
    }

    @Nonnull
    @Override
    public CompletableTransformer forCompletable() {
        return new UntilEventCompletableTransformer<>(lifecycle, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilEventObservableTransformer<?, ?> that = (UntilEventObservableTransformer<?, ?>) o;

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
        return "UntilEventObservableTransformer{" +
            "lifecycle=" + lifecycle +
            ", event=" + event +
            '}';
    }
}
