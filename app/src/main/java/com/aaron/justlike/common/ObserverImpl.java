package com.aaron.justlike.common;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ObserverImpl<T> implements Observer<T> {

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }
}
