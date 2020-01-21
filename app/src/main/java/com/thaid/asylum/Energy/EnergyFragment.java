package com.thaid.asylum.Energy;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.thaid.asylum.MainActivity;
import com.thaid.asylum.R;
import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.Energy.GetCurrentPowerDataRequest;
import com.thaid.asylum.api.requests.Energy.GetHistoryEnergyDataRequest;

import org.joda.time.LocalDate;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnergyFragment extends Fragment {

    Fragment thisFragment;
    EnergyHistoryFragment energyHistoryFragment;
    FragmentManager fragmentManager;
    MainActivity main;
    boolean isRequestRunning = false;

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
                            powerProductionNowTextView.setText(String.valueOf(data.getProduction()));
                            powerConsumptionNowTextView.setText(String.valueOf(data.getConsumption()));
                            powerUseNowTextView.setText(String.valueOf(data.getUse()));
                            powerImportNowTextView.setText(String.valueOf(data.getImport_()));
                            powerExportNowTextView.setText(String.valueOf(data.getExport()));
                            powerStoreNowTextView.setText(String.valueOf(data.getStore()));
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

    Button dayHistoryButton;
    Button monthHistoryButton;
    Button yearHistoryButton;
    Button otherHistoryButton;
    Button allHistoryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_energy, container, false);


        powerProductionNowTextView = rootView.findViewById(R.id.card_value_production);
        powerConsumptionNowTextView = rootView.findViewById(R.id.card_value_consumption);
        powerUseNowTextView = rootView.findViewById(R.id.card_value_use);
        powerImportNowTextView = rootView.findViewById(R.id.card_value_import);
        powerExportNowTextView = rootView.findViewById(R.id.card_value_export);
        powerStoreNowTextView = rootView.findViewById(R.id.card_value_store);

        main = (MainActivity) getActivity();

        dayHistoryButton = rootView.findViewById(R.id.button3);
        monthHistoryButton = rootView.findViewById(R.id.button5);
        yearHistoryButton = rootView.findViewById(R.id.button4);
        otherHistoryButton = rootView.findViewById(R.id.button7);
        allHistoryButton = rootView.findViewById(R.id.button6);

        dayHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHistoryFragment(new LocalDate(), new LocalDate(), GetHistoryEnergyDataRequest.GROUP_SPAN_MINUTES);
            }
        });

        monthHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocalDate today = new LocalDate();
                openHistoryFragment(today.withDayOfMonth(1), today, GetHistoryEnergyDataRequest.GROUP_SPAN_DAY);
            }
        });

        yearHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocalDate today = new LocalDate();
                openHistoryFragment(today.withMonthOfYear(1).withDayOfMonth(1), today, GetHistoryEnergyDataRequest.GROUP_SPAN_MONTH);
            }
        });

        allHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHistoryFragment(GetHistoryEnergyDataRequest.STARTING_DATE, new LocalDate(), GetHistoryEnergyDataRequest.GROUP_SPAN_YEAR);
            }
        });

        otherHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHistoryFragment();
            }
        });

        fragmentManager = getFragmentManager();

        thisFragment = this;

        handler = new Handler();

        return rootView;
    }

    public void setEnergyHistoryFragment(EnergyHistoryFragment energyHistoryFragment){
        this.energyHistoryFragment = energyHistoryFragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        startRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRequests();
    }
    public void stopRequests(){
        handler.removeCallbacks(runnable);
        isRequestRunning = false;
    }
    public void startRequests(){
        if(!isRequestRunning){
            handler.post(runnable);
            isRequestRunning = true;
        }
    }
    private void openHistoryFragment(LocalDate fromDate, LocalDate toDate, String groupSpan){
            energyHistoryFragment.setInitialState(fromDate, toDate, groupSpan, true);
            fragmentManager.beginTransaction().hide(thisFragment).show(energyHistoryFragment).commit();
            main.setActiveFragment(energyHistoryFragment);
            stopRequests();
    }
    private void openHistoryFragment(){
        fragmentManager.beginTransaction().hide(thisFragment).show(energyHistoryFragment).commit();
        main.setActiveFragment(energyHistoryFragment);
        stopRequests();
    }
}
