package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilCorrespondingEventSingleTransformerTest {

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        Single.just("1")
                .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        testSubscriber.request(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        lifecycle.onNext("create");
        testSubscriber.request(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        testSubscriber.request(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        testSubscriber = (TestSubscriber<String>) Single.just("1")
                .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        lifecycle.onNext("create");
        lifecycle.onNext("destroy");
        testSubscriber.request(1);
        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
    }
}