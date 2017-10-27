package com.sonos.abaker.android_sample.control.response;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * Created by alan.baker on 10/27/17.
 */

public class ResponseFilter implements Predicate<BaseResponse> {
    Class aClass;

    public ResponseFilter(Class responseClass) {
        this.aClass = responseClass;
    }

    @Override
    public boolean test(@NonNull BaseResponse response) throws Exception {
        return aClass.isInstance(response);
    }
}
