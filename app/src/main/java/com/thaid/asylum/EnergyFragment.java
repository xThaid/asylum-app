package com.thaid.asylum;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.GetCurrentPowerDataRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnergyFragment extends Fragment {


    public EnergyFragment() {
    }

    private View rootView;
    private static Handler handler;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final Runnable thisRunnable = this;
            APIClient apiClient = APIClient.getInstance();
            apiClient.sendRequest(new GetCurrentPowerDataRequest(),
                    new ResponseListener<GetCurrentPowerDataRequest.GetCurrentPowerDataModel>() {
                        @Override
                        public void onSuccess(GetCurrentPowerDataRequest.GetCurrentPowerDataModel data) {
                            powerProductionNowTextView.setText(data.getProduction() + "");
                            powerConsumptionNowTextView.setText(data.getConsumption() + "");
                            powerUseNowTextView.setText(data.getUse() + "");
                            powerImportNowTextView.setText(data.getImport_() + "");
                            powerExportNowTextView.setText(data.getExport() + "");
                            powerStoreNowTextView.setText(data.getStore() + "");
                            handler.postDelayed(thisRunnable, 1000);
                        }

                        @Override
                        public void onError(APIError error) {
                            if (error.getType() != APIError.INTERNAL_SERVER_ERROR && isAdded()) {
                                Snackbar snackbar = Snackbar
                                        .make(rootView, getString(error.getTranslationId()) + " " + error.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                            handler.postDelayed(thisRunnable, 5000);
                        }
                    });
        }
    };

    TextView powerProductionNowTextView;
    TextView powerConsumptionNowTextView;
    TextView powerUseNowTextView;
    TextView powerImportNowTextView;
    TextView powerExportNowTextView;
    TextView powerStoreNowTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_energy, container, false);


        powerProductionNowTextView = rootView.findViewById(R.id.card_value6);
        powerConsumptionNowTextView = rootView.findViewById(R.id.card_value8);
        powerUseNowTextView = rootView.findViewById(R.id.card_value7);
        powerImportNowTextView = rootView.findViewById(R.id.card_value);
        powerExportNowTextView = rootView.findViewById(R.id.card_value2);
        powerStoreNowTextView = rootView.findViewById(R.id.card_value1);


        handler = new Handler();

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}
