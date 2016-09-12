package com.trello.rxlifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleCompletableTransformer<T> implements CompletableTransformer {

    final Observable<T> lifecycle;

    public UntilLifecycleCompletableTransformer(@Nonnull Observable<T> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Completable apply(Completable source) throws Exception{
        return Completable.ambArray(
            source,
            lifecycle
                .flatMap(Functions.CANCEL_COMPLETABLE)
                .toCompletable()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleCompletableTransformer<?> that = (UntilLifecycleCompletableTransformer<?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleCompletableTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
