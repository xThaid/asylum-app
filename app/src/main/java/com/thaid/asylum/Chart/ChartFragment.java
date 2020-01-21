package com.thaid.asylum.Chart;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.GsonBuilder;
import com.thaid.asylum.MainActivity;
import com.thaid.asylum.R;
import com.thaid.asylum.api.requests.Energy.GetHistoryEnergyDataRequest;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChartFragment extends Fragment {

    Fragment previousFragment;
    LineChart chart;
    ChartData chartData;

    public ChartFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        chart = rootView.findViewById(R.id.chart);


       final Fragment thisFragment = this;

        /*rootView.findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().beginTransaction().hide(thisFragment).show(previousFragment).commit();
                ((MainActivity) getActivity()).setActiveFragment(previousFragment);
            }
        });
*/

        return rootView;
    }


    public void drawChart(ChartData chartData, Fragment previousFragment){
        this.chartData = chartData;
        this.previousFragment = previousFragment;

        final List<Entry> series = getChartEntryList(chartData);

        LineDataSet dataSet = new LineDataSet(series, chartData.getChartName());
        dataSet.setColor(chartData.getColor());
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setFillColor(chartData.getColor());
        dataSet.setFillAlpha(150);
        dataSet.setDrawFilled(true);



        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextSize(14f);
        xAxis.setValueFormatter(new ValueFormatter() {
            //Workaround, because "getFormatedValue" do not provide index of element
            @Override
            public String getFormattedValue(float value) {
                LocalDateTime dateTime = new LocalDateTime(2000, 1, 1, 0, 0);
                dateTime = dateTime.plusMinutes((int)value);
                return dateTime.toString("HH:mm", Locale.US);
            }
        });



        YAxis yAxis = chart.getAxisRight();
        yAxis.setEnabled(false);

        yAxis = chart.getAxisLeft();
        yAxis.setTextSize(13f);
        yAxis.setAxisMinimum(0f);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + " W";
            }
        });
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);

        Legend legend = chart.getLegend();

        legend.setTextSize(18f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setDrawInside(true);
        legend.setFormSize(18f);
        legend.setForm(Legend.LegendForm.LINE);

        chart.invalidate();
    }

    private List<Entry> getChartEntryList(ChartData chartData){
        List<Entry> entries = new ArrayList<>();
        switch (chartData.getGroupSpan()){
            case GetHistoryEnergyDataRequest.GROUP_SPAN_MINUTES:
                int dayToMinutesRatio = 24 * 60;
                int timeSeparation = 4;

                int groupCount = ((Days.daysBetween(chartData.getFromDate(), chartData.getToDate()).getDays() + 1) * dayToMinutesRatio) / timeSeparation;

                LocalDateTime dateTime = new LocalDateTime(
                        2000,
                        1,
                        1,
                        0,
                        0
                );
                LocalDateTime now = new LocalDateTime();
                for(int i =0; i < groupCount; i++){
                    entries.add(new Entry((float) (dateTime.getMinuteOfHour() + (dateTime.getHourOfDay() * 60) + ((dateTime.getDayOfMonth() - 1) * 24 * 60)), chartData.getData()[i].floatValue()));
                    dateTime = dateTime.plusMinutes(timeSeparation);
                    if (dateTime.compareTo(now) > 0)
                        break;
                }


                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_DAY:
                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_MONTH:
                break;
            case GetHistoryEnergyDataRequest.GROUP_SPAN_YEAR:
                break;
        }
        return entries;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            FragmentManager fragmentManager = getFragmentManager();
            if(fragmentManager != null)
                previousFragment = getFragmentManager().getFragment(savedInstanceState,"chartPreviousFragment");

            String json= savedInstanceState.getString("chart_data");
            if(json != null && !json.isEmpty()) {
                Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();
                chartData = gson.fromJson(json, ChartData.class);

                if(previousFragment !=null && chartData != null)
                    drawChart(chartData, previousFragment);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();
        String json= gson.toJson(chartData);
        outState.putString("chart_data", json);

        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null && previousFragment != null)
            fragmentManager.putFragment(outState, "chartPreviousFragment", previousFragment);
    }
}
