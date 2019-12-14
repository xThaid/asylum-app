package com.thaid.asylum.api.requests.Blinds;

import com.thaid.asylum.Blinds.BlindsFragment;
import com.thaid.asylum.api.APINoDataOnResponseRequest;


public class BlindActionRequest extends APINoDataOnResponseRequest {

    private static final String API_ENDPOINT = "blindAction";

    public BlindActionRequest(BlindsFragment.BlindAction blindAction) {
        super(API_ENDPOINT + "/" + blindAction.getBlind().getId() + "/" + blindAction.getAction().getId());
    }
}
