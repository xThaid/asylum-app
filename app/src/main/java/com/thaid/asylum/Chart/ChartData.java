package com.thaid.asylum.Chart;

import org.joda.time.LocalDate;

public class ChartData{
    private LocalDate fromDate;
    private LocalDate toDate;
    private String groupSpan;
    private Double[] data;
    private String chartName;
    private int color;

    public ChartData(LocalDate fromDate,
                     LocalDate toDate,
                     String groupSpan,
                     Double[] data,
                     String chartName,
                     int color){
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.groupSpan = groupSpan;
        this.data = data;
        this.chartName = chartName;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public String getGroupSpan() {
        return groupSpan;
    }

    public Double[] getData() {
        return data;
    }

    public String getChartName() {
        return chartName;
    }
}