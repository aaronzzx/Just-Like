package com.aaron.base.impl;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * RxJava Observer 的空实现
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class ObserverImpl<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
