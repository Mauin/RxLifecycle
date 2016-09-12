package com.trello.rxlifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleSingleTransformer<T, R> implements SingleTransformer<T, T> {

    final Observable<R> lifecycle;

    public UntilLifecycleSingleTransformer(@Nonnull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Single<T> apply(Single<T> source) throws Exception {
        return source.toObservable().takeUntil(lifecycle).toSingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UntilLifecycleSingleTransformer<?, ?> that = (UntilLifecycleSingleTransformer<?, ?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleSingleTransformer{" +
                "lifecycle=" + lifecycle +
                '}';
    }
}
