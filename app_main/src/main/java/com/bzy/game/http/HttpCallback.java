package com.bzy.game.http;

public interface HttpCallback {
    void onResponse(Response response);

    void onError(String msg, Exception e);
}
