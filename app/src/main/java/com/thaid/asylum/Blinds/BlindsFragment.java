package com.thaid.asylum.Blinds;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.thaid.asylum.MainActivity;
import com.thaid.asylum.R;
import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.BlindActionRequest;
import com.thaid.asylum.api.requests.BlindAllActionRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlindsFragment extends Fragment {

    public static final Blind[] BLINDS = {
            new Blind(-1, R.string.blind_all),
            new Blind(0, R.string.blind_name_0),
            new Blind(1, R.string.blind_name_1),
            new Blind(2, R.string.blind_name_2),
            new Blind(3, R.string.blind_name_3),
            new Blind(4, R.string.blind_name_4),
            new Blind(5, R.string.blind_name_5),
            new Blind(6, R.string.blind_name_6),
            new Blind(7, R.string.blind_name_7),
            new Blind(8, R.string.blind_name_8)
    };

    public static final Action ACTION0 = new Action(0, R.string.action_name_0);
    public static final Action ACTION1 = new Action(1, R.string.action_name_1);
    public static final Action ACTION2 = new Action(2, R.string.action_name_2);

    View root_view;

    public BlindsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       root_view =  inflater.inflate(R.layout.fragment_blinds, container, false);

        ListView blindsList = root_view.findViewById(R.id.listView);
        BlindsAdapter blindsAdapter = new BlindsAdapter(this,root_view.getContext(), BlindsFragment.BLINDS);
        blindsList.setAdapter(blindsAdapter);

        return root_view;
    }

    public void setButtonListeners(FloatingActionButton buttonOpen,
                                   FloatingActionButton buttonClose,
                                   FloatingActionButton buttonStop,
                                   final int i,
                                   boolean allBlinds){


        if(!allBlinds){
            buttonOpen.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeAction(new BlindAction(BLINDS[i], ACTION0));
                }
            });
            buttonClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeAction(new BlindAction(BLINDS[i], ACTION1));
                }
            });
            buttonStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeAction(new BlindAction(BLINDS[i], ACTION2));
                }
            });
        }else{
            buttonOpen.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeActionForAllBlinds(ACTION0);
                }
            });
            buttonClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeActionForAllBlinds(ACTION1);
                }
            });
            buttonStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    executeActionForAllBlinds(ACTION2);
                }
            });
        }

    }

    private void executeAction(final BlindAction blindAction){
        APIClient apiClient = APIClient.getInstance();

        apiClient.sendRequest(new BlindActionRequest(blindAction),
                new ResponseListener<BlindActionRequest.NoDataOnResponseModel>() {
            @Override
            public void onSuccess(BlindActionRequest.NoDataOnResponseModel data) {
                Snackbar snackbar = Snackbar.make(
                        root_view,
                        "Wykonano zadanie: " + getString(blindAction.getAction().getNameId()) + " " + getString(blindAction.getBlind().getNameId()),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onError(APIError error) {
                Snackbar snackbar = Snackbar
                        .make(root_view, getString(error.getTranslationId()) + " " + error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void executeActionForAllBlinds(final Action action){
        APIClient apiClient = APIClient.getInstance();

        apiClient.sendRequest(new BlindAllActionRequest(action),
                new ResponseListener<BlindActionRequest.NoDataOnResponseModel>() {
                    @Override
                    public void onSuccess(BlindActionRequest.NoDataOnResponseModel data) {
                        Snackbar snackbar = Snackbar.make(
                                root_view,
                                "Wykonano zadanie: " + getString(action.getNameId()) + " " + getString(R.string.blind_all),
                                Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onError(APIError error) {
                        Snackbar snackbar = Snackbar
                                .make(root_view, getString(error.getTranslationId()), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }


    public static class Blind{
        private int id;
        private int nameId;


        public Blind(int id, int nameId){
            this.id = id;
            this.nameId = nameId;
        }
        public int getId(){
            return id;
        }
        public int getNameId(){
            return nameId;
        }
    }

    public static class Action{
        private int id;
        private int nameId;

        public Action(int id, int nameId){
            this.id = id;
            this.nameId = nameId;
        }
        public int getId(){
            return id;
        }
        public int getNameId(){
            return nameId;
        }
    }

    public class BlindAction{
        private Blind blind;
        private Action action;

        public BlindAction(Blind blind, Action action){
            this.blind = blind;
            this.action = action;
        }
        public Blind getBlind(){
            return blind;
        }
        public Action getAction(){
            return action;
        }
    }
}
