package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilEventSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        testSubscriber.request(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        lifecycle.onNext("keep going");
        testSubscriber.request(1);

        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void twoEvents() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilEventSingleTransformer<String, String>(lifecycle, "stop"))
                .subscribe();

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");
        testSubscriber.request(1);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }

}