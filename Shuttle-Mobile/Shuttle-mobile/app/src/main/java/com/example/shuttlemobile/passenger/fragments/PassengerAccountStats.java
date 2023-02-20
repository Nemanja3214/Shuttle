package com.example.shuttlemobile.passenger.fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.util.SettingsUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerAccountStats extends Fragment{
    public static final String START = "start";
    public static final String END = "end";
    private LineChart chart;
    protected Map<Integer, Fragment> fragments = new HashMap<>();
    public static final int COST_SUM_COLOR = R.color.purple_200;
    public static final int TOTAL_LENGTH_COLOR = R.color.red;
    public static final int NUM_OF_RIDES_COLOR = R.color.green;

    public static final String ENTRIES = "entries";
    private Fragment currentFragment;
    private ArrayList<GraphEntryDTO> entries;

    public static PassengerAccountStats newInstance() {
        PassengerAccountStats fragment = new PassengerAccountStats();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeFragmentMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passenger_account_stats, container, false);
        setVisibleFragment(getDefaultFragment());
        createGraph(v);
        setData();
        return v;
    }

    public void createGraph(View v){
        chart = v.findViewById(R.id.p_chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
    }
    public void setData() {
        chart.invalidate();
        chart.clear();

        if(entries == null){
            return;
        }

        ArrayList<Entry> costSum = new ArrayList<>();
        ArrayList<Entry> totalLength = new ArrayList<>();
        ArrayList<Entry> numOfRides = new ArrayList<>();
        for (int i = (int) 0; i < entries.size(); i++) {
            costSum.add(new Entry((float) i, entries.get(i).getCostSum().floatValue()));
            totalLength.add(new Entry((float) i, entries.get(i).getLength().floatValue()));
            numOfRides.add(new Entry((float) i, entries.get(i).getNumberOfRides().floatValue()));
        }

        LineDataSet costSumSet;
        LineDataSet totalLengthSet;
        LineDataSet numOfRidesSet;

//        change if data already exists
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            costSumSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            costSumSet.setValues(costSum);

            totalLengthSet = (LineDataSet) chart.getData().getDataSetByIndex(1);
            totalLengthSet.setValues(totalLength);

            numOfRidesSet = (LineDataSet) chart.getData().getDataSetByIndex(2);
            numOfRidesSet.setValues(numOfRides);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
//        initial setting of data
        } else {
            setAxis(entries);
            costSumSet = new LineDataSet(costSum, "Cost sum");
            costSumSet.setColors(new int[] { COST_SUM_COLOR }, requireContext());
            costSumSet.setDrawIcons(false);

            totalLengthSet = new LineDataSet(totalLength, "Total length");
            totalLengthSet.setColors(new int[] { TOTAL_LENGTH_COLOR }, requireContext());
            setAxis(entries);
            totalLengthSet.setDrawIcons(false);

            numOfRidesSet = new LineDataSet(numOfRides, "Number of rides");
            numOfRidesSet.setColors(new int[] { NUM_OF_RIDES_COLOR }, requireContext());
            setAxis(entries);
            numOfRidesSet.setDrawIcons(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(costSumSet);
            dataSets.add(totalLengthSet);
            dataSets.add(numOfRidesSet);

            LineData data = new LineData(dataSets);
            data.setValueTextSize(10f);
            chart.setData(data);
            chart.invalidate();

        }
    }

    private void setAxis(ArrayList<GraphEntryDTO> entries) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        List<String> dates = entries.stream().map(entry ->entry.getTime()).collect(Collectors.toList());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
    }

    protected void initializeFragmentMap() {
        fragments.put(R.layout.fragment_date_range_picker, DateRangePicker.newInstance());
        fragments.put(R.layout.fragment_graph_data_table, GraphDataTable.newInstance());
    }

    protected final void setVisibleFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .replace(getFragmentFrameId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    public void fetchGraphEntries(Long start, Long end) {
        Instant instant = Instant.ofEpochMilli(start);
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        instant = Instant.ofEpochMilli(end);
        LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));


        Long passengerId = SettingsUtil.getUserJWT().getId();
        Call<ArrayList<GraphEntryDTO>> call = IRideService.service.getPassengerGraphData(passengerId,
                startDate.atZone(ZoneOffset.UTC).toString(),
                endDate.atZone(ZoneOffset.UTC).toString());
        call.enqueue(new Callback<ArrayList<GraphEntryDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GraphEntryDTO>> call, Response<ArrayList<GraphEntryDTO>> response) {
                if(response.isSuccessful()){
                    entries = response.body();
                    if(entries != null && entries.size() == 0){
                        Toast.makeText(requireContext(), "There are no rides matching criteria", Toast.LENGTH_LONG).show();
                    }
                    if(entries != null && entries.size() > 0) {
                        setData();
                        showTable();
                    }
                }
                else {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GraphEntryDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTable() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ENTRIES, entries);
        Fragment table = fragments.get(R.layout.fragment_graph_data_table);
        table.setArguments(bundle);
        setVisibleFragment(table);
    }

//    public void onBackPressed() {
//        FragmentManager fm = getChildFragmentManager();
//
//        if (currentFragment == getDefaultFragment()) {
////            super.onBackPressed();
//        } else {
//            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            setVisibleFragment(getDefaultFragment());
//        }
//    }

    private Fragment getDefaultFragment() {
        return fragments.get(R.layout.fragment_date_range_picker);
    }

    protected int getFragmentFrameId() {
        return R.id.container_p_stats;
    }
}