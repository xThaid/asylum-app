package com.thaid.asylum.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class APIRequest<T> {

    private final String APIEndpoint;
    protected JSONObject data;

    public APIRequest(String APIEndpoint, JSONObject data) {
        this.APIEndpoint = APIEndpoint;
        this.data = data;
    }

    public APIRequest(String APIEndpoint) {
        this(APIEndpoint, new JSONObject());
    }

    public String getAPIEndpoint() {
        return APIEndpoint;
    }

    public JSONObject getData() {
        return data;
    }

    public abstract T parseResponse(JSONObject json) throws JSONException;
}
