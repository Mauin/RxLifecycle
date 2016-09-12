package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import io.reactivex.Completable;
import io.reactivex.functions.Function;
import io.reactivex.internal.subscribers.completable.SubscriberCompletableObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilCorrespondingEventCompletableTransformerTest {

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
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
    public void noEvents() {
        completable
                .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe(new SubscriberCompletableObserver<>(testSubscriber));

        subject.onComplete();
        testSubscriber.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        completable
                .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe(new SubscriberCompletableObserver<>(testSubscriber));

        lifecycle.onNext("create");
        subject.onComplete();
        testSubscriber.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        completable
                .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe(new SubscriberCompletableObserver<>(testSubscriber));

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        subject.onComplete();
        testSubscriber.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        completable
                .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe(new SubscriberCompletableObserver<>(testSubscriber));

        lifecycle.onNext("create");
        lifecycle.onNext("destroy");
        subject.onComplete();
        testSubscriber.assertError(CancellationException.class);
    }
}