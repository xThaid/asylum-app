package com.thaid.asylum.Energy;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thaid.asylum.Chart.ChartData;
import com.thaid.asylum.Chart.ChartFragment;
import com.thaid.asylum.MainActivity;
import com.thaid.asylum.R;
import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.Energy.GetHistoryEnergyDataRequest;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;

public class EnergyHistoryFragment extends Fragment {

    TextView productionTextView;
    TextView consumptionTextView;
    TextView importTextView;
    TextView exportTextView;
    TextView storeTextView;
    TextView useTextView;
    ProgressBar loadingView;
    View rootView;
    Button dateFromButton;
    Button dateToButton;
    RadioGroup groupSpanRadioGroup;

    Type chartDataType = new TypeToken<HashMap<String, ChartData>>(){}.getType();
    Type energyTotalType = new TypeToken<HashMap<String, String>>(){}.getType();
    HashMap<String, ChartData> chartData;
    HashMap<String, String> energyTotal;
    LocalDate fromDate;
    LocalDate toDate;
    String groupSpan = "";

    boolean activeRequest;
    ChartFragment chartFragment;

    public EnergyHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_energy_history, container, false);

        productionTextView = rootView.findViewById(R.id.card_value_production);
        consumptionTextView = rootView.findViewById(R.id.card_value_consumption);
        importTextView = rootView.findViewById(R.id.card_value_import);
        exportTextView= rootView.findViewById(R.id.card_value_export);
        storeTextView = rootView.findViewById(R.id.card_value_store);
        useTextView = rootView.findViewById(R.id.card_value_use);
        loadingView = rootView.findViewById(R.id.progressBar);
        dateFromButton = rootView.findViewById(R.id.date_from_button);
        dateToButton = rootView.findViewById(R.id.date_to_button);
        groupSpanRadioGroup = rootView.findViewById(R.id.radioGroup);
        Button loadDataButton = rootView.findViewById(R.id.button6);

        Button chartProductionButton = rootView.findViewById(R.id.button9);
        Button chartConsumptionButton = rootView.findViewById(R.id.button10);
        Button chartImportButton = rootView.findViewById(R.id.button2);
        Button chartExportButton = rootView.findViewById(R.id.button8);
        Button chartStoreButton = rootView.findViewById(R.id.button12);
        Button chartUseButton = rootView.findViewById(R.id.button);

        activeRequest = false;

        fromDate = new LocalDate();
        toDate = new LocalDate();


        final DatePickerDialog.OnDateSetListener dateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                fromDate = dateSanitation(year, month, day);
                dateFromButton.setText(fromDate.toString(GetHistoryEnergyDataRequest.DATE_PATTERN, Locale.US));
            }
        };

        final DatePickerDialog.OnDateSetListener dateToSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toDate = dateSanitation(year, month, day);
                dateToButton.setText(toDate.toString(GetHistoryEnergyDataRequest.DATE_PATTERN, Locale.US));
            }
        };

        dateFromButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDateDialog(dateFromSetListener, fromDate);
            }
        });

        dateToButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDateDialog(dateToSetListener, toDate);
            }
        });

        loadDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int checkedRadioId = groupSpanRadioGroup.getCheckedRadioButtonId();

                if(checkedRadioId != -1 && fromDate.compareTo(toDate) <= 0){

                    switch (checkedRadioId){
                        case R.id.radioButton1:
                            if(Days.daysBetween(fromDate, toDate).getDays() < 2)
                                groupSpan = GetHistoryEnergyDataRequest.GROUP_SPAN_MINUTES;
                            else{
                                groupSpan = GetHistoryEnergyDataRequest.GROUP_SPAN_DAY;
                                ((RadioButton)groupSpanRadioGroup.getChildAt(1)).setChecked(true);
                            }
                            break;
                        case R.id.radioButton2:
                            groupSpan = GetHistoryEnergyDataRequest.GROUP_SPAN_DAY;
                            break;
                        case R.id.radioButton3:
                            groupSpan = GetHistoryEnergyDataRequest.GROUP_SPAN_MONTH;
                            break;
                        case R.id.radioButton4:
                            groupSpan = GetHistoryEnergyDataRequest.GROUP_SPAN_YEAR;
                            break;
                    }
                    loadData(fromDate, toDate, groupSpan);
                }
            }
        });

        chartProductionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("production"));
            }
        });

        chartConsumptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("consumption"));
            }
        });

        chartImportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("import"));
            }
        });

        chartExportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("export"));
            }
        });

        chartStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("store"));
            }
        });

        chartUseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChartFragment(chartData.get("use"));
            }
        });
        return rootView;
    }

    private LocalDate dateSanitation(int year, int month, int day){
        LocalDate date = new LocalDate(year, month + 1, day);
        LocalDate dateNow = new LocalDate();

        if(date.compareTo(GetHistoryEnergyDataRequest.STARTING_DATE) < 0){
            date = GetHistoryEnergyDataRequest.STARTING_DATE;
        }

        if(date.compareTo(dateNow) > 0){
            date = dateNow;
        }

        return date;
    }

    private void openDateDialog(DatePickerDialog.OnDateSetListener onDateSetListener, LocalDate startingDate){

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light,
                onDateSetListener,
                startingDate.getYear(),
                startingDate.getMonthOfYear()- 1,
                startingDate.getDayOfMonth());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.show();
    }

    public void setInitialState(LocalDate fromDate, LocalDate toDate, String groupSpan, boolean loadData){
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.groupSpan = groupSpan;
        dateFromButton.setText(fromDate.toString(GetHistoryEnergyDataRequest.DATE_PATTERN, Locale.US));
        dateToButton.setText(toDate.toString(GetHistoryEnergyDataRequest.DATE_PATTERN, Locale.US));

        switch (groupSpan){
            case GetHistoryEnergyDataRequest.GROUP_SPAN_MINUTES:
                ((RadioButton)groupSpanRadioGroup.getChildAt(0)).setChecked(true);
                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_DAY:
                ((RadioButton)groupSpanRadioGroup.getChildAt(1)).setChecked(true);
                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_MONTH:
                ((RadioButton)groupSpanRadioGroup.getChildAt(2)).setChecked(true);
                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_YEAR:
                ((RadioButton)groupSpanRadioGroup.getChildAt(3)).setChecked(true);
                break;
        }
        if(loadData)
            loadData(fromDate, toDate, groupSpan);
    }

    private void loadData(final LocalDate fromDate, final LocalDate toDate, final String groupSpan){
        if(!activeRequest) {
            GetHistoryEnergyDataRequest request;
            loadingView.setVisibility(View.VISIBLE);
            activeRequest = true;
            try {
                request = new GetHistoryEnergyDataRequest(fromDate, toDate, groupSpan);

                APIClient apiClient = APIClient.getInstance();
                apiClient.sendRequest(request,
                        new ResponseListener<GetHistoryEnergyDataRequest.HistoryEnergyDataModel>() {
                            @Override
                            public void onSuccess(GetHistoryEnergyDataRequest.HistoryEnergyDataModel data) {
                                String production = String.format(Locale.US, "%.2f", data.getTotalEnergy().getProduction());
                                String consumption = String.format(Locale.US, "%.2f", data.getTotalEnergy().getConsumption());
                                String import_ = String.format(Locale.US, "%.2f", data.getTotalEnergy().getImport_());
                                String export = String.format(Locale.US, "%.2f", data.getTotalEnergy().getExport());
                                String store = String.format(Locale.US, "%.2f", data.getTotalEnergy().getStore());
                                String use = String.format(Locale.US, "%.2f", data.getTotalEnergy().getUse());

                                productionTextView.setText(production);
                                consumptionTextView.setText(consumption);
                                importTextView.setText(import_);
                                exportTextView.setText(export);
                                storeTextView.setText(store);
                                useTextView.setText(use);

                                energyTotal.put("production", production);
                                energyTotal.put("consumption", consumption);
                                energyTotal.put("import", import_);
                                energyTotal.put("export", export);
                                energyTotal.put("store", store);
                                energyTotal.put("use", use);

                                chartData.put("production", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getProduction(), "Produkcja", Color.parseColor("#78C878")));
                                chartData.put("consumption", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getConsumption(), "Zużycie", Color.parseColor("#FF5050")));
                                chartData.put("import", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getImport_(), "Pobieranie", Color.parseColor("#C88C14")));
                                chartData.put("export", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getExport(), "Oddawanie", Color.parseColor("#8C8C14")));
                                chartData.put("store", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getStore(), "Magazynowanie", Color.parseColor("#8C3C8C")));
                                chartData.put("use", new ChartData(fromDate, toDate, groupSpan, data.getGroups().getUse(), "Wykorzystanie", Color.parseColor("#DC8C00")));

                                loadingView.setVisibility(View.INVISIBLE);
                                activeRequest = false;
                            }

                            @Override
                            public void onError(APIError error) {
                                if (error.getType() != APIError.INTERNAL_SERVER_ERROR && isAdded()) {
                                    Snackbar snackbar = Snackbar
                                            .make(rootView, getString(error.getTranslationId()) + " " + error.getMessage(), Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    loadingView.setVisibility(View.INVISIBLE);
                                    activeRequest = false;
                                }
                            }
                        });
            } catch (JSONException e) {
                Snackbar snackbar = Snackbar
                        .make(rootView, "Wystąpił błąd podczas tworzenia zapytania", Snackbar.LENGTH_LONG);
                snackbar.show();
                loadingView.setVisibility(View.INVISIBLE);
                activeRequest = false;
            }
        }
    }
    private void openChartFragment(ChartData chartData){
        if(chartData != null) {
            chartFragment.drawChart(chartData, this);
            getFragmentManager().beginTransaction().hide(this).show(chartFragment).commit();
            ((MainActivity) getActivity()).setActiveFragment(chartFragment);
        }
    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();

        if(savedInstanceState != null) {
            if(fragmentManager !=null) {
                chartFragment = (ChartFragment) fragmentManager.getFragment(savedInstanceState, "chartFragment");
            }

            Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

            String json= savedInstanceState.getString("chart_data_hash_map");
            if(json != null && !json.isEmpty()) {
                chartData = gson.fromJson(json, chartDataType);
            }

            json= savedInstanceState.getString("energy_total_hash_map");
            if(json != null && !json.isEmpty()) {
                energyTotal = gson.fromJson(json, energyTotalType);
                productionTextView.setText(energyTotal.get("production"));
                consumptionTextView.setText(energyTotal.get("consumption"));
                importTextView.setText(energyTotal.get("import"));
                exportTextView.setText(energyTotal.get("export"));
                storeTextView.setText(energyTotal.get("store"));
                useTextView.setText(energyTotal.get("use"));
            }

            String jsonFromDate= savedInstanceState.getString("from_date");
            String jsonToDate = savedInstanceState.getString("to_date");
            String groupSpan = savedInstanceState.getString("group_span");
            if(jsonFromDate != null &&
                    !jsonFromDate.isEmpty() &&
                    jsonToDate != null &&
                    !jsonToDate.isEmpty() &&
                    groupSpan != null &&
                    !groupSpan.isEmpty()) {

                setInitialState(gson.fromJson(jsonFromDate, LocalDate.class),
                        gson.fromJson(jsonToDate, LocalDate.class),
                        groupSpan,
                        false);
            }
        }
        if(chartFragment == null && fragmentManager != null){
            chartFragment = new ChartFragment();
            fragmentManager.beginTransaction().add(R.id.main_container, chartFragment, "4").hide(chartFragment).commit();
        }

        if(chartData == null){
            chartData = new HashMap<>();
        }
        if(energyTotal == null){
            energyTotal = new HashMap<>();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

        outState.putString("chart_data_hash_map", gson.toJson(chartData, chartDataType));
        outState.putString("energy_total_hash_map", gson.toJson(energyTotal, energyTotalType));
        outState.putString("from_date", gson.toJson(fromDate));
        outState.putString("to_date", gson.toJson(toDate));
        outState.putString("group_span", groupSpan);

        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null && chartFragment != null)
            fragmentManager.putFragment(outState, "chartFragment", chartFragment);
    }
}
