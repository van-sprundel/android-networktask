package com.ramones156.networktask;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkTask implements Callable<NetworkResult> {
    private final String TAG = NetworkTask.class.getSimpleName();
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final StringBuilder url;
    private final HashMap<String, String> headers;
    private final HashMap<String, String> parameters;
    private final HashMap<String, String> formData;

    private final RequestMethod requestMethod;

    public NetworkTask(RequestMethod requestMethod, String url) {
        this.requestMethod = requestMethod;
        this.url = new StringBuilder(url);
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.formData = new HashMap<>();
    }

    //Add headers, parameters or HTTP form data
    public void addHeader(String title, String value) {
        if (this.headers.containsKey(title))
            this.headers.put(title, this.headers.get(title) + "+" + value);
        else this.headers.put(title, value);
    }

    public void addParameter(String title, String value) {
        if (this.parameters.containsKey(title))
            this.parameters.put(title, this.parameters.get(title) + "+" + value);
        else this.parameters.put(title, value);
    }

    public void addFormData(String title, String value) {
        if (this.formData.containsKey(title))
            this.formData.put(title, this.formData.get(title) + "+" + value);
        else this.formData.put(title, value);
    }


    @Override
    public NetworkResult call() {
        if (parameters.size() > 0) {
            parameters.forEach((k, v) -> {
                try {
                    url.append("&").append(URLEncoder.encode(k, "UTF-8")).append("=").append(v);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Failed to encode URL data");
                    e.printStackTrace();
                }
            });
        }

        StringBuilder form_data = new StringBuilder();
        if (formData.size() > 0) {
            formData.forEach((k, v) -> {
                try {
                    form_data.append("&").append(URLEncoder.encode(k, "UTF-8")).append("=").append(v);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Failed to URLEncode data");
                    e.printStackTrace();
                }
            });
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url.toString().replaceFirst("&", "?")).openConnection();
            Log.d(TAG, requestMethod.get() + " to url: ( " + connection.getURL().toString() + " )");
            //Add headers to connection
            headers.forEach(connection::addRequestProperty);
            connection.setRequestMethod(requestMethod.get());

            //Write request if needed
            if (formData.size() > 0) {
                byte[] postData = new NetworkResult(form_data.toString()).getData();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.addRequestProperty("charset", "utf-8");
                connection.addRequestProperty("Content-Length", Integer.toString(postData.length));
//                connection.setUseCaches(false);

                //Write data using OutputStream
                try (DataOutputStream ws = new DataOutputStream(connection.getOutputStream())) {
                    ws.write(postData);
                }
            }

            //Read response
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            //Read response
            Log.d(TAG, "Reading response: " + connection.getResponseCode());
            Log.d(TAG, "Response message: " + connection.getResponseMessage());

            buffer.flush();

            //Create and return new BinaryData object. See BinaryData.java
            return new NetworkResult(buffer.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to get data: " + e.getMessage());
        }
        return null;
    }

    public interface Callback<BinaryData> {
        void onComplete(BinaryData result, boolean success);
    }

    public <networkResult> void executeAsync(Callable<networkResult> callable, Callback<networkResult> callback) {
        executor.execute(() -> {
            try {
                final networkResult result = callable.call();
                if (result != null) {
                    Log.d(TAG, result.toString());
                    handler.post(() -> callback.onComplete(result, true));
                } else {
                    handler.post(() -> callback.onComplete(null, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}