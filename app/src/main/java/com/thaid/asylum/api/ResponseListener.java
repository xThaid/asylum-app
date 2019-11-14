package com.thaid.asylum.api;

public interface ResponseListener<T> {

    void onSuccess(T data);
    void onError(APIError error);
}
