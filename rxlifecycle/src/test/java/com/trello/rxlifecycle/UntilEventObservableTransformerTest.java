package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilEventObservableTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        testSubscriber = (TestSubscriber<String>) Observable.just("1", "2", "3")
                .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        testSubscriber.request(2);
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneWrongEvent() {
        testSubscriber = (TestSubscriber<String>) Observable.just("1", "2", "3")
                .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        testSubscriber.request(1);
        lifecycle.onNext("keep going");
        testSubscriber.request(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void twoEvents() {
        testSubscriber = (TestSubscriber<String>) Observable.just("1", "2", "3")
                .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        testSubscriber.request(1);
        lifecycle.onNext("keep going");
        testSubscriber.request(1);
        lifecycle.onNext("stop");
        testSubscriber.request(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertComplete();
    }

}