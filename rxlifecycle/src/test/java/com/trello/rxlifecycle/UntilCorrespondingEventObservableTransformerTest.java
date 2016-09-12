package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.subscribers.TestSubscriber.create;

public class UntilCorrespondingEventObservableTransformerTest {

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
        testSubscriber = create(0);
    }

    @Test
    public void noEvents() {
        TestObserver testObserver = Observable.just("1", "2", "3")
                .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .test();

        testObserver.request(2);
        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<String> testObserver = new TestObserver<>();
        Observable.just("1", "2", "3")
                .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe(testObserver);


        lifecycle.onNext("create");
//        testObserver.request(2);

        testObserver.assertValues("1", "2");
        testObserver.assertNotTerminated();
    }

    @Test
    public void twoOpenEvents() {
        testSubscriber = (TestSubscriber<String>) Observable.just("1", "2", "3")
                .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        lifecycle.onNext("create");
        testSubscriber.request(1);
        lifecycle.onNext("start");
        testSubscriber.request(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void openAndCloseEvent() {
        testSubscriber = (TestSubscriber<String>) Observable.just("1", "2", "3")
                .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
                .subscribe();

        lifecycle.onNext("create");
        testSubscriber.request(1);
        lifecycle.onNext("destroy");
        testSubscriber.request(1);

        testSubscriber.assertValues("1");
        testSubscriber.assertComplete();
    }

}