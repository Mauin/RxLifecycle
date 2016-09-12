package com.trello.rxlifecycle;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

/**
 * A Transformer that works for all RxJava types ({@link Observable}, {@link Single} and {@link Completable}).
 * <p>
 * Out of the box, it works for Observable. But it can be easily converted
 * for {@link Single} or for {@link Completable}.
 */
public interface LifecycleTransformer<T> extends ObservableTransformer<T, T> {

    /**
     * @return a version of this Transformer for {@link Single} streams.
     * <p>
     * If interrupted by the lifecycle, this stream throws onError({@link java.util.concurrent.CancellationException}).
     */
    @Nonnull
    @CheckReturnValue
    // Implementation note: We use a different generic to cover some insane case in Java 8 inference.
    // See more here: https://github.com/trello/RxLifecycle/issues/126
    <U> SingleTransformer<U, U> forSingle();

    /**
     * @return a version of this Transformer for {@link Completable} streams.
     * <p>
     * If interrupted by the lifecycle, this stream throws onError({@link java.util.concurrent.CancellationException}).
     */
    @Nonnull
    @CheckReturnValue
    CompletableTransformer forCompletable();

}
