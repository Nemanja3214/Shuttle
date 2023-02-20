package com.example.shuttlemobile.passenger.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.passenger.dto.GraphEntryDTO;

import java.util.List;
import java.util.OptionalDouble;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphDataTable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphDataTable extends Fragment {


    private TableLayout table;

    public static GraphDataTable newInstance() {
        GraphDataTable fragment = new GraphDataTable();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph_data_table, container, false);
        table = v.findViewById(R.id.legend_p_chart);
        List<GraphEntryDTO> entries = (List<GraphEntryDTO>) requireArguments().getSerializable(PassengerAccountStats.ENTRIES);
        calculateAndAdd(entries);
        return v;
    }

        private void calculateAndAdd(List<GraphEntryDTO> data){
        double sum = data.stream().mapToDouble(GraphEntryDTO::getCostSum).sum();
        OptionalDouble avgO = data.stream().mapToDouble(GraphEntryDTO::getCostSum).average();
        double avg;
        if(avgO.isPresent()){
            avg = avgO.getAsDouble();
        }
        else {
            avg = 0;
        }
        addRow(PassengerAccountStats.COST_SUM_COLOR, "Cost sum", sum, avg);

        sum = data.stream().mapToDouble(GraphEntryDTO::getLength).sum();
        avgO = data.stream().mapToDouble(GraphEntryDTO::getLength).average();
        if(avgO.isPresent()){
            avg = avgO.getAsDouble();
        }
        else {
            avg = 0;
        }
        addRow(PassengerAccountStats.TOTAL_LENGTH_COLOR, "Total length", sum, avg);

        sum = data.stream().mapToDouble(GraphEntryDTO::getNumberOfRides).sum();
        avgO = data.stream().mapToDouble(GraphEntryDTO::getNumberOfRides).average();
        if(avgO.isPresent()){
            avg = avgO.getAsDouble();
        }
        else {
            avg = 0;
        }
        addRow(PassengerAccountStats.NUM_OF_RIDES_COLOR, "Number of rides", sum, avg);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void addRow(int colorVal, String labelText, double sum, double avg){
        LayoutInflater inflater = getLayoutInflater();
        TableRow tr = (TableRow) inflater.inflate(R.layout.chart_data_row,
                table, false);
        table.addView(tr);

        View color =(View) tr.findViewById(R.id.color_p_chart);
        TextView tvLabel = (TextView) tr.findViewById(R.id.lbl_p_chart_data);
        TextView tvSum = (TextView) tr.findViewById(R.id.lbl_p_chart_sum);
        TextView tvAvg = (TextView) tr.findViewById(R.id.lbl_p_chart_avg);

        color.setBackgroundColor(getResources().getColor(colorVal, null));
        tvLabel.setText(labelText);
        tvSum.setText(String.format("%.2f", sum));
        tvAvg.setText( String.format("%.2f", avg));
    }
}