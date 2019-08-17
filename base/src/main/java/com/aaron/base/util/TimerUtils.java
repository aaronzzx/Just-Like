package com.aaron.base.util;

import android.util.Log;
import androidx.lifecycle.LifecycleOwner;
import com.aaron.base.impl.ObserverImpl;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 计时器
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class TimerUtils {

    private static final String TAG = "TimerUtils";

    private Disposable mDisp;
    private Listener mListener;

    public static void start(long millisDelay, LifecycleOwner lifecycle, OnFinishListener listener) {
        Observable.intervalRange(0L, 1L, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                .subscribe(new ObserverImpl<Long>() {
                    @Override
                    public void onComplete() {
                        listener.onFinish();
                    }
                });
    }

    public static void start(long seconds, long millisDelay, LifecycleOwner lifecycle, Listener listener) {
        Observable.intervalRange(0L, seconds + 1L, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        listener.onStart();
                    }

                    @Override
                    public void onNext(Long curProgress) {
                        listener.onProgress(curProgress);
                    }

                    @Override
                    public void onComplete() {
                        listener.onFinish();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, Objects.requireNonNull(throwable.getMessage()));
                    }
                });
    }

    public TimerUtils() {

    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void start(long seconds, long millisDelay) {
        Observable.intervalRange(0L, seconds + 1L, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisp = d;
                        if (mListener != null) mListener.onStart();
                    }

                    @Override
                    public void onNext(Long curProgress) {
                        if (mListener != null) mListener.onProgress(curProgress);
                    }

                    @Override
                    public void onComplete() {
                        if (mListener != null) mListener.onFinish();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, Objects.requireNonNull(throwable.getMessage()));
                    }
                });
    }

    public void start(long seconds, long millisDelay, LifecycleOwner lifecycle) {
        Observable.intervalRange(0L, seconds + 1L, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mListener != null) mListener.onStart();
                    }

                    @Override
                    public void onNext(Long curProgress) {
                        if (mListener != null) mListener.onProgress(curProgress);
                    }

                    @Override
                    public void onComplete() {
                        if (mListener != null) mListener.onFinish();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, Objects.requireNonNull(throwable.getMessage()));
                    }
                });
    }

    public void cancel() {
        if (mDisp != null && !mDisp.isDisposed()) {
            mDisp.dispose();
        }
    }

    public static class CountDownUtil {
        private Disposable disp;
        private Listener listener;

        public CountDownUtil() {

        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        public void start(long seconds, long millisDelay) {
            Observable.intervalRange(1L, seconds, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disp = d;
                            if (listener != null) listener.onStart();
                        }

                        @Override
                        public void onNext(Long curProgress) {
                            if (listener != null) listener.onProgress(seconds - curProgress);
                        }

                        @Override
                        public void onComplete() {
                            if (listener != null) listener.onFinish();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.d(TAG, Objects.requireNonNull(throwable.getMessage()));
                        }
                    });
        }

        public void start(long seconds, long millisDelay, LifecycleOwner lifecycle) {
            Observable.intervalRange(1L, seconds, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disp = d;
                            if (listener != null) listener.onStart();
                        }

                        @Override
                        public void onNext(Long curProgress) {
                            if (listener != null) listener.onProgress(seconds - curProgress);
                        }

                        @Override
                        public void onComplete() {
                            if (listener != null) listener.onFinish();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.d(TAG, Objects.requireNonNull(throwable.getMessage()));
                        }
                    });
        }

        public void start(long seconds, long millisDelay, LifecycleOwner lifecycle, Listener listener) {
            Observable.intervalRange(1L, seconds, millisDelay, 1000L, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            listener.onStart();
                        }

                        @Override
                        public void onNext(Long curProgress) {
                            listener.onProgress(seconds - curProgress);
                        }

                        @Override
                        public void onComplete() {
                            listener.onFinish();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e(TAG, Objects.requireNonNull(throwable.getMessage()));
                        }
                    });
        }

        public void cancel() {
            if (disp != null && !disp.isDisposed()) {
                disp.dispose();
            }
        }
    }

    public static class SimpleListener implements Listener {
        @Override
        public void onStart() {

        }

        @Override
        public void onProgress(long curProgress) {

        }

        @Override
        public void onFinish() {

        }
    }

    public interface Listener {
        void onStart();

        void onProgress(long curProgress);

        void onFinish();
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
