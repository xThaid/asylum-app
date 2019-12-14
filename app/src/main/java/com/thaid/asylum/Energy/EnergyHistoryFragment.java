package com.thaid.asylum.Energy;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.thaid.asylum.R;
import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.Energy.GetHistoryEnergyDataRequest;

import org.json.JSONException;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EnergyHistoryFragment extends Fragment {

    Date fromDate;
    Date toDate;

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
    DatePickerDialog.OnDateSetListener dateFromSetListener;
    DatePickerDialog.OnDateSetListener dateToSetListener;
    Format formatter;
    Button loadDataButton;


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
        loadDataButton = rootView.findViewById(R.id.button6);

        fromDate = new Date();
        toDate = new Date();
        formatter = new SimpleDateFormat(GetHistoryEnergyDataRequest.DATE_PATTERN, Locale.US);

        dateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                fromDate = dateSanitation(year, month, day);
                dateFromButton.setText(formatter.format(fromDate));
            }
        };

        dateToSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toDate = dateSanitation(year, month, day);
                dateToButton.setText(formatter.format(toDate));
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
                int checkeddRadioId = groupSpanRadioGroup.getCheckedRadioButtonId();

                long timeDiff = toDate.getTime() - fromDate.getTime();
                double millisecondsToDaysRatio = 24 * 60 * 60 * 1000;
                double daysDiff = (timeDiff / millisecondsToDaysRatio) + 1;

                if(checkeddRadioId != -1 && timeDiff >= 0){
                    String groupSpan = "";

                    switch (checkeddRadioId){
                        case R.id.radioButton1:
                            if(daysDiff < 2)
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

        return rootView;
    }

    private Date dateSanitation(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Date date = cal.getTime();

        if(date.compareTo(GetHistoryEnergyDataRequest.STARTING_DATE) < 0){
            date = GetHistoryEnergyDataRequest.STARTING_DATE;
        }

        if(date.compareTo(new Date()) > 0){
            date = new Date();
        }

        return date;
    }

    private void openDateDialog(DatePickerDialog.OnDateSetListener onDateSetListener, Date startingDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingDate);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light,
                onDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.show();
    }

    public void setInitialState(Date fromDate, Date toDate, String groupSpan){
        this.fromDate = fromDate;
        this.toDate = toDate;
        dateFromButton.setText(formatter.format(fromDate));
        dateToButton.setText(formatter.format(toDate));

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
        loadData(fromDate, toDate, groupSpan);
    }

    private void loadData(Date fromDate, Date toDate, String groupSpan){
        GetHistoryEnergyDataRequest request;
        loadingView.setVisibility(View.VISIBLE);
        try {
            request = new GetHistoryEnergyDataRequest(fromDate, toDate, groupSpan);

            APIClient apiClient = APIClient.getInstance();
            apiClient.sendRequest(request,
                    new ResponseListener<GetHistoryEnergyDataRequest.HistoryEnergyDataModel>() {
                        @Override
                        public void onSuccess(GetHistoryEnergyDataRequest.HistoryEnergyDataModel data) {
                            productionTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getProduction()));
                            consumptionTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getConsumption()));
                            importTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getImport_()));
                            exportTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getExport()));
                            storeTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getStore()));
                            useTextView.setText(String.format(Locale.US, "%.2f", data.getTotalEnergy().getUse()));
                            loadingView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(APIError error) {
                            if (error.getType() != APIError.INTERNAL_SERVER_ERROR && isAdded()) {
                                Snackbar snackbar = Snackbar
                                        .make(rootView, getString(error.getTranslationId()) + " " + error.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                loadingView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        } catch (JSONException e) {
            Snackbar snackbar = Snackbar
                    .make(rootView, "Wystąpił błąd podczas tworzenia zapytania", Snackbar.LENGTH_LONG);
            snackbar.show();
            loadingView.setVisibility(View.INVISIBLE);
        }
    }
}
