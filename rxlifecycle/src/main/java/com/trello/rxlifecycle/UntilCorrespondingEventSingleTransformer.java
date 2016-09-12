package com.trello.rxlifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
final class UntilCorrespondingEventSingleTransformer<T, R> implements SingleTransformer<T, T> {

    final Observable<R> sharedLifecycle;
    final Function<R, R> correspondingEvents;

    public UntilCorrespondingEventSingleTransformer(@Nonnull Observable<R> sharedLifecycle,
                                                    @Nonnull Function<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public SingleSource<T> apply(Single<T> source) throws Exception {
        return source.toObservable().takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents)).toSingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilCorrespondingEventSingleTransformer<?, ?> that = (UntilCorrespondingEventSingleTransformer<?, ?>) o;

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
        return "UntilCorrespondingEventSingleTransformer{" +
            "sharedLifecycle=" + sharedLifecycle +
            ", correspondingEvents=" + correspondingEvents +
            '}';
    }
}
