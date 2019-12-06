package com.thaid.asylum.api.requests;

import com.thaid.asylum.api.APIRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class GetCurrentPowerDataRequest extends APIRequest {

    private static final String API_ENDPOINT = "getCurrentPowerData";

    public GetCurrentPowerDataRequest(){
        super(API_ENDPOINT);
    }
    @Override
    public Object parseResponse(JSONObject json) throws JSONException {
        int production = json.getInt("production");
        int consumption = json.getInt("consumption");
        int use = json.getInt("use");
        int import_ = json.getInt("import");
        int export = json.getInt("export");
        int store = json.getInt("store");

        return new GetCurrentPowerDataModel(production, consumption, use, import_, export, store);
    }
    public class GetCurrentPowerDataModel{

        private int production;
        private int consumption;
        private int use;
        private int import_;
        private int export;
        private int store;

        public GetCurrentPowerDataModel(int production,
                                        int consumption,
                                        int use,
                                        int import_,
                                        int export,
                                        int store){

            this.production = production;
            this.consumption = consumption;
            this.use = use;
            this.import_ = import_;
            this.export = export;
            this.store = store;
        }

        public int getProduction(){
            return production;
        }

        public int getConsumption(){
            return consumption;
        }

        public int getUse(){
            return use;
        }

        public int getImport_(){
            return import_;
        }

        public int getExport(){
            return export;
        }

        public int getStore(){
            return store;
        }
    }
}
