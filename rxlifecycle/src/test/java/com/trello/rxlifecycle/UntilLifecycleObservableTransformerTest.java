package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilLifecycleObservableTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvent() {
        testSubscriber= (TestSubscriber<String>) Observable.just("1", "2", "3")
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe();

        testSubscriber.request(2);
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneEvent() {
        testSubscriber= (TestSubscriber<String>) Observable.just("1", "2", "3")
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe();

        testSubscriber.request(1);
        lifecycle.onNext("stop");
        testSubscriber.request(1);

        testSubscriber.assertValues("1");
        testSubscriber.assertComplete();
    }
}