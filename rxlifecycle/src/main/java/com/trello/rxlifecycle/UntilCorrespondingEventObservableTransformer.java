package com.trello.rxlifecycle;

import javax.annotation.Nonnull;

import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
final class UntilCorrespondingEventObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> sharedLifecycle;
    final Function<R, R> correspondingEvents;

    public UntilCorrespondingEventObservableTransformer(@Nonnull Observable<R> sharedLifecycle,
                                                        @Nonnull Function<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public Observable<T> apply(Observable<T> source) throws Exception {
        return source.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents));
    }

    @Nonnull
    @Override
    public SingleTransformer<T, T> forSingle() {
        return new UntilCorrespondingEventSingleTransformer<>(sharedLifecycle, correspondingEvents);
    }

    @Nonnull
    @Override
    public CompletableTransformer forCompletable() {
        return new UntilCorrespondingEventCompletableTransformer<>(sharedLifecycle, correspondingEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilCorrespondingEventObservableTransformer<?, ?> that
            = (UntilCorrespondingEventObservableTransformer<?, ?>) o;

        if (!sharedLifecycle.equals(that.sharedLifecycle)) { return false; }
        return correspondingEvents.equals(that.correspondingEvents);
    }

    @Override
    public int hashCode() {
        int result = sharedLifecycle.hashCode();
        result = 31 * result + correspondingEvents.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UntilCorrespondingEventObservableTransformer{" +
            "sharedLifecycle=" + sharedLifecycle +
            ", correspondingEvents=" + correspondingEvents +
            '}';
    }
}
