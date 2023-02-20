package com.example.shuttlemobile.driver.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.util.SettingsUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAccountReports extends GenericUserFragment {

    private LineChart chart;
    private TableLayout table;

    private final int COST_SUM_COLOR = R.color.purple_200;
    private final int TOTAL_LENGTH_COLOR = R.color.red;
    private final int NUM_OF_RIDES_COLOR = R.color.green;
    private ArrayList<GraphEntryDTO> entries;

    final Calendar startDateCalendar = Calendar.getInstance();
    final Calendar endDateCalendar = Calendar.getInstance();

    public static DriverAccountReports newInstance(SessionContext session) {
        DriverAccountReports fragment = new DriverAccountReports();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_account_reports, container, false);
        EditText startDate = view.findViewById(R.id.driver_report_start_date);
        setDate(startDate, startDateCalendar);
        EditText endDate = view.findViewById(R.id.driver_report_end_date);
        setDate(endDate, endDateCalendar);
        table = view.findViewById(R.id.legend_d_chart);
        view.findViewById(R.id.driver_report_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchGraphEntries(startDateCalendar.getTimeInMillis(), endDateCalendar.getTimeInMillis());

//
            }
        });
        createGraph(view);

//        setData();
        return view;
    }

    private void setDate(EditText editText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(editText, calendar);
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, 1);
                datePickerDialog.getDatePicker().setMaxDate(gc.getTimeInMillis());
                datePickerDialog.show();
            }
        });
    }

    private void updateLabel(EditText editText, Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(calendar.getTime()));
    }

    public void createGraph(View v) {
        chart = v.findViewById(R.id.driver_stats_chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
    }

    public void setData() {
        chart.invalidate();
        chart.clear();

        if (entries == null) {
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

            costSumSet = new LineDataSet(costSum, "Cost sum");
            costSumSet.setColors(new int[]{COST_SUM_COLOR}, requireContext());
            setAxis(entries);
            costSumSet.setDrawIcons(false);

            totalLengthSet = new LineDataSet(totalLength, "Total length");
            totalLengthSet.setColors(new int[]{TOTAL_LENGTH_COLOR}, requireContext());
            setAxis(entries);
            totalLengthSet.setDrawIcons(false);

            numOfRidesSet = new LineDataSet(numOfRides, "Number of rides");
            numOfRidesSet.setColors(new int[]{NUM_OF_RIDES_COLOR}, requireContext());
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

    private void calculateAndAdd() {
        double sum = entries.stream().mapToDouble(GraphEntryDTO::getCostSum).sum();
        OptionalDouble avgO = entries.stream().mapToDouble(GraphEntryDTO::getCostSum).average();
        double avg;
        if (avgO.isPresent()) {
            avg = avgO.getAsDouble();
        } else {
            avg = 0;
        }
        addRow(COST_SUM_COLOR, "Cost sum", sum, avg);

        sum = entries.stream().mapToDouble(GraphEntryDTO::getLength).sum();
        avgO = entries.stream().mapToDouble(GraphEntryDTO::getLength).average();
        if (avgO.isPresent()) {
            avg = avgO.getAsDouble();
        } else {
            avg = 0;
        }
        addRow(TOTAL_LENGTH_COLOR, "Total length", sum, avg);

        sum = entries.stream().mapToDouble(GraphEntryDTO::getNumberOfRides).sum();
        avgO = entries.stream().mapToDouble(GraphEntryDTO::getNumberOfRides).average();
        if (avgO.isPresent()) {
            avg = avgO.getAsDouble();
        } else {
            avg = 0;
        }
        addRow(NUM_OF_RIDES_COLOR, "Number of rides", sum, avg);

    }

    @SuppressLint("SetTextI18n")
    private void addRow(int colorVal, String labelText, double sum, double avg) {
        LayoutInflater inflater = getLayoutInflater();
        TableRow tr = (TableRow) inflater.inflate(R.layout.chart_data_row,
                table, false);
        table.addView(tr);

        View color = (View) tr.findViewById(R.id.color_p_chart);
        TextView tvLabel = (TextView) tr.findViewById(R.id.lbl_p_chart_data);
        TextView tvSum = (TextView) tr.findViewById(R.id.lbl_p_chart_sum);
        TextView tvAvg = (TextView) tr.findViewById(R.id.lbl_p_chart_avg);

        color.setBackgroundColor(getResources().getColor(colorVal, null));
        tvLabel.setText(labelText);
        tvSum.setText(Double.toString(sum));
        tvAvg.setText(Double.toString(avg));
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

    public void fetchGraphEntries(Long start, Long end) {
        Instant instant = Instant.ofEpochMilli(start);
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        instant = Instant.ofEpochMilli(end);
        LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));


        Long driverId = SettingsUtil.getUserJWT().getId();
        Call<ArrayList<GraphEntryDTO>> call = IRideService.service.getDriverGraphData(driverId,
                startDate.atZone(ZoneOffset.UTC).toString(),
                endDate.atZone(ZoneOffset.UTC).toString());
        call.enqueue(new Callback<ArrayList<GraphEntryDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<GraphEntryDTO>> call, Response<ArrayList<GraphEntryDTO>> response) {
                if (response.isSuccessful()) {
                    entries = response.body();
                    if (entries != null && entries.size() == 0) {
                        Toast.makeText(requireContext(), "There are no rides matching criteria", Toast.LENGTH_LONG).show();
                    }
                    if (entries != null && entries.size() > 0) {
                        setData();
                        calculateAndAdd();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GraphEntryDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
            }
        });
    }



}