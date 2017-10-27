package com.sonos.abaker.android_sample.control.response;

import android.util.Log;

import com.sonos.abaker.android_sample.ControlActivity;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Base Observer class for Responses, to provide default implementation
 * to reduce boilerplate.  override what you need
 *
 *
 * Created by alan.baker on 10/27/17.
 */

public class ResponseObserver implements Observer<BaseResponse> {
    private static final String LOG_TAG = ResponseObserver.class.getSimpleName();

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull BaseResponse response) {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.e(LOG_TAG, "Error", e);
    }

    @Override
    public void onComplete() {

    }
}
