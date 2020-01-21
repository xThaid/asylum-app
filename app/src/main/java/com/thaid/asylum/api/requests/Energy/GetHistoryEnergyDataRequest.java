package com.thaid.asylum.api.requests.Energy;

import com.thaid.asylum.api.APIRequest;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class GetHistoryEnergyDataRequest extends APIRequest<GetHistoryEnergyDataRequest.HistoryEnergyDataModel> {

    private static final String API_ENDPOINT = "getHistoryEnergyData";

    private static final String ENERGY_TOTAL = "energy_total";
    private static final String ENERGY_GROUPED = "energy_grouped";

    private static final String PRODUCTION = "production";
    private static final String CONSUMPTION = "consumption";
    private static final String IMPORT = "import";
    private static final String EXPORT = "export";
    private static final String STORE = "store";
    private static final String USE = "use";

    private static final String FROM_DATE = "from_date";
    private static final String TO_DATE = "to_date";
    private static final String GROUP_SPAN = "group_span";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String GROUP_SPAN_YEAR = "year";
    public static final String GROUP_SPAN_MONTH = "month";
    public static final String GROUP_SPAN_DAY = "day";
    public static final String GROUP_SPAN_MINUTES = "minutes";

    public static final LocalDate STARTING_DATE = new LocalDate(2018, 5, 21);


    public GetHistoryEnergyDataRequest(LocalDate fromDate, LocalDate toDate, String groupSpan) throws JSONException{
        super(API_ENDPOINT);
        JSONObject postData = new JSONObject();
        postData.put(FROM_DATE, fromDate.toString(DATE_PATTERN, Locale.US));
        postData.put(TO_DATE, toDate.toString(DATE_PATTERN, Locale.US));
        postData.put(GROUP_SPAN, groupSpan);
        this.data = postData;
    }

    @Override
    public HistoryEnergyDataModel parseResponse(JSONObject json) throws JSONException {
        JSONObject energyTotalJson = json.getJSONObject(ENERGY_TOTAL);
        JSONObject energyGroupedJson = json.getJSONObject(ENERGY_GROUPED);
        //JSONObject records = json.getJSONObject("records");

        HistoryEnergyGroup<Double> energyTotal = new HistoryEnergyGroup<>(
                energyTotalJson.getDouble(PRODUCTION),
                energyTotalJson.getDouble(CONSUMPTION),
                energyTotalJson.getDouble(IMPORT),
                energyTotalJson.getDouble(EXPORT),
                energyTotalJson.getDouble(STORE),
                energyTotalJson.getDouble(USE));

        HistoryEnergyGroup<Double[]> energyGroup = new HistoryEnergyGroup<>(
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(PRODUCTION)),
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(CONSUMPTION)),
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(IMPORT)),
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(EXPORT)),
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(STORE)),
                JSONArrayToDoubleArray(energyGroupedJson.getJSONArray(USE))
        );

        return new HistoryEnergyDataModel(energyTotal, energyGroup);
    }

    private Double[] JSONArrayToDoubleArray(JSONArray j) throws JSONException{

        Double[] array = new Double[j.length()];

        for(int i = 0; i < j.length(); i++)
            array[i] = j.getDouble(i);

        return array;
    }

    public class HistoryEnergyGroup<I> {
        private I production;
        private I consumption;
        private I import_;
        private I export;
        private I store;
        private I use;

        public HistoryEnergyGroup(I production,
                                  I consumption,
                                  I import_,
                                  I export,
                                  I store,
                                  I use){

            this.production = production;
            this.consumption = consumption;
            this.import_ = import_;
            this.export = export;
            this.store = store;
            this.use= use;
        }

        public I getProduction(){
            return production;
        }

        public I getConsumption() {
            return consumption;
        }

        public I getImport_(){
            return import_;
        }

        public I getExport(){
            return export;
        }

        public I getStore() {
            return store;
        }

        public I getUse() {
            return use;
        }
    }

    public class HistoryEnergyDataModel{
        private HistoryEnergyGroup<Double> energyTotal;
        private HistoryEnergyGroup<Double[]> energyGroups;

        public HistoryEnergyDataModel(HistoryEnergyGroup<Double> energyTotal, HistoryEnergyGroup<Double[]> energyGroups){
            this.energyTotal = energyTotal;
            this.energyGroups = energyGroups;
        }

        public HistoryEnergyGroup<Double> getTotalEnergy(){
            return energyTotal;
        }

        public HistoryEnergyGroup<Double[]> getGroups(){
            return energyGroups;
        }
    }

}
