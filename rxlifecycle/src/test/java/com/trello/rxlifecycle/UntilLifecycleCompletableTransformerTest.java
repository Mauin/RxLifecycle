package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilLifecycleCompletableTransformerTest {

    PublishSubject<Object> subject;
    Completable completable;
    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        subject = PublishSubject.create();
        completable = Completable.fromObservable(subject);
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void noEvent() {
        testSubscriber = (TestSubscriber<String>) completable
                .compose(new UntilLifecycleCompletableTransformer<>(lifecycle))
                .subscribe();

        subject.onComplete();

        testSubscriber.assertComplete();
    }

    @Test
    public void oneEvent() {
        testSubscriber = (TestSubscriber<String>) completable
                .compose(new UntilLifecycleCompletableTransformer<>(lifecycle))
                .subscribe();

        lifecycle.onNext("stop");
        subject.onComplete();

        testSubscriber.assertError(CancellationException.class);
    }
}