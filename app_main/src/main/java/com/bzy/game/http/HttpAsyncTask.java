package com.bzy.game.http;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Description :
 *
 * @author : rocky
 * @Create Time : 2018/12/8 3:17 PM
 * @Modified By: rocky
 * @Modified Time : 2018/12/8 3:17 PM
 */
public class HttpAsyncTask extends AsyncTask<Void, Void, Response> {

    private Request request;
    private HttpURLConnection connection;
    private HttpCallback httpCallback;
    private Exception exception;

    public HttpAsyncTask(Request request, HttpURLConnection connection, HttpCallback httpCallback) {
        this.connection = connection;
        this.request = request;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Response doInBackground(Void... params) {
        try {
            if (request != null) {
                return request.execute();
            }
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (response == null) {
            if (httpCallback != null) {
                if (exception != null) {
                    httpCallback.onError("error", exception);
                } else {
                    httpCallback.onError("error", new Exception("UnKnown Exception"));
                }
            }
        } else {
            if (httpCallback != null) {
                httpCallback.onResponse(response);
            }
        }
    }
}