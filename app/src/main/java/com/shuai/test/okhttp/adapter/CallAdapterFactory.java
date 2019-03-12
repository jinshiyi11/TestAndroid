package com.shuai.test.okhttp.adapter;

import com.shuai.test.okhttp.annotation.EnableCache;
import com.shuai.test.okhttp.data.CachePolicy;
import com.shuai.test.okhttp.data.Const;
import com.shuai.test.okhttp.util.ReflectUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.Result;
import rx.Completable;
import rx.Observable;
import rx.Producer;
import rx.Scheduler;
import rx.Single;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class CallAdapterFactory extends CallAdapter.Factory {

    private final Scheduler scheduler;

    private final Scheduler observeOnScheduler;


    public static CallAdapterFactory create() {
        return new CallAdapterFactory(null, null);
    }

    /**
     * Returns an instance which creates synchronous observables that
     * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
     */
    public static CallAdapterFactory createWithScheduler(Scheduler subscribeOnScheduler, Scheduler observeOnSchedulers) {
        return new CallAdapterFactory(subscribeOnScheduler, observeOnSchedulers);
    }

    public CallAdapterFactory(Scheduler scheduler, Scheduler observeOnScheduler) {
        this.scheduler = scheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        String canonicalName = rawType.getCanonicalName();
        boolean isSingle = "rx.Single".equals(canonicalName);
        boolean isCompletable = "rx.Completable".equals(canonicalName);
        if (rawType != Observable.class && !isSingle && !isCompletable) {
            return null;
        }
        if (!isCompletable && !(returnType instanceof ParameterizedType)) {
            String name = isSingle ? "Single" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        if (isCompletable) {
            // Add Completable-converter wrapper from a separate class. This defers classloading such that
            // regular Observable operation can be leveraged without relying on this unstable RxJava API.
            // Note that this has to be done separately since Completable doesn't have a parametrized
            // type.
            return CompletableHelper.createCallAdapter(scheduler);
        }

        CallAdapter<Observable<?>> callAdapter = getCallAdapter(returnType, scheduler, observeOnScheduler, annotations);
        if (isSingle) {
            // Add Single-converter wrapper from a separate class. This defers classloading such that
            // regular Observable operation can be leveraged without relying on this unstable RxJava API.
            return SingleHelper.makeSingle(callAdapter);
        }
        return callAdapter;
    }

    private CallAdapter<Observable<?>> getCallAdapter(Type returnType, Scheduler scheduler, Scheduler observeOnScheduler, Annotation[] annotations) {
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            Type responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            return new ResponseCallAdapter(responseType, scheduler);
        }

        if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized"
                        + " as Result<Foo> or Result<? extends Foo>");
            }
            Type responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            return new ResultCallAdapter(responseType, scheduler);
        }

        CachePolicy cachePolicy = getCachePolicyFromAnnotation(annotations);
        return new SimpleCallAdapter(observableType, scheduler, observeOnScheduler, cachePolicy);
    }

    private CachePolicy getCachePolicyFromAnnotation(Annotation[] annotations) {
        CachePolicy cachePolicy = null;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof EnableCache) {
                    EnableCache enableCache = (EnableCache) annotation;
                    cachePolicy = new CachePolicy();
                    cachePolicy.setExcludeKeys(enableCache.excludeKeys());
                    cachePolicy.setOnlyUseCache(enableCache.onlyUseCache());
                    cachePolicy.setUseBeforeRequest(enableCache.useBeforeRequest());
                    cachePolicy.setUseAfterRequest(enableCache.useAfterRequest());
                    cachePolicy.setExpireTime(enableCache.expireTime());
                    break;
                }
            }
        }
        return cachePolicy;
    }

    static final class CallOnSubscribe<T> implements Observable.OnSubscribe<Response<T>> {
        private final Call<T> originalCall;

        CallOnSubscribe(Call<T> originalCall) {
            this.originalCall = originalCall;
        }

        @Override
        public void call(final Subscriber<? super Response<T>> subscriber) {
            // Since Call is a one-shot type, clone it for each new subscriber.
            Call<T> call = originalCall.clone();
            if(originalCall.request().tag() instanceof CachePolicy) {
                ReflectUtil.setRequestTag(call.request(), (CachePolicy) originalCall.request().tag());
            }

            // Wrap the call in a helper which handles both unsubscription and backpressure.
            RequestArbiter<T> requestArbiter = new RequestArbiter<>(call, subscriber);
            subscriber.add(requestArbiter);
            subscriber.setProducer(requestArbiter);
        }
    }

    static final class RequestArbiter<T> extends AtomicBoolean implements Subscription, Producer {
        private final Call<T> call;
        private final Subscriber<? super Response<T>> subscriber;

        RequestArbiter(Call<T> call, Subscriber<? super Response<T>> subscriber) {
            this.call = call;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n < 0: " + n);
            }
            if (n == 0) {
                return; // Nothing to do when requesting 0.
            }
            if (!compareAndSet(false, true)) {
                return; // Request was already triggered.
            }

            try {
                Response<T> response = call.execute();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(response);
                }
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(t);
                }
                return;
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void unsubscribe() {
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }

    static final class ResponseCallAdapter implements CallAdapter<Observable<?>> {
        private final Type responseType;
        private final Scheduler scheduler;

        ResponseCallAdapter(Type responseType, Scheduler scheduler) {
            this.responseType = responseType;
            this.scheduler = scheduler;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> Observable<Response<R>> adapt(Call<R> call) {
            Observable<Response<R>> observable = Observable.create(new CallOnSubscribe<>(call));
            if (scheduler != null) {
                return observable.subscribeOn(scheduler);
            }
            return observable;
        }
    }

    static class CallWrapper<T> implements Call<T>{
        private Call<T> mOriginalCall;
        private Request mNewRequest;

        public CallWrapper(Call<T> call, Request newRequest) {
            this.mOriginalCall = call;
            this.mNewRequest = newRequest;
        }

        @Override
        public Response<T> execute() throws IOException {
            return mOriginalCall.execute();
        }

        @Override
        public void enqueue(Callback<T> callback) {
            mOriginalCall.enqueue(callback);
        }

        @Override
        public boolean isExecuted() {
            return mOriginalCall.isExecuted();
        }

        @Override
        public void cancel() {
            mOriginalCall.cancel();
        }

        @Override
        public boolean isCanceled() {
            return mOriginalCall.isCanceled();
        }

        @Override
        public Call<T> clone() {
            return new CallWrapper(mOriginalCall.clone(),mNewRequest);
        }

        @Override
        public Request request() {
            return mNewRequest;
        }
    }

    static final class SimpleCallAdapter implements CallAdapter<Observable<?>> {
        private final Type responseType;
        private final Scheduler scheduler;
        private final Scheduler observeOnScheduler;
        //缓存策略
        private final CachePolicy cachePolicy;

        SimpleCallAdapter(Type responseType, Scheduler scheduler, Scheduler observeOnScheduler, CachePolicy cachePolicy) {
            this.responseType = responseType;
            this.scheduler = scheduler;
            this.observeOnScheduler = observeOnScheduler;
            this.cachePolicy = cachePolicy;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> Observable<R> adapt(Call<R> call) {
            if (cachePolicy != null) {
                Request request = call.request();//.newBuilder().addHeader(Const.HEAD_CACHE_POLICY, cachePolicy.toString()).build();
                ReflectUtil.setRequestTag(request,cachePolicy);
                //call = new CallWrapper<>(call, newRequest);
            }

            Observable<R> observable = Observable.create(new CallOnSubscribe<>(call))
                    .lift(OperatorMapResponseToBodyOrError.<R>instance());
            if (scheduler != null) {
                observable = observable.subscribeOn(scheduler);
            }
            if (observeOnScheduler != null) {
                observable = observable.observeOn(observeOnScheduler);
            }
            return observable;
        }
    }

    static final class ResultCallAdapter implements CallAdapter<Observable<?>> {
        private final Type responseType;
        private final Scheduler scheduler;

        ResultCallAdapter(Type responseType, Scheduler scheduler) {
            this.responseType = responseType;
            this.scheduler = scheduler;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> Observable<Result<R>> adapt(Call<R> call) {
            Observable<Result<R>> observable = Observable.create(new CallOnSubscribe<>(call)) //
                    .map(new Func1<Response<R>, Result<R>>() {
                        @Override
                        public Result<R> call(Response<R> response) {
                            return Result.response(response);
                        }
                    }).onErrorReturn(new Func1<Throwable, Result<R>>() {
                        @Override
                        public Result<R> call(Throwable throwable) {
                            return Result.error(throwable);
                        }
                    });
            if (scheduler != null) {
                return observable.subscribeOn(scheduler);
            }
            return observable;
        }
    }

    static final class OperatorMapResponseToBodyOrError<T> implements Observable.Operator<T, Response<T>> {
        static private final OperatorMapResponseToBodyOrError<Object> INSTANCE =
                new OperatorMapResponseToBodyOrError<>();

        @SuppressWarnings("unchecked") // Safe because of erasure.
        static <R> OperatorMapResponseToBodyOrError<R> instance() {
            return (OperatorMapResponseToBodyOrError<R>) INSTANCE;
        }

        @Override
        public Subscriber<? super Response<T>> call(final Subscriber<? super T> child) {
            return new Subscriber<Response<T>>(child) {
                @Override
                public void onNext(Response<T> response) {
                    if (response.isSuccessful()) {
                        child.onNext(response.body());
                    } else {
                        child.onError(new HttpException(response));
                    }
                }

                @Override
                public void onCompleted() {
                    child.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
            };
        }
    }

    static final class SingleHelper {
        static CallAdapter<Single<?>> makeSingle(final CallAdapter<Observable<?>> callAdapter) {
            return new CallAdapter<Single<?>>() {
                @Override
                public Type responseType() {
                    return callAdapter.responseType();
                }

                @Override
                public <R> Single<?> adapt(Call<R> call) {
                    Observable<?> observable = callAdapter.adapt(call);
                    return observable.toSingle();
                }
            };
        }
    }

    static final class CompletableHelper {
        static CallAdapter<Completable> createCallAdapter(Scheduler scheduler) {
            return new CompletableHelper.CompletableCallAdapter(scheduler);
        }

        private static final class CompletableCallOnSubscribe implements Completable.CompletableOnSubscribe {
            private final Call originalCall;

            CompletableCallOnSubscribe(Call originalCall) {
                this.originalCall = originalCall;
            }

            @Override
            public void call(Completable.CompletableSubscriber subscriber) {
                // Since Call is a one-shot type, clone it for each new subscriber.
                final Call call = originalCall.clone();

                // Attempt to cancel the call if it is still in-flight on unsubscription.
                Subscription subscription = Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        call.cancel();
                    }
                });
                subscriber.onSubscribe(subscription);

                try {
                    Response response = call.execute();
                    if (!subscription.isUnsubscribed()) {
                        if (response.isSuccessful()) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new HttpException(response));
                        }
                    }
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    if (!subscription.isUnsubscribed()) {
                        subscriber.onError(t);
                    }
                }
            }
        }

        static class CompletableCallAdapter implements CallAdapter<Completable> {
            private final Scheduler scheduler;

            CompletableCallAdapter(Scheduler scheduler) {
                this.scheduler = scheduler;
            }

            @Override
            public Type responseType() {
                return Void.class;
            }

            @Override
            public Completable adapt(Call call) {
                Completable completable = Completable.create(new CompletableHelper.CompletableCallOnSubscribe(call));
                if (scheduler != null) {
                    return completable.subscribeOn(scheduler);
                }
                return completable;
            }
        }
    }


}