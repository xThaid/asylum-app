package com.thaid.asylum.api.requests;

import com.thaid.asylum.api.APIRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUserInfoRequest extends APIRequest<GetUserInfoRequest.GetUserInfoModel> {

    private static final String API_ENDPOINT = "getUserInfo";

    public GetUserInfoRequest() {
        super(API_ENDPOINT);
    }

    @Override
    public GetUserInfoModel parseResponse(JSONObject json) throws JSONException {
        int id = json.getInt("id");
        String name = json.getString("name");
        String role = json.getString("role");

        return new GetUserInfoModel(id, name, role);
    }

    public class GetUserInfoModel {
        private final int id;
        private final String name;
        private final String role;

        private GetUserInfoModel(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }

}
