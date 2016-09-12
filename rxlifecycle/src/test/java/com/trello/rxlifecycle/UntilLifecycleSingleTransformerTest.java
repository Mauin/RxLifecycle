package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilLifecycleSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvent() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilLifecycleSingleTransformer<String, String>(lifecycle))
                .subscribe();

        testSubscriber.request(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void oneEvent() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilLifecycleSingleTransformer<String, String>(lifecycle))
                .subscribe();

        lifecycle.onNext("stop");
        testSubscriber.request(1);

        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }
}