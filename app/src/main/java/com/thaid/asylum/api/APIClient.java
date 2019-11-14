package com.thaid.asylum.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thaid.asylum.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIClient {

    private static final String API_ENDPOINT = "/api/";

    private static APIClient instance;

    private final SharedPreferences preferences;
    private final ConnectivityManager connectivityManager;
    private final RequestQueue requestQueue;

    private APIClient(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized APIClient getInstance(Context context) {
        if (instance == null) {
            instance = new APIClient(context.getApplicationContext());
        }
        return instance;
    }

    public <T> void sendRequest(final APIRequest<T> request, final ResponseListener<T> responseListener) {
        if(!isNetworkAvailable()) {
            responseListener.onError(new APIError(APIError.NOT_CONNECTED));
            return;
        }

        PreparedRequest prepared = new PreparedRequest(
                getAPIURL().concat(request.getAPIEndpoint()),
                getAPIKey(),
                request.getData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            responseListener.onSuccess(request.parseResponse(response));
                        } catch (JSONException e) {
                            responseListener.onError(new APIError(APIError.PARSE_ERROR));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        APIError apiError;
                        if(error.networkResponse == null) {
                            apiError = new APIError(APIError.NETWORK_ERROR, error.getMessage());
                        } else if(error.networkResponse.statusCode == 401) {
                            apiError = new APIError(APIError.AUTHORIZATION_FAILED);
                        } else {
                            apiError = new APIError(APIError.UNKNOWN_ERROR, error.getMessage());
                        }
                        responseListener.onError(apiError);
                    }
                });
        prepared.setShouldCache(false);
        requestQueue.add(prepared);
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getAPIKey() {
        return preferences.getString(SettingsActivity.KEY_API_KEY, "");
    }

    private String getAPIURL() {
        return getHostURL() + API_ENDPOINT;
    }

    private String getHostURL() {
        return "https://asylum.zapto.org";
    }

    private class PreparedRequest extends JsonObjectRequest {

        private final String APIKey;

        private PreparedRequest(String url, String APIKey,
                                @Nullable JSONObject jsonRequest,
                                Response.Listener<JSONObject> listener,
                                @Nullable Response.ErrorListener errorListener) {
            super(Method.POST, url, jsonRequest, listener, errorListener);
            this.APIKey = APIKey;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<>();
            params.put("X-API-KEY", APIKey);
            return params;
        }
    }
}
