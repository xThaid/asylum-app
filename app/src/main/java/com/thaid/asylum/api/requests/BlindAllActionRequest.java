package com.thaid.asylum.api.requests;

import com.thaid.asylum.Blinds.BlindsFragment;
import com.thaid.asylum.api.APINoDataOnResponseRequest;


public class BlindAllActionRequest extends APINoDataOnResponseRequest {

    private static final String API_ENDPOINT = "blindAction/all";

    public BlindAllActionRequest(BlindsFragment.Action action) {
        super(API_ENDPOINT + "/" + action.getId());
    }
}
