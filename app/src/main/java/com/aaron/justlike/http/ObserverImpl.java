package com.aaron.justlike.http;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ObserverImpl<T> implements Observer<T> {

    @Override
    public abstract void onNext(T t);

    @Override
    public abstract void onError(Throwable e);

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }
}
