package com.thaid.asylum.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class APINoDataOnResponseRequest extends APIRequest<APINoDataOnResponseRequest.NoDataOnResponseModel>{

    public APINoDataOnResponseRequest(String APIEndpoint, JSONObject data) {
        super(APIEndpoint, data);
    }

    public APINoDataOnResponseRequest(String APIEndpoint) {
        super(APIEndpoint);
    }

    @Override
    public NoDataOnResponseModel parseResponse(JSONObject json) throws JSONException {
        String message = json.getString("msg");
        return new NoDataOnResponseModel(message);
    }

    public class NoDataOnResponseModel {
        private final String message;

        private NoDataOnResponseModel (String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
