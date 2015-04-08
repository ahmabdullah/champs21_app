package com.champs21.schoolapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.schoolapp.R;

/**
 * Created by Tasvir on 4/8/2015.
 */
public class ProgressGraphFragment extends Fragment implements View.OnClickListener {

    private TextView subjectTextView;
    private ImageButton btnSubjectSelect;
    private ProgressBar pbGraph;
    private LinearLayout graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_graph, container, false);
        subjectTextView = (TextView) view.findViewById(R.id.tv_prog_selected_subject);
        btnSubjectSelect = (ImageButton) view.findViewById(R.id.btn_prog_select_subject);
        btnSubjectSelect.setOnClickListener(this);
        pbGraph = (ProgressBar) view.findViewById(R.id.pb_graph);
        graphView = (LinearLayout) view.findViewById(R.id.graph_view);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_prog_select_subject:

                break;
            default:
                break;
        }
    }

    private void setGraphVisibility(boolean status) {
        graphView.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        pbGraph.setVisibility(!status ? View.VISIBLE : View.INVISIBLE);
    }
}
